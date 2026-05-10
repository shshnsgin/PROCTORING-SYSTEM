package kz.proctoring.service;

import kz.proctoring.domain.entity.User;
import kz.proctoring.domain.enums.AuditAction;
import kz.proctoring.dto.request.LoginRequest;
import kz.proctoring.dto.request.RefreshTokenRequest;
import kz.proctoring.dto.request.RegisterRequest;
import kz.proctoring.dto.response.AuthResponse;
import kz.proctoring.exception.AppException;
import kz.proctoring.repository.UserRepository;
import kz.proctoring.security.JwtService;
import kz.proctoring.security.UserDetailsServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final UserDetailsServiceImpl userDetailsService;
    private final AuthenticationManager authenticationManager;
    private final AuditService auditService;

    @Transactional
    public AuthResponse register(RegisterRequest req) {
        if (userRepository.findByEmail(req.getEmail()).isPresent()) {
            throw new AppException("Email already in use", HttpStatus.CONFLICT);
        }

        var user = User.builder()
                .email(req.getEmail())
                .password(passwordEncoder.encode(req.getPassword()))
                .fullName(req.getFullName())
                .roles(req.getRoles())
                .active(true)
                .build();

        var saved = userRepository.save(user);

        auditService.log(saved.getId(), AuditAction.USER_CREATED,
                "User registered: " + saved.getEmail());

        var userDetails = userDetailsService.loadUserByUsername(saved.getEmail());

        return AuthResponse.builder()
                .accessToken(jwtService.generateAccessToken(userDetails))
                .refreshToken(jwtService.generateRefreshToken(userDetails))
                .email(saved.getEmail())
                .fullName(saved.getFullName())
                .roles(saved.getRoles())
                .build();
    }

    public AuthResponse login(LoginRequest req) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(req.getEmail(), req.getPassword())
        );

        var user = userRepository.findByEmail(req.getEmail())
                .orElseThrow(() -> new AppException("User not found", HttpStatus.NOT_FOUND));

        if (!user.isActive()) {
            throw new AppException("Account is disabled", HttpStatus.FORBIDDEN);
        }

        auditService.log(user.getId(), AuditAction.USER_LOGIN,
                "Login: " + req.getEmail());

        var userDetails = userDetailsService.loadUserByUsername(user.getEmail());

        return AuthResponse.builder()
                .accessToken(jwtService.generateAccessToken(userDetails))
                .refreshToken(jwtService.generateRefreshToken(userDetails))
                .email(user.getEmail())
                .fullName(user.getFullName())
                .roles(user.getRoles())
                .build();
    }

    public AuthResponse refresh(RefreshTokenRequest req) {
        String userEmail = jwtService.extractUsername(req.getRefreshToken());

        var user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new AppException("User not found", HttpStatus.NOT_FOUND));

        if (!user.isActive()) {
            throw new AppException("Account is disabled", HttpStatus.FORBIDDEN);
        }

        var userDetails = userDetailsService.loadUserByUsername(user.getEmail());

        if (!jwtService.isTokenValid(req.getRefreshToken(), userDetails)) {
            throw new AppException("Invalid refresh token", HttpStatus.UNAUTHORIZED);
        }

        return AuthResponse.builder()
                .accessToken(jwtService.generateAccessToken(userDetails))
                .refreshToken(req.getRefreshToken())
                .email(user.getEmail())
                .fullName(user.getFullName())
                .roles(user.getRoles())
                .build();
    }
}