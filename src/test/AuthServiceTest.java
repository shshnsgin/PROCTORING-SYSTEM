package kz.proctoring.service;

import kz.proctoring.domain.enums.AuditAction;
import kz.proctoring.domain.enums.Role;
import kz.proctoring.dto.request.LoginRequest;
import kz.proctoring.dto.request.RegisterRequest;
import kz.proctoring.exception.AppException;
import kz.proctoring.repository.UserRepository;
import kz.proctoring.security.JwtService;
import kz.proctoring.security.UserDetailsServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.contains;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtService jwtService;

    @Mock
    private UserDetailsServiceImpl userDetailsService;

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private AuditService auditService;

    @InjectMocks
    private AuthService authService;

    @Test
    void register_shouldCreateUser() {
        var req = new RegisterRequest();
        req.setEmail("student@test.com");
        req.setPassword("123456");
        req.setFullName("Test Student");
        req.setRoles(Set.of(Role.ROLE_STUDENT));

        var saved = kz.proctoring.domain.entity.User.builder()
                .id(UUID.randomUUID())
                .email(req.getEmail())
                .password("encoded")
                .fullName(req.getFullName())
                .roles(req.getRoles())
                .active(true)
                .build();

        var userDetails = org.springframework.security.core.userdetails.User
                .withUsername(req.getEmail())
                .password("encoded")
                .authorities("ROLE_STUDENT")
                .build();

        when(userRepository.findByEmail(req.getEmail())).thenReturn(Optional.empty());
        when(passwordEncoder.encode(req.getPassword())).thenReturn("encoded");
        when(userRepository.save(any(kz.proctoring.domain.entity.User.class))).thenReturn(saved);
        when(userDetailsService.loadUserByUsername(req.getEmail())).thenReturn(userDetails);
        when(jwtService.generateAccessToken(userDetails)).thenReturn("access-token");
        when(jwtService.generateRefreshToken(userDetails)).thenReturn("refresh-token");

        var response = authService.register(req);

        assertNotNull(response);
        assertEquals("student@test.com", response.getEmail());
        assertEquals("access-token", response.getAccessToken());
        assertEquals("refresh-token", response.getRefreshToken());

        verify(auditService).log(
                eq(saved.getId()),
                eq(AuditAction.USER_CREATED),
                contains("User registered")
        );
    }

    @Test
    void register_shouldThrowWhenEmailExists() {
        var req = new RegisterRequest();
        req.setEmail("student@test.com");
        req.setPassword("123456");
        req.setFullName("Test Student");
        req.setRoles(Set.of(Role.ROLE_STUDENT));

        when(userRepository.findByEmail(req.getEmail()))
                .thenReturn(Optional.of(kz.proctoring.domain.entity.User.builder().build()));

        var ex = assertThrows(AppException.class, () -> authService.register(req));
        assertEquals(HttpStatus.CONFLICT, ex.getStatus());
    }

    @Test
    void login_shouldThrowWhenUserDisabled() {
        var req = new LoginRequest();
        req.setEmail("student@test.com");
        req.setPassword("123456");

        var user = kz.proctoring.domain.entity.User.builder()
                .id(UUID.randomUUID())
                .email(req.getEmail())
                .password("encoded")
                .fullName("Test Student")
                .roles(Set.of(Role.ROLE_STUDENT))
                .active(false)
                .build();

        when(userRepository.findByEmail(req.getEmail())).thenReturn(Optional.of(user));

        var ex = assertThrows(AppException.class, () -> authService.login(req));
        assertEquals(HttpStatus.FORBIDDEN, ex.getStatus());
    }
}