package kz.proctoring.repository;

import kz.proctoring.domain.entity.ExamSession;
import kz.proctoring.domain.enums.SessionStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface ExamSessionRepository extends JpaRepository<ExamSession, UUID> {
    Page<ExamSession> findAllByOrderByCreatedAtDesc(Pageable pageable);
    Page<ExamSession> findByStudentId(UUID studentId, Pageable pageable);
    Optional<ExamSession> findByStudentIdAndStatus(UUID studentId, SessionStatus status);
}
