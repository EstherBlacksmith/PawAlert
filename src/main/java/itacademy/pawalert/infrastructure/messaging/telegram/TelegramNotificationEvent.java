package itacademy.pawalert.infrastructure.messaging.telegram;

import itacademy.pawalert.domain.alert.model.StatusNames;
import itacademy.pawalert.domain.notification.model.NotificationEvent;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.UUID;

//  Event representing a Telegram notification request.
public record TelegramNotificationEvent(
        UUID eventId,
        UUID userId,
        UUID alertId,
        StatusNames newStatus,
        String chatId,
        String message,
        String photoUrl,
        LocalDateTime createdAt,
        int retryCount
) implements NotificationEvent {

    private static final long serialVersionUID = 1L;

    // Factory method to create a new event
    public static TelegramNotificationEvent create(
            UUID userId,
            UUID alertId,
            StatusNames newStatus,
            String chatId,
            String message,
            String photoUrl) {
        return new TelegramNotificationEvent(
                UUID.randomUUID(),
                userId,
                alertId,
                newStatus,
                chatId,
                message,
                photoUrl,
                LocalDateTime.now(),
                0
        );
    }

    // Create a copy with incremented retry count
    public TelegramNotificationEvent withIncrementedRetry() {
        return new TelegramNotificationEvent(
                this.eventId,
                this.userId,
                this.alertId,
                this.newStatus,
                this.chatId,
                this.message,
                this.photoUrl,
                this.createdAt,
                this.retryCount + 1
        );
    }
}
