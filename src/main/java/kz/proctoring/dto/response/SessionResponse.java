package kz.proctoring.dto.response;

import kz.proctoring.domain.enums.SessionStatus;
import lombok.*;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SessionResponse {
    private UUID id;
    private UUID examId;
    private String examTitle;
    private UUID studentId;
    private String studentName;
    private SessionStatus status;
    private LocalDateTime startedAt;
    private LocalDateTime endedAt;
    private int violationCount;
    private String notes;
    private LocalDateTime createdAt;
}
