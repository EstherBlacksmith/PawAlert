package itacademy.pawalert.infrastructure.messaging.telegram;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Component
public class TelegramFailedNotificationRepository {

    private final Map<UUID, TelegramNotificationEvent> failedEvents = new ConcurrentHashMap<>();

    public void save(TelegramNotificationEvent event) {
        failedEvents.put(event.eventId(), event);
        log.warn("Stored failed Telegram notification: eventId={}, total failed: {}",
                event.eventId(), failedEvents.size());
    }

    public List<TelegramNotificationEvent> findAll() {
        return new ArrayList<>(failedEvents.values());
    }

    public void remove(UUID eventId) {
        failedEvents.remove(eventId);
    }
}
