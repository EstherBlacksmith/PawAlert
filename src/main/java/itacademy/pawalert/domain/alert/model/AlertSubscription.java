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
    private NotificationChannel notificationChannel;

    public AlertSubscription(UUID alertId, UUID userId, NotificationChannel channel) {
        this.id = UUID.randomUUID();
        this.alertId = alertId;
        this.userId = userId;
        this.active = true;
        this.subscribedAt = LocalDateTime.now();
        this.notificationChannel = channel;
    }

    public AlertSubscription(UUID id, UUID alertId, UUID userId, boolean active, LocalDateTime subscribedAt,
                             NotificationChannel notificationChannel) {
        this.id = id;
        this.alertId = alertId;
        this.active = active;
        this.userId = userId;
        this.subscribedAt = subscribedAt;
        this.notificationChannel = notificationChannel;
    }




    //Factory method for nre subscriptions
    public static AlertSubscription create(UUID alertId, UUID userId) {
        return create(alertId, userId, NotificationChannel.ALL);
    }

    public static AlertSubscription create(UUID alertId, UUID userId, NotificationChannel channel) {
        // Domain validation
        if (alertId == null) {
            throw new IllegalArgumentException("Alert ID cannot be null");
        }
        if (userId == null) {
            throw new IllegalArgumentException("User ID cannot be null");
        }
        return new AlertSubscription(alertId, userId, channel);
    }

    public AlertSubscription cancel() {
        this.active = false;
        return this;
    }

    public AlertSubscription reactivate() {
        this.active = true;
        return this;
    }

    public AlertSubscription changeChannel(NotificationChannel newChannel) {
        this.notificationChannel = newChannel;
        return this;
    }

    @Override
    public String toString() {
        return String.format("AlertSubscription[alert=%s, user=%s, active=%s]",
                alertId, userId, active);
    }
}
