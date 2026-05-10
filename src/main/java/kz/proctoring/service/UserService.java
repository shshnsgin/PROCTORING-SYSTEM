package kz.proctoring.service;

import kz.proctoring.domain.entity.User;
import kz.proctoring.domain.enums.AuditAction;
import kz.proctoring.dto.response.UserResponse;
import kz.proctoring.exception.AppException;
import kz.proctoring.mapper.UserMapper;
import kz.proctoring.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final AuditService auditService;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;

    public User getByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new AppException("User not found: " + email, HttpStatus.NOT_FOUND));
    }

    public User findById(UUID id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new AppException("User not found: " + id, HttpStatus.NOT_FOUND));
    }

    public Page<UserResponse> getAll(Pageable pageable) {
        return userRepository.findAllByOrderByCreatedAtDesc(pageable)
                .map(userMapper::toResponse);
    }

    public UserResponse getById(UUID id) {
        return userMapper.toResponse(findById(id));
    }

    @Transactional
    public UserResponse toggleActive(UUID userId, UUID adminId) {
        var user = findById(userId);
        user.setActive(!user.isActive());
        userRepository.save(user);
        auditService.log(adminId, AuditAction.USER_UPDATED,
                "User " + userId + " active=" + user.isActive());
        return userMapper.toResponse(user);
    }

    @Transactional
    public void deleteUser(UUID userId, UUID adminId) {
        var user = findById(userId);
        user.setActive(false);
        userRepository.save(user);
        auditService.log(adminId, AuditAction.USER_DELETED, "User deleted by admin: " + userId);
    }

    @Transactional
    public UserResponse updateEmail(UUID userId, String newEmail) {
        var user = findById(userId);

        userRepository.findByEmail(newEmail)
                .filter(existing -> !existing.getId().equals(userId))
                .ifPresent(existing -> {
                    throw new AppException("Email already in use", HttpStatus.CONFLICT);
                });

        user.setEmail(newEmail);
        userRepository.save(user);

        auditService.log(userId, AuditAction.USER_UPDATED, "User changed email");
        return userMapper.toResponse(user);
    }

    @Transactional
    public void changePassword(UUID userId, String currentPassword, String newPassword) {
        var user = findById(userId);

        if (!passwordEncoder.matches(currentPassword, user.getPassword())) {
            throw new AppException("Current password is incorrect", HttpStatus.BAD_REQUEST);
        }

        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);

        auditService.log(userId, AuditAction.USER_UPDATED, "User changed password");
    }

    @Transactional
    public void deleteOwnAccount(UUID userId) {
        var user = findById(userId);

        userRepository.delete(user);

        auditService.log(userId, AuditAction.USER_DELETED, "User deleted own account: " + userId);
    }
}