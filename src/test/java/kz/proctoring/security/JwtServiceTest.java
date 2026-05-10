package kz.proctoring.security;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.userdetails.User;

import java.util.Collections;

import static org.assertj.core.api.Assertions.*;

class JwtServiceTest {

    private JwtService jwtService;

    @BeforeEach
    void setUp() throws Exception {
        jwtService = new JwtService();
        var secretField = JwtService.class.getDeclaredField("secretKey");
        secretField.setAccessible(true);
        secretField.set(jwtService, "404E635266556A586E3272357538782F413F4428472B4B6250645367566B5970");

        var expField = JwtService.class.getDeclaredField("accessExpiration");
        expField.setAccessible(true);
        expField.set(jwtService, 86400000L);

        var refField = JwtService.class.getDeclaredField("refreshExpiration");
        refField.setAccessible(true);
        refField.set(jwtService, 604800000L);
    }

    @Test
    void generateAndValidateToken() {
        var userDetails = new User("test@test.kz", "pass", Collections.emptyList());
        var token = jwtService.generateAccessToken(userDetails);

        assertThat(token).isNotBlank();
        assertThat(jwtService.extractUsername(token)).isEqualTo("test@test.kz");
        assertThat(jwtService.isTokenValid(token, userDetails)).isTrue();
    }

    @Test
    void invalidTokenShouldFail() {
        var userDetails = new User("test@test.kz", "pass", Collections.emptyList());
        assertThatThrownBy(() -> jwtService.isTokenValid("invalid.token.here", userDetails))
                .isInstanceOf(Exception.class);
    }
}
