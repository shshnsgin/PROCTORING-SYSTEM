package kz.proctoring.controller;

import jakarta.validation.Valid;
import kz.proctoring.dto.request.ChangePasswordRequest;
import kz.proctoring.dto.request.UpdateEmailRequest;
import kz.proctoring.dto.response.ApiResponse;
import kz.proctoring.dto.response.UserResponse;
import kz.proctoring.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Page<UserResponse>>> getAll(Pageable pageable) {
        return ResponseEntity.ok(ApiResponse.ok(userService.getAll(pageable)));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<UserResponse>> getById(@PathVariable UUID id) {
        return ResponseEntity.ok(ApiResponse.ok(userService.getById(id)));
    }

    @GetMapping("/me")
    public ResponseEntity<ApiResponse<UserResponse>> getMe(@AuthenticationPrincipal UserDetails principal) {
        var user = userService.getByEmail(principal.getUsername());
        return ResponseEntity.ok(ApiResponse.ok(userService.getById(user.getId())));
    }

    @PatchMapping("/me/email")
    public ResponseEntity<ApiResponse<UserResponse>> updateMyEmail(
            @Valid @RequestBody UpdateEmailRequest req,
            @AuthenticationPrincipal UserDetails principal) {
        var user = userService.getByEmail(principal.getUsername());
        return ResponseEntity.ok(ApiResponse.ok(userService.updateEmail(user.getId(), req.getEmail())));
    }

    @PatchMapping("/me/password")
    public ResponseEntity<ApiResponse<Void>> changeMyPassword(
            @Valid @RequestBody ChangePasswordRequest req,
            @AuthenticationPrincipal UserDetails principal) {
        var user = userService.getByEmail(principal.getUsername());
        userService.changePassword(user.getId(), req.getCurrentPassword(), req.getNewPassword());
        return ResponseEntity.ok(ApiResponse.ok(null, "Password updated"));
    }

    @DeleteMapping("/me")
    public ResponseEntity<ApiResponse<Void>> deleteMe(@AuthenticationPrincipal UserDetails principal) {
        var user = userService.getByEmail(principal.getUsername());
        userService.deleteOwnAccount(user.getId());
        return ResponseEntity.ok(ApiResponse.ok(null, "Account deleted"));
    }

    @PatchMapping("/{id}/toggle")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<UserResponse>> toggleActive(
            @PathVariable UUID id,
            @AuthenticationPrincipal UserDetails principal) {
        var admin = userService.getByEmail(principal.getUsername());
        return ResponseEntity.ok(ApiResponse.ok(userService.toggleActive(id, admin.getId())));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Void>> delete(
            @PathVariable UUID id,
            @AuthenticationPrincipal UserDetails principal) {
        var admin = userService.getByEmail(principal.getUsername());
        userService.deleteUser(id, admin.getId());
        return ResponseEntity.ok(ApiResponse.ok(null, "User deleted"));
    }
}