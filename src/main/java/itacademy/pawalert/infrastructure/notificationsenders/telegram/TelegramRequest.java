package itacademy.pawalert.infrastructure.notificationsenders.telegram;

import itacademy.pawalert.domain.notification.exception.TelegramNotificationException;
import itacademy.pawalert.domain.notification.model.NotificationFailureReason;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

/**
 * Fluent builder for Telegram API requests.
 * Handles request construction, execution, and exception translation.
 */
public class TelegramRequest {
    private static final Logger LOGGER = LoggerFactory.getLogger(TelegramRequest.class);
    private static final String TELEGRAM_API_BASE = "https://api.telegram.org/bot";
    
    private final String endpoint;
    private final String chatId;
    private final Map<String, Object> body;

    private TelegramRequest(Builder builder) {
        this.endpoint = builder.endpoint;
        this.chatId = builder.chatId;
        this.body = builder.body;
    }

    public static Builder builder() {
        return new Builder();
    }

    public void execute(RestTemplate restTemplate, String botToken) {
        String url = TELEGRAM_API_BASE + botToken + "/" + endpoint;
        
        LOGGER.debug("Telegram request - URL: {}", url);
        LOGGER.debug("Telegram request - Body: {}", body);
        
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        
        HttpEntity<Map<String, Object>> request = new HttpEntity<>(body, headers);
        
        try {
            ResponseEntity<String> response = restTemplate.postForEntity(url, request, String.class);
            LOGGER.debug("Telegram request successful - Response: {}", response.getBody());
        } catch (RestClientException e) {
            LOGGER.error("Telegram request failed - URL: {}, Body: {}, Error: {}", url, body, e.getMessage(), e);
            throw translateException(chatId, e);
        }
    }

    private TelegramNotificationException translateException(String chatId, RestClientException e) {
        String message = e.getMessage();
        NotificationFailureReason reason = determineFailureReason(message);
        
        LOGGER.warn("Telegram API error for chat {}: {} - {}", maskChatId(chatId), reason, message);
        
        return new TelegramNotificationException(chatId, reason, e);
    }

    private NotificationFailureReason determineFailureReason(String message) {
        if (message == null) {
            return NotificationFailureReason.UNKNOWN;
        }
        
        if (message.contains("chat not found")) {
            return NotificationFailureReason.CHAT_NOT_FOUND;
        }
        if (message.contains("Forbidden") || message.contains("bot was blocked")) {
            return NotificationFailureReason.BOT_BLOCKED;
        }
        if (message.contains("Bad Request")) {
            return NotificationFailureReason.INVALID_CHAT_ID;
        }
        if (message.contains("Connection refused") || 
            message.contains("timeout") || 
            message.contains("I/O error")) {
            return NotificationFailureReason.NETWORK_ERROR;
        }
        
        return NotificationFailureReason.UNKNOWN;
    }

    private String maskChatId(String chatId) {
        if (chatId == null || chatId.length() <= 5) {
            return "***";
        }
        return chatId.substring(0, 3) + "..." + chatId.substring(chatId.length() - 2);
    }

    /**
     * Builder for constructing Telegram requests with a fluent API.
     */
    public static class Builder {
        private String endpoint;
        private String chatId;
        private final Map<String, Object> body = new HashMap<>();

        public Builder endpoint(String endpoint) {
            this.endpoint = endpoint;
            return this;
        }

        public Builder chatId(String chatId) {
            this.chatId = chatId;
            this.body.put("chat_id", chatId);
            return this;
        }

        public Builder message(String message) {
            this.body.put("text", message);
            this.body.put("parse_mode", "HTML");
            return this;
        }

        public Builder photo(String photoUrl) {
            this.body.put("photo", photoUrl);
            this.body.put("parse_mode", "HTML");
            
            // Transfer 'text' to 'caption' for photos (Telegram API uses caption for photo messages)
            Object text = this.body.remove("text");
            if (text != null && !text.toString().trim().isEmpty()) {
                this.body.put("caption", text.toString());
            } else {
                this.body.put("caption", "Alert notification"); // Fallback caption
            }
            return this;
        }

        public TelegramRequest build() {
            return new TelegramRequest(this);
        }
    }
}
