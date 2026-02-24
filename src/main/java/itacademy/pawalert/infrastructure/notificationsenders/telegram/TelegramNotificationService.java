package itacademy.pawalert.infrastructure.notificationsenders.telegram;

import itacademy.pawalert.domain.notification.exception.TelegramNotificationException;
import itacademy.pawalert.domain.notification.model.NotificationFailureReason;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@Service
public class TelegramNotificationService {
    private static final Logger LOGGER = LoggerFactory.getLogger(TelegramNotificationService.class);
    
    private final RestTemplate restTemplate;
    
    @Value("${telegram.bot.token}")
    private String botToken;
    
    @Value("${telegram.bot.chat_id}")
    private String chatId;

    public TelegramNotificationService() {
        this.restTemplate = new RestTemplate();
    }

    public void sendToUser(String chatId, String message) {
        TelegramRequest.builder()
                .endpoint("sendMessage")
                .chatId(chatId)
                .message(message)
                .build()
                .execute(restTemplate, botToken);
        
        LOGGER.info("Mensaje enviado a Telegram exitosamente para chat ID: {}", maskChatId(chatId));
    }

    public void sendPhotoWithCaption(String chatId, String photoUrl, String caption) {
        if (photoUrl != null && photoUrl.startsWith("data:")) {
            LOGGER.warn("Cannot send base64 images directly via Telegram API. Image size would be too large.");
            throw new TelegramNotificationException(chatId, NotificationFailureReason.INVALID_CHAT_ID);
        }

        // Validate caption is not null or empty
        if (caption == null || caption.trim().isEmpty()) {
            LOGGER.warn("Caption is null or empty for photo notification to chat {}", maskChatId(chatId));
            caption = "Alert notification"; // Provide default caption
        }

        LOGGER.debug("Sending photo with caption: '{}' to chat {}", caption, maskChatId(chatId));

        TelegramRequest.builder()
                .endpoint("sendPhoto")
                .chatId(chatId)
                .message(caption)
                .photo(photoUrl)
                .build()
                .execute(restTemplate, botToken);
        
        LOGGER.info("Foto enviada a Telegram exitosamente para chat ID: {}", maskChatId(chatId));
    }

    public void sendToAll(List<String> chatIds, String message) {
        chatIds.stream()
                .filter(chatId -> chatId != null && !chatId.isBlank())
                .forEach(chatId -> sendToUser(chatId, message));
    }

    private String maskChatId(String chatId) {
        if (chatId == null || chatId.length() <= 5) {
            return "***";
        }
        return chatId.substring(0, 3) + "..." + chatId.substring(chatId.length() - 2);
    }
}
