package kz.proctoring.repository;

import kz.proctoring.domain.entity.Exam;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface ExamRepository extends JpaRepository<Exam, UUID> {
    Page<Exam> findAllByOrderByCreatedAtDesc(Pageable pageable);
}
