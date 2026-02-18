package itacademy.pawalert.domain.alert.model;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
public class AlertSubscription {
    private final UUID id;
    private final UUID alertId;
    private final UUID userId;
    private boolean active;
    private final LocalDateTime subscribedAt;

    public AlertSubscription(UUID alertId, UUID userId) {
        this.id = UUID.randomUUID();
        this.alertId = alertId;
        this.userId = userId;
        this.active = true;
        this.subscribedAt = LocalDateTime.now();
    }

    public AlertSubscription(UUID id, UUID alertId, UUID userId, boolean active, LocalDateTime subscribedAt) {
        this.id = id;
        this.alertId = alertId;
        this.active = active;
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

    public AlertSubscription cancel() {
        this.active = false;
        return this;
    }

    public AlertSubscription reactivate() {
        this.active = true;
        return this;
    }

    @Override
    public String toString() {
        return String.format("AlertSubscription[alert=%s, user=%s, active=%s]",
                alertId, userId, active);
    }
}