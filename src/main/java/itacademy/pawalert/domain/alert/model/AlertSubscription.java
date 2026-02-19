package itacademy.pawalert.domain.alert.model;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
public class AlertSubscription {
    private final UUID id;
    private final UUID alertId;
    private final UUID userId;
    private final LocalDateTime subscribedAt;

    public AlertSubscription(UUID alertId, UUID userId) {
        this.id = UUID.randomUUID();
        this.alertId = alertId;
        this.userId = userId;
        this.subscribedAt = LocalDateTime.now();
    }

    public AlertSubscription(UUID id, UUID alertId, UUID userId, LocalDateTime subscribedAt) {
        this.id = id;
        this.alertId = alertId;
        this.userId = userId;
        this.subscribedAt = subscribedAt;
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