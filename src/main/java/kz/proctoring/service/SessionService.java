package kz.proctoring.service;

import kz.proctoring.domain.entity.ExamSession;
import kz.proctoring.domain.entity.SessionEvent;
import kz.proctoring.domain.enums.*;
import kz.proctoring.dto.request.SessionEventRequest;
import kz.proctoring.dto.response.*;
import kz.proctoring.exception.AppException;
import kz.proctoring.kafka.ProctoringEvent;
import kz.proctoring.mapper.SessionEventMapper;
import kz.proctoring.mapper.SessionMapper;
import kz.proctoring.repository.ExamSessionRepository;
import kz.proctoring.repository.SessionEventRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class SessionService {

    private final ExamSessionRepository sessionRepository;
    private final SessionEventRepository eventRepository;
    private final ExamService examService;
    private final UserService userService;
    private final AuditService auditService;
    private final SessionMapper sessionMapper;
    private final SessionEventMapper eventMapper;
    private final KafkaTemplate<String, ProctoringEvent> kafkaTemplate;

    @Transactional
    public SessionResponse startSession(UUID examId, UUID studentId) {
        var exam = examService.findById(examId);
        if (!exam.isActive()) throw new AppException("Exam is not active", HttpStatus.BAD_REQUEST);

        sessionRepository.findByStudentIdAndStatus(studentId, SessionStatus.ACTIVE)
                .ifPresent(s -> { throw new AppException("Student already has active session", HttpStatus.CONFLICT); });

        var student = userService.findById(studentId);
        var session = new ExamSession();
        session.setExam(exam);
        session.setStudent(student);
        session.setStatus(SessionStatus.ACTIVE);
        session.setStartedAt(LocalDateTime.now());
        session.setViolationCount(0);

        var saved = sessionRepository.save(session);
        auditService.log(studentId, AuditAction.SESSION_STARTED, "Session started for exam: " + examId);
        return sessionMapper.toResponse(saved);
    }

    @Transactional
    public SessionResponse endSession(UUID sessionId, UUID studentId) {
        var session = findSessionById(sessionId);
        if (!session.getStudent().getId().equals(studentId))
            throw new AppException("Access denied", HttpStatus.FORBIDDEN);
        if (session.getStatus() != SessionStatus.ACTIVE)
            throw new AppException("Session is not active", HttpStatus.BAD_REQUEST);

        session.setStatus(SessionStatus.COMPLETED);
        session.setEndedAt(LocalDateTime.now());
        var saved = sessionRepository.save(session);
        auditService.log(studentId, AuditAction.SESSION_ENDED, "Session ended: " + sessionId);
        return sessionMapper.toResponse(saved);
    }

    @Transactional
    public SessionEventResponse recordEvent(UUID sessionId, UUID studentId, SessionEventRequest req) {
        var session = findSessionById(sessionId);
        if (!session.getStudent().getId().equals(studentId))
            throw new AppException("Access denied", HttpStatus.FORBIDDEN);
        if (session.getStatus() != SessionStatus.ACTIVE)
            throw new AppException("Session is not active", HttpStatus.BAD_REQUEST);

        var severity = resolveSeverity(req.getEventType());
        var event = new SessionEvent();
        event.setSession(session);
        event.setEventType(req.getEventType());
        event.setSeverity(severity);
        event.setDescription(req.getDescription());
        event.setScreenshotUrl(req.getScreenshotUrl());
        eventRepository.save(event);

        if (severity == EventSeverity.HIGH || severity == EventSeverity.CRITICAL) {
            session.setViolationCount(session.getViolationCount() + 1);
        }
        if (session.getViolationCount() >= 5) {
            session.setStatus(SessionStatus.VIOLATED);
            session.setNotes("Auto-terminated due to violations");
        }
        sessionRepository.save(session);

        try {
            kafkaTemplate.send("proctoring-events",
                    new ProctoringEvent(studentId, sessionId, req.getEventType().name(), severity.name()));
        } catch (Exception e) {
            log.warn("Kafka unavailable, event not published: {}", e.getMessage());
        }

        return eventMapper.toResponse(event);
    }

    public Page<SessionResponse> getAll(Pageable pageable) {
        return sessionRepository.findAllByOrderByCreatedAtDesc(pageable).map(sessionMapper::toResponse);
    }

    public Page<SessionResponse> getByStudent(UUID studentId, Pageable pageable) {
        return sessionRepository.findByStudentId(studentId, pageable).map(sessionMapper::toResponse);
    }

    public SessionResponse getById(UUID sessionId) {
        return sessionMapper.toResponse(findSessionById(sessionId));
    }

    public Page<SessionEventResponse> getEvents(UUID sessionId, Pageable pageable) {
        return eventRepository.findBySessionId(sessionId, pageable).map(eventMapper::toResponse);
    }

    public SessionReportResponse getReport(UUID sessionId, UUID requesterId) {
        var session = findSessionById(sessionId);
        auditService.log(requesterId, AuditAction.REPORT_VIEWED, "Report viewed for session: " + sessionId);
        var events = eventRepository.findBySessionIdOrderByCreatedAtAsc(session.getId());
        return SessionReportResponse.builder()
                .sessionId(session.getId())
                .examTitle(session.getExam().getTitle())
                .studentName(session.getStudent().getFullName())
                .status(session.getStatus())
                .startedAt(session.getStartedAt())
                .endedAt(session.getEndedAt())
                .violationCount(session.getViolationCount())
                .events(events.stream().map(eventMapper::toResponse).toList())
                .build();
    }

    public ExamSession findSessionById(UUID id) {
        return sessionRepository.findById(id)
                .orElseThrow(() -> new AppException("Session not found: " + id, HttpStatus.NOT_FOUND));
    }

    private EventSeverity resolveSeverity(EventType type) {
        return switch (type) {
            case PHONE_DETECTED, MULTIPLE_FACES -> EventSeverity.CRITICAL;
            case FACE_NOT_DETECTED, BROWSER_MINIMIZE -> EventSeverity.HIGH;
            case TAB_SWITCH, COPY_PASTE -> EventSeverity.MEDIUM;
            case HEAD_TURN, NOISE_DETECTED -> EventSeverity.LOW;
            default -> throw new IllegalArgumentException("Unknown event type: " + type);
        };
    }
}
