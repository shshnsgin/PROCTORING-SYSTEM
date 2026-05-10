package kz.proctoring.domain.entity;

import jakarta.persistence.*;
import kz.proctoring.domain.enums.EventSeverity;
import kz.proctoring.domain.enums.EventType;
import lombok.*;
import lombok.experimental.SuperBuilder;

@Entity
@Table(name = "session_events")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class SessionEvent extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "session_id", nullable = false)
    private ExamSession session;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EventType eventType;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EventSeverity severity;

    @Column(columnDefinition = "TEXT")
    private String description;

    private String screenshotUrl;
}
