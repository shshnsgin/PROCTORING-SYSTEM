package kz.proctoring.service;

import kz.proctoring.domain.entity.*;
import kz.proctoring.domain.enums.*;
import kz.proctoring.dto.request.SessionEventRequest;
import kz.proctoring.exception.AppException;
import kz.proctoring.mapper.SessionEventMapper;
import kz.proctoring.mapper.SessionMapper;
import kz.proctoring.repository.ExamSessionRepository;
import kz.proctoring.repository.SessionEventRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.core.KafkaTemplate;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SessionServiceTest {

    @Mock ExamSessionRepository sessionRepository;
    @Mock SessionEventRepository eventRepository;
    @Mock ExamService examService;
    @Mock UserService userService;
    @Mock AuditService auditService;
    @Mock SessionMapper sessionMapper;
    @Mock SessionEventMapper eventMapper;
    @Mock KafkaTemplate kafkaTemplate;
    @InjectMocks SessionService sessionService;

    private UUID studentId;
    private UUID examId;
    private User student;
    private Exam exam;

    @BeforeEach
    void setUp() {
        studentId = UUID.randomUUID();
        examId = UUID.randomUUID();
        student = User.builder().id(studentId).email("student@test.kz").build();
        exam = Exam.builder().id(examId).title("Test Exam").active(true).build();
    }

    @Test
    void startSession_shouldFailIfExamInactive() {
        exam.setActive(false);
        when(examService.findById(examId)).thenReturn(exam);

        assertThatThrownBy(() -> sessionService.startSession(examId, studentId))
                .isInstanceOf(AppException.class)
                .hasMessageContaining("not active");
    }

    @Test
    void startSession_shouldFailIfAlreadyActive() {
        when(examService.findById(examId)).thenReturn(exam);
        var activeSession = ExamSession.builder().status(SessionStatus.ACTIVE).build();
        when(sessionRepository.findByStudentIdAndStatus(studentId, SessionStatus.ACTIVE))
                .thenReturn(Optional.of(activeSession));

        assertThatThrownBy(() -> sessionService.startSession(examId, studentId))
                .isInstanceOf(AppException.class)
                .hasMessageContaining("active session");
    }

    @Test
    void recordEvent_criticalEventIncrementsViolationCount() {
        var session = ExamSession.builder()
                .id(UUID.randomUUID()).student(student)
                .status(SessionStatus.ACTIVE).violationCount(0).build();

        var req = new SessionEventRequest();
        req.setEventType(EventType.PHONE_DETECTED);
        req.setDescription("Phone detected");

        when(sessionRepository.findById(session.getId())).thenReturn(Optional.of(session));
        when(sessionRepository.save(any())).thenReturn(session);
        when(eventRepository.save(any())).thenReturn(new SessionEvent());
        when(eventMapper.toResponse(any())).thenReturn(null);

        sessionService.recordEvent(session.getId(), studentId, req);

        assertThat(session.getViolationCount()).isEqualTo(1);
    }
}
