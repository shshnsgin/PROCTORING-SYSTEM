package kz.proctoring.dto.request;

import jakarta.validation.constraints.NotNull;
import kz.proctoring.domain.enums.EventType;
import lombok.Data;

@Data
public class SessionEventRequest {
    @NotNull
    private EventType eventType;

    private String description;
    private String screenshotUrl;
}
