package kz.proctoring.repository;

import kz.proctoring.domain.entity.SessionEvent;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface SessionEventRepository extends JpaRepository<SessionEvent, UUID> {
    Page<SessionEvent> findBySessionId(UUID sessionId, Pageable pageable);
    List<SessionEvent> findBySessionIdOrderByCreatedAtAsc(UUID sessionId);
}
