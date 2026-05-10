package kz.proctoring.dto.response;

import kz.proctoring.domain.enums.Role;
import lombok.Builder;
import lombok.Data;
import java.time.LocalDateTime;
import java.util.Set;
import java.util.UUID;

@Data @Builder
public class UserResponse {
    private UUID id;
    private String email;
    private String fullName;
    private Set<Role> roles;
    private boolean active;
    private LocalDateTime createdAt;
}
