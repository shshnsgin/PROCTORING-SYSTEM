package kz.proctoring.controller;

import jakarta.validation.Valid;
import kz.proctoring.dto.request.ExamRequest;
import kz.proctoring.dto.response.ApiResponse;
import kz.proctoring.dto.response.ExamResponse;
import kz.proctoring.service.ExamService;
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
@RequestMapping("/api/exams")
@RequiredArgsConstructor
public class ExamController {

    private final ExamService examService;
    private final UserService userService;

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN','PROCTOR','TEACHER','STUDENT')")
    public ResponseEntity<ApiResponse<Page<ExamResponse>>> getAll(Pageable pageable) {
        return ResponseEntity.ok(ApiResponse.ok(examService.getAll(pageable)));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','PROCTOR','TEACHER','STUDENT')")
    public ResponseEntity<ApiResponse<ExamResponse>> getById(@PathVariable UUID id) {
        return ResponseEntity.ok(ApiResponse.ok(examService.getById(id)));
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN','TEACHER')")
    public ResponseEntity<ApiResponse<ExamResponse>> create(
            @Valid @RequestBody ExamRequest req,
            @AuthenticationPrincipal UserDetails principal) {
        var user = userService.getByEmail(principal.getUsername());
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.ok(examService.create(req, user.getId()), "Exam created"));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','TEACHER')")
    public ResponseEntity<ApiResponse<ExamResponse>> update(
            @PathVariable UUID id,
            @Valid @RequestBody ExamRequest req,
            @AuthenticationPrincipal UserDetails principal) {
        var user = userService.getByEmail(principal.getUsername());
        return ResponseEntity.ok(ApiResponse.ok(examService.update(id, req, user.getId()), "Exam updated"));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','TEACHER')")
    public ResponseEntity<ApiResponse<Void>> delete(
            @PathVariable UUID id,
            @AuthenticationPrincipal UserDetails principal) {
        var user = userService.getByEmail(principal.getUsername());
        examService.delete(id, user.getId());
        return ResponseEntity.ok(ApiResponse.ok(null, "Exam deleted"));
    }
}