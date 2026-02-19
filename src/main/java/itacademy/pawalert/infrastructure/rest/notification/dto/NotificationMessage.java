package itacademy.pawalert.infrastructure.rest.notification.dto;

import java.time.LocalDateTime;
import java.util.UUID;

public record NotificationMessage(
        UUID id,
        String type,
        String title,
        String message,
        UUID alertId,
        String alertStatus,
        String petName,
        LocalDateTime timestamp
) {
    public static NotificationMessage statusChange(UUID alertId, String petName, String oldStatus, String newStatus) {
        return new NotificationMessage(
                UUID.randomUUID(),
                "ALERT_STATUS_CHANGE",
                "Alert actualized",
                String.format("The stated of %s  has changed from %s to %s", petName, oldStatus, newStatus),
                alertId,
                newStatus,
                petName,
                LocalDateTime.now()
        );
    }

    public static NotificationMessage newAlert(UUID alertId, String petName) {
        return new NotificationMessage(
                UUID.randomUUID(),
                "NEW_ALERT",
                "New alert",
                String.format("A new alert has been created for %s", petName),
                alertId,
                "OPEN",
                petName,
                LocalDateTime.now()
        );
    }
}