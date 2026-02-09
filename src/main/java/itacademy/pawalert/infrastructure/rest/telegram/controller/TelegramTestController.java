package itacademy.pawalert.infrastructure.rest.telegram.controller;

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
    public ResponseEntity<Map<String, String>> sendTestMessage() {
        telegramService.sendMessage("🐕 PawAlert: ¡Hola! El bot funciona.");
        return ResponseEntity.ok(Map.of(
                "status", "success",
                "message", "Mensaje enviado"
        ));
    }

    @PostMapping("/alert")
    public ResponseEntity<Map<String, String>> sendAlert() {
        telegramService.sendAlert("Max", "Plaza Mayor", "Perdido");
        return ResponseEntity.ok(Map.of(
                "status", "success",
                "message", "Alerta enviada"
        ));
    }
}
