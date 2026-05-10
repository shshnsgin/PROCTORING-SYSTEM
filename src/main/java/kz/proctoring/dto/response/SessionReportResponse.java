package kz.proctoring.dto.response;

import kz.proctoring.domain.enums.SessionStatus;
import lombok.Builder;
import lombok.Data;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Data @Builder
public class SessionReportResponse {
    private UUID sessionId;
    private String examTitle;
    private String studentName;
    private String studentEmail;
    private SessionStatus status;
    private LocalDateTime startedAt;
    private LocalDateTime endedAt;
    private long durationMinutes;
    private int totalEvents;
    private int criticalEvents;
    private int highEvents;
    private int violationCount;
    private Map<String, Long> eventsByType;
    private List<SessionEventResponse> events;
}
