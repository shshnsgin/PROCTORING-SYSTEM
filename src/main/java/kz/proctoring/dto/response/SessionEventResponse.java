package kz.proctoring.dto.response;

import kz.proctoring.domain.enums.EventSeverity;
import kz.proctoring.domain.enums.EventType;
import lombok.*;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SessionEventResponse {
    private UUID id;
    private UUID sessionId;
    private EventType eventType;
    private EventSeverity severity;
    private String description;
    private String screenshotUrl;
    private LocalDateTime createdAt;
}
