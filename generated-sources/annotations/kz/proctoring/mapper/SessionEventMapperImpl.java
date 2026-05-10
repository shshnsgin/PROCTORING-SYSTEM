package kz.proctoring.mapper;

import java.util.UUID;
import javax.annotation.processing.Generated;
import kz.proctoring.domain.entity.ExamSession;
import kz.proctoring.domain.entity.SessionEvent;
import kz.proctoring.dto.response.SessionEventResponse;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2026-05-11T02:56:18+0500",
    comments = "version: 1.5.5.Final, compiler: javac, environment: Java 17.0.17 (Eclipse Adoptium)"
)
@Component
public class SessionEventMapperImpl implements SessionEventMapper {

    @Override
    public SessionEventResponse toResponse(SessionEvent event) {
        if ( event == null ) {
            return null;
        }

        SessionEventResponse.SessionEventResponseBuilder sessionEventResponse = SessionEventResponse.builder();

        sessionEventResponse.sessionId( eventSessionId( event ) );
        sessionEventResponse.id( event.getId() );
        sessionEventResponse.eventType( event.getEventType() );
        sessionEventResponse.severity( event.getSeverity() );
        sessionEventResponse.description( event.getDescription() );
        sessionEventResponse.screenshotUrl( event.getScreenshotUrl() );
        sessionEventResponse.createdAt( event.getCreatedAt() );

        return sessionEventResponse.build();
    }

    private UUID eventSessionId(SessionEvent sessionEvent) {
        if ( sessionEvent == null ) {
            return null;
        }
        ExamSession session = sessionEvent.getSession();
        if ( session == null ) {
            return null;
        }
        UUID id = session.getId();
        if ( id == null ) {
            return null;
        }
        return id;
    }
}
