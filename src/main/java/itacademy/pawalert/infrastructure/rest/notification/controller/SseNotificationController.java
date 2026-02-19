package itacademy.pawalert.infrastructure.rest.notification.controller;

import itacademy.pawalert.infrastructure.notification.sse.SseNotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@RestController
@RequestMapping("/api/notifications")
@RequiredArgsConstructor
@Slf4j
public class SseNotificationController {

    private final SseNotificationService sseNotificationService;

    @GetMapping(value = "/stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter subscribe() {
        log.info("New SSE subscription request");
        return sseNotificationService.subscribe();
    }

    @GetMapping("/connected-count")
    public int getConnectedCount() {
        return sseNotificationService.getConnectedCount();
    }
}