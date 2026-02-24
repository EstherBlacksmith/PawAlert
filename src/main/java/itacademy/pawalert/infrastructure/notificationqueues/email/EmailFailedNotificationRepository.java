package itacademy.pawalert.infrastructure.notificationqueues.email;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Component
public class EmailFailedNotificationRepository {

    private final Map<UUID, EmailNotificationEvent> failedEvents = new ConcurrentHashMap<>();

    public void save(EmailNotificationEvent event) {
        failedEvents.put(event.eventId(), event);
        log.warn("Stored failed Email notification: eventId={}, total failed: {}",
                event.eventId(), failedEvents.size());
    }

    public List<EmailNotificationEvent> findAll() {
        return new ArrayList<>(failedEvents.values());
    }

    public void remove(UUID eventId) {
        failedEvents.remove(eventId);
    }
}
