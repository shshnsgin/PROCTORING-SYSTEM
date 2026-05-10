package kz.proctoring.service;

import kz.proctoring.domain.entity.Exam;
import kz.proctoring.domain.enums.AuditAction;
import kz.proctoring.dto.request.ExamRequest;
import kz.proctoring.dto.response.ExamResponse;
import kz.proctoring.exception.AppException;
import kz.proctoring.mapper.ExamMapper;
import kz.proctoring.repository.ExamRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ExamService {

    private final ExamRepository examRepository;
    private final AuditService auditService;
    private final ExamMapper examMapper;

    public Page<ExamResponse> getAll(Pageable pageable) {
        return examRepository.findAllByOrderByCreatedAtDesc(pageable).map(examMapper::toResponse);
    }

    public ExamResponse getById(UUID id) {
        return examMapper.toResponse(findById(id));
    }

    public Exam findById(UUID id) {
        return examRepository.findById(id)
                .orElseThrow(() -> new AppException("Exam not found: " + id, HttpStatus.NOT_FOUND));
    }

    @Transactional
    public ExamResponse create(ExamRequest req, UUID adminId) {
        var exam = examMapper.toEntity(req);
        exam.setActive(true);
        var saved = examRepository.save(exam);
        auditService.log(adminId, AuditAction.EXAM_CREATED, "Exam created: " + saved.getTitle());
        return examMapper.toResponse(saved);
    }

    @Transactional
    public ExamResponse update(UUID id, ExamRequest req, UUID adminId) {
        var exam = findById(id);
        examMapper.updateEntity(req, exam);
        var saved = examRepository.save(exam);
        auditService.log(adminId, AuditAction.EXAM_UPDATED, "Exam updated: " + id);
        return examMapper.toResponse(saved);
    }

    @Transactional
    public void delete(UUID id, UUID adminId) {
        var exam = findById(id);
        exam.setActive(false);
        examRepository.save(exam);
        auditService.log(adminId, AuditAction.EXAM_DELETED, "Exam deactivated: " + id);
    }
}
