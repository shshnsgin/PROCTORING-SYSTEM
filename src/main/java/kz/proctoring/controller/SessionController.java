package kz.proctoring.controller;

import jakarta.validation.Valid;
import kz.proctoring.dto.request.SessionEventRequest;
import kz.proctoring.dto.response.*;
import kz.proctoring.service.SessionService;
import kz.proctoring.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/sessions")
@RequiredArgsConstructor
public class SessionController {

    private final SessionService sessionService;
    private final UserService userService;

    @PostMapping("/start/{examId}")
    @PreAuthorize("hasRole('STUDENT')")
    public ResponseEntity<ApiResponse<SessionResponse>> start(
            @PathVariable UUID examId,
            @AuthenticationPrincipal UserDetails principal) {
        var student = userService.getByEmail(principal.getUsername());
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.ok(sessionService.startSession(examId, student.getId()), "Session started"));
    }

    @PostMapping("/{sessionId}/end")
    @PreAuthorize("hasRole('STUDENT')")
    public ResponseEntity<ApiResponse<SessionResponse>> end(
            @PathVariable UUID sessionId,
            @AuthenticationPrincipal UserDetails principal) {
        var student = userService.getByEmail(principal.getUsername());
        return ResponseEntity.ok(ApiResponse.ok(sessionService.endSession(sessionId, student.getId())));
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN','PROCTOR')")
    public ResponseEntity<ApiResponse<Page<SessionResponse>>> getAll(Pageable pageable) {
        return ResponseEntity.ok(ApiResponse.ok(sessionService.getAll(pageable)));
    }

    @GetMapping("/my")
    @PreAuthorize("hasRole('STUDENT')")
    public ResponseEntity<ApiResponse<Page<SessionResponse>>> getMy(
            @AuthenticationPrincipal UserDetails principal, Pageable pageable) {
        var student = userService.getByEmail(principal.getUsername());
        return ResponseEntity.ok(ApiResponse.ok(sessionService.getByStudent(student.getId(), pageable)));
    }

    @GetMapping("/{sessionId}")
    @PreAuthorize("hasAnyRole('ADMIN','PROCTOR')")
    public ResponseEntity<ApiResponse<SessionResponse>> getById(@PathVariable UUID sessionId) {
        return ResponseEntity.ok(ApiResponse.ok(sessionService.getById(sessionId)));
    }

    @PostMapping("/{sessionId}/events")
    @PreAuthorize("hasRole('STUDENT')")
    public ResponseEntity<ApiResponse<SessionEventResponse>> recordEvent(
            @PathVariable UUID sessionId,
            @Valid @RequestBody SessionEventRequest req,
            @AuthenticationPrincipal UserDetails principal) {
        var student = userService.getByEmail(principal.getUsername());
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.ok(sessionService.recordEvent(sessionId, student.getId(), req)));
    }

    @GetMapping("/{sessionId}/events")
    @PreAuthorize("hasAnyRole('ADMIN','PROCTOR')")
    public ResponseEntity<ApiResponse<Page<SessionEventResponse>>> getEvents(
            @PathVariable UUID sessionId, Pageable pageable) {
        return ResponseEntity.ok(ApiResponse.ok(sessionService.getEvents(sessionId, pageable)));
    }

    @GetMapping("/{sessionId}/report")
    @PreAuthorize("hasAnyRole('ADMIN','PROCTOR')")
    public ResponseEntity<ApiResponse<SessionReportResponse>> getReport(
            @PathVariable UUID sessionId,
            @AuthenticationPrincipal UserDetails principal) {
        var user = userService.getByEmail(principal.getUsername());
        return ResponseEntity.ok(ApiResponse.ok(sessionService.getReport(sessionId, user.getId())));
    }
}
