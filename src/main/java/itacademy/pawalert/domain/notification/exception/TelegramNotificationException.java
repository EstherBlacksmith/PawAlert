package itacademy.pawalert.domain.notification.exception;

import itacademy.pawalert.domain.notification.model.NotificationFailureReason;
import lombok.Getter;

@Getter
public class TelegramNotificationException extends RuntimeException {
    private final String chatId;
    private final NotificationFailureReason reason;

    public TelegramNotificationException(String chatId, NotificationFailureReason reason, Throwable cause) {
        super(buildMessage(chatId, reason), cause);
        this.chatId = chatId;
        this.reason = reason;
    }

    public TelegramNotificationException(String chatId, NotificationFailureReason reason) {
        this(chatId, reason, null);
    }

    private static String buildMessage(String chatId, NotificationFailureReason reason) {
        return String.format("Telegram notification failed for chat ID %s: %s",
                maskChatId(chatId), reason);
    }

    private static String maskChatId(String chatId) {
        if (chatId == null || chatId.length() <= 5) {
            return "***";
        }
        return chatId.substring(0, 3) + "..." + chatId.substring(chatId.length() - 2);
    }

}
