package kz.proctoring.kafka;

import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class ProctoringEventConsumer {

    @KafkaListener(topics = "proctoring-events", groupId = "proctoring-group")
    public void consume(ProctoringEvent event) {
        log.info("Received proctoring event: student={} session={} type={} severity={}",
                event.getStudentId(), event.getSessionId(),
                event.getEventType(), event.getSeverity());
        // Здесь можно добавить логику: уведомления, алерты, аналитику
    }
}
