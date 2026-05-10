package kz.proctoring.mapper;

import kz.proctoring.domain.entity.SessionEvent;
import kz.proctoring.dto.response.SessionEventResponse;
import org.mapstruct.*;

@Mapper(componentModel = "spring")
public interface SessionEventMapper {

    @Mapping(target = "sessionId", source = "session.id")
    SessionEventResponse toResponse(SessionEvent event);
}
