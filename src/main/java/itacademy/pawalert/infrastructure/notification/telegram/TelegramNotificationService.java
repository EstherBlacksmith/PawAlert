package itacademy.pawalert.infrastructure.notification.telegram;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class TelegramNotificationService {
    private static final Logger LOGGER = LoggerFactory.getLogger(TelegramNotificationService.class);
    private final RestTemplate restTemplate;
    @Value("${telegram.bot.token}")
    private String botToken;
    // Keep this for backward compatibility
    @Value("${telegram.bot.chat_id}")
    private String chatId;

    public TelegramNotificationService() {
        this.restTemplate = new RestTemplate();
    }

    public void sendToUser(String chatId, String message) {
        String url = "https://api.telegram.org/bot" + botToken + "/sendMessage";

        Map<String, Object> body = new HashMap<>();
        body.put("chat_id", chatId);
        body.put("text", message);
        body.put("parse_mode", "HTML");
        body.put("disable_web_page_preview", false);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<Map<String, Object>> request = new HttpEntity<>(body, headers);

        restTemplate.postForEntity(url, request, String.class);

        LOGGER.info("Enviando mensaje a Telegram: {}", message);
        LOGGER.info("URL: {}", url);
        LOGGER.info("Chat ID: {}", chatId);
    }

    public void sendPhotoWithCaption(String chatId, String photoUrl, String caption) {
        String url = "https://api.telegram.org/bot" + botToken + "/sendPhoto";

        // Check if it's a base64 image - Telegram API may have issues with large base64
        if (photoUrl != null && photoUrl.startsWith("data:")) {
            // For base64, we need to handle differently - try to send as file upload or skip
            LOGGER.warn("Cannot send base64 images directly via Telegram API. Image size would be too large.");
            throw new RuntimeException("Base64 images not supported for Telegram API");
        }

        Map<String, Object> body = new HashMap<>();
        body.put("chat_id", chatId);
        body.put("photo", photoUrl);
        body.put("caption", caption);
        body.put("parse_mode", "HTML");

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<Map<String, Object>> request = new HttpEntity<>(body, headers);

        try {
            restTemplate.postForEntity(url, request, String.class);
            LOGGER.info("Enviando foto a Telegram exitosamente");
        } catch (Exception e) {
            LOGGER.error("Error sending photo to Telegram: {}", e.getMessage());
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
