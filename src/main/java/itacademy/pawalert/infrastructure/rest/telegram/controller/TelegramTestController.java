package itacademy.pawalert.infrastructure.rest.telegram.controller;

import itacademy.pawalert.domain.user.model.TelegramChatId;
import itacademy.pawalert.infrastructure.notificationsenders.telegram.TelegramNotificationService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/telegram")
public class TelegramTestController {

    private final TelegramNotificationService telegramService;

    public TelegramTestController(TelegramNotificationService telegramService) {
        this.telegramService = telegramService;
    }

    @PostMapping("/test")
    public ResponseEntity<Map<String, String>> sendTestMessage(
            @RequestParam(required = false) String chatId) {
        String targetChatId = (chatId != null && !chatId.isBlank()) ? chatId : "test";
        TelegramChatId telegramChatId = TelegramChatId.of(targetChatId);
        telegramService.sendToUser(telegramChatId.value(), "🐕 PawAlert: Hello! The bot is working.");
        return ResponseEntity.ok(Map.of(
                "status", "success",
                "message", "Message sent to " + targetChatId
        ));
    }

    @PostMapping("/alert")
    public ResponseEntity<Map<String, String>> sendAlert() {
        TelegramChatId telegramChatId = TelegramChatId.of("test");
        telegramService.sendToUser(telegramChatId.value(), "Max,Plaza Mayor,Lost");
        return ResponseEntity.ok(Map.of(
                "status", "success",
                "message", "Alert sent"
        ));
    }
}
