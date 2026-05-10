package kz.proctoring.service;

import kz.proctoring.domain.entity.AuditLog;
import kz.proctoring.domain.enums.AuditAction;
import kz.proctoring.dto.response.AuditLogResponse;
import kz.proctoring.repository.AuditLogRepository;
import kz.proctoring.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuditService {

    private final AuditLogRepository auditLogRepository;
    private final UserRepository userRepository;

    @Async
    public void log(UUID actorId, AuditAction action, String details) {
        try {
            var email = actorId != null
                    ? userRepository.findById(actorId).map(u -> u.getEmail()).orElse("unknown")
                    : "system";
            var entry = new AuditLog();
            entry.setActorId(actorId);
            entry.setActorEmail(email);
            entry.setAction(action);
            entry.setDetails(details);
            entry.setTimestamp(LocalDateTime.now());
            auditLogRepository.save(entry);
        } catch (Exception e) {
            log.error("Audit log failed: {}", e.getMessage());
        }
    }

    public Page<AuditLogResponse> getAll(Pageable pageable) {
        return auditLogRepository.findAllByOrderByTimestampDesc(pageable).map(this::toResponse);
    }

    public Page<AuditLogResponse> getByActor(UUID actorId, Pageable pageable) {
        return auditLogRepository.findByActorIdOrderByTimestampDesc(actorId, pageable).map(this::toResponse);
    }

    private AuditLogResponse toResponse(AuditLog log) {
        return AuditLogResponse.builder()
                .id(log.getId())
                .actorId(log.getActorId())
                .actorEmail(log.getActorEmail())
                .action(log.getAction())
                .details(log.getDetails())
                .timestamp(log.getTimestamp())
                .build();
    }
}
