package itacademy.pawalert.infrastructure.rest.telegram.controller;

import itacademy.pawalert.domain.user.model.TelegramChatId;
import itacademy.pawalert.infrastructure.notification.telegram.TelegramNotificationService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
        String targetChatId = (chatId != null && !chatId.isBlank()) ? chatId : "prueba";
        TelegramChatId telegramChatId = TelegramChatId.of(targetChatId);
        telegramService.sendToUser(telegramChatId.value(),"🐕 PawAlert: ¡Hola! El bot funciona.");
        return ResponseEntity.ok(Map.of(
                "status", "success",
                "message", "Mensaje enviado a " + targetChatId
        ));
    }

    @PostMapping("/alert")
    public ResponseEntity<Map<String, String>> sendAlert() {
        TelegramChatId telegramChatId = TelegramChatId.of("prueba");
        telegramService.sendToUser(telegramChatId.value(),"Max,Plaza Mayor,Perdido");
        return ResponseEntity.ok(Map.of(
                "status", "success",
                "message", "Alerta enviada"
        ));
    }
}
