package itacademy.pawalert.domain.alert.model;

import java.time.LocalDateTime;
import java.util.UUID;

public record AlertSubscription(UUID id, UUID alertId, UUID userId, LocalDateTime subscribedAt) {
    public AlertSubscription(UUID alertId, UUID userId) {
        this(UUID.randomUUID(), alertId, userId, LocalDateTime.now());
    }

    // Factory method - sin NotificationChannel
    public static AlertSubscription create(UUID alertId, UUID userId) {
        if (alertId == null) {
            throw new IllegalArgumentException("Alert ID cannot be null");
        }
        if (userId == null) {
            throw new IllegalArgumentException("User ID cannot be null");
        }
        return new AlertSubscription(alertId, userId);
    }

    @Override
    public String toString() {
        return String.format("AlertSubscription[alert=%s, user=%s, active=%s]",
                alertId, userId);
    }
}