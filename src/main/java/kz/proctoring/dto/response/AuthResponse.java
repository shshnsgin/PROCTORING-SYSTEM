package kz.proctoring.dto.response;

import kz.proctoring.domain.enums.Role;
import lombok.Builder;
import lombok.Getter;
import java.util.Set;

@Getter
@Builder
public class AuthResponse {
    private String accessToken;
    private String refreshToken;
    private String email;
    private String fullName;
    private Set<Role> roles;
}