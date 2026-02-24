package itacademy.pawalert.infrastructure.messaging.email;

import itacademy.pawalert.domain.alert.model.StatusNames;
import itacademy.pawalert.domain.notification.model.NotificationEvent;

import java.time.LocalDateTime;
import java.util.UUID;


public record EmailNotificationEvent(
        UUID eventId,
        UUID userId,
        UUID alertId,
        StatusNames newStatus,
        String email,
        String subject,
        String body,
        LocalDateTime createdAt,
        int retryCount
) implements NotificationEvent {

    private static final long serialVersionUID = 1L;

    public static EmailNotificationEvent create(
            UUID userId,
            UUID alertId,
            StatusNames newStatus,
            String email,
            String subject,
            String body) {
        return new EmailNotificationEvent(
                UUID.randomUUID(),
                userId,
                alertId,
                newStatus,
                email,
                subject,
                body,
                LocalDateTime.now(),
                0
        );
    }

    @Override
    public EmailNotificationEvent withIncrementedRetry() {
        return new EmailNotificationEvent(
                this.eventId,
                this.userId,
                this.alertId,
                this.newStatus,
                this.email,
                this.subject,
                this.body,
                this.createdAt,
                this.retryCount + 1
        );
    }
}
