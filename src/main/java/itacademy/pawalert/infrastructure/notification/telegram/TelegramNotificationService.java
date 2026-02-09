package itacademy.pawalert.infrastructure.notification.telegram;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class TelegramNotificationService {
    @Value("${telegram.bot.token}")
    private String botToken;

    // Keep this for backward compatibility
    @Value("${telegram.bot.chat_id}")
    private String chatId;

    private final RestTemplate restTemplate;

    private static final Logger LOGGER = LoggerFactory.getLogger(TelegramNotificationService.class);

    public TelegramNotificationService() {
        this.restTemplate = new RestTemplate();
    }

    public void sendToUser(String chatId, String message) {
        String url = "https://api.telegram.org/bot" + botToken + "/sendMessage";

        Map<String, String> body = new HashMap<>();
        body.put("chat_id", chatId);
        body.put("text", message);
        body.put("parse_mode", "HTML");

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<Map<String, String>> request = new HttpEntity<>(body, headers);

        restTemplate.postForEntity(url, request, String.class);

        LOGGER.info("Enviando mensaje a Telegram: {}", message);
        LOGGER.info("URL: {}", url);
        LOGGER.info("Chat ID: {}", chatId);

        try {
            ResponseEntity<String> response = restTemplate.postForEntity(url, request, String.class);
            LOGGER.info("✅ Telegram message sent to {}: {} - {}", chatId, response.getStatusCode(), response.getBody());
        } catch (Exception e) {
            LOGGER.error("❌ Error sending Telegram to {}: {}", chatId, e.getMessage());
            throw e;
        }
    }


    public void sendToAll(List<String> chatIds, String message) {
        for (String chatId : chatIds) {
            if (chatId != null && !chatId.isBlank()) {
                sendToUser(chatId, message);
            }
        }
    }
}
