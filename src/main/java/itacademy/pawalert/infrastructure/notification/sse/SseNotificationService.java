package itacademy.pawalert.infrastructure.notification.sse;

import itacademy.pawalert.infrastructure.rest.notification.dto.NotificationMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;


@Service
@Slf4j
public class SseNotificationService {

    // List of ALL connected clients - thread-safe
    private final List<SseEmitter> emitters = new CopyOnWriteArrayList<>();

    // Timeout: 30 minutes
    private static final long SSE_TIMEOUT = 30 * 60 * 1000L;


    public SseEmitter subscribe() {
        SseEmitter emitter = new SseEmitter(SSE_TIMEOUT);

        emitter.onCompletion(() -> {
            log.debug("SSE connection completed");
            emitters.remove(emitter);
        });

        emitter.onTimeout(() -> {
            log.debug("SSE connection timed out");
            emitters.remove(emitter);
        });

        emitter.onError(e -> {
            log.debug("SSE connection error: {}", e.getMessage());
            emitters.remove(emitter);
        });

        emitters.add(emitter);
        log.info("New SSE connection. Total connected: {}", emitters.size());

        // Send initial connection confirmation
        try {
            emitter.send(SseEmitter.event()
                    .name("connected")
                    .data("{\"status\":\"connected\",\"message\":\"Connected to PawAlert notifications\"}"));
        } catch (Exception e) {
            log.error("Error sending initial SSE message", e);
            emitters.remove(emitter);
        }

        return emitter;
    }


    public void broadcast(NotificationMessage notification) {
        log.info("Broadcasting notification to {} clients: {}", emitters.size(), notification.title());

        // Remove dead emitters while broadcasting
        emitters.removeIf(emitter -> {
            try {
                emitter.send(SseEmitter.event()
                        .name("notification")
                        .data(notification));
                return false; // Keep this emitter
            } catch (Exception e) {
                log.debug("Removing dead emitter");
                return true; // Remove this emitter
            }
        });
    }

    public int getConnectedCount() {
        return emitters.size();
    }
}