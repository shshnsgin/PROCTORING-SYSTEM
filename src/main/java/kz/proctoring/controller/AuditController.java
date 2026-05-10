package kz.proctoring.controller;

import kz.proctoring.dto.response.ApiResponse;
import kz.proctoring.dto.response.AuditLogResponse;
import kz.proctoring.service.AuditService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/audit")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class AuditController {

    private final AuditService auditService;

    @GetMapping
    public ResponseEntity<ApiResponse<Page<AuditLogResponse>>> getAll(Pageable pageable) {
        return ResponseEntity.ok(ApiResponse.ok(auditService.getAll(pageable)));
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<ApiResponse<Page<AuditLogResponse>>> getByUser(
            @PathVariable UUID userId, Pageable pageable) {
        return ResponseEntity.ok(ApiResponse.ok(auditService.getByActor(userId, pageable)));
    }
}
