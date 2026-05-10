package kz.proctoring.kafka;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProctoringEvent {
    private UUID studentId;
    private UUID sessionId;
    private String eventType;
    private String severity;

    @Builder.Default
    private LocalDateTime timestamp = LocalDateTime.now();

    public ProctoringEvent(UUID studentId, UUID sessionId, String eventType, String severity) {
        this.studentId = studentId;
        this.sessionId = sessionId;
        this.eventType = eventType;
        this.severity = severity;
        this.timestamp = LocalDateTime.now();
    }
}