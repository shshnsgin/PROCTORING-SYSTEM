package kz.proctoring.dto.response;

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
public class ExamResponse {
    private UUID id;
    private String title;
    private String description;
    private LocalDateTime scheduledAt;
    private int durationMinutes;
    private boolean active;
    private UUID createdById;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}