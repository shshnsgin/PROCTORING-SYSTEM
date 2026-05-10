package kz.proctoring.mapper;

import kz.proctoring.domain.entity.User;
import kz.proctoring.dto.response.UserResponse;
import org.mapstruct.*;

@Mapper(componentModel = "spring")
public interface UserMapper {

    UserResponse toResponse(User user);
}
