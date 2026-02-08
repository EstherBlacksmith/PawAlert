package itacademy.pawalert.infrastructure.persistence.alert;

import itacademy.pawalert.domain.alert.model.*;
import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "alert_subscriptions")
public class AlertSubscriptionEntity {

    @Id
    @Column(name ="id", nullable = false)
    private UUID id;
    @Column(name ="alert_id", nullable = false)
    private UUID alertId;
    @Column(name ="user_id", nullable = false)
    private UUID userId;
    @Column(name ="active", nullable = false)
    private boolean active;
    @Column(name = "subscribed_at", nullable = false)
    private LocalDateTime subscribedAt;

    @Enumerated(EnumType.STRING)
    @Column(name = "notification_channel", nullable = false)
    private NotificationChannel notificationChannel;

    //Default constructor for JPA
    public AlertSubscriptionEntity() {}

    public AlertSubscriptionEntity(UUID id, UUID alertId, UUID userID, boolean active, LocalDateTime subscribedAt,
                                   NotificationChannel notificationChannel) {
        this.id = id;
        this.alertId = alertId;
        this.userId = userID;
        this.active = active;
        this.subscribedAt = subscribedAt;
        this.notificationChannel = notificationChannel;
    }

    public static AlertSubscriptionEntity fromDomain(AlertSubscription subscription) {
        return new AlertSubscriptionEntity(
                subscription.getId(),
                subscription.getAlertId(),
                subscription.getUserId(),
                subscription.isActive(),
                subscription.getSubscribedAt(),
                subscription.getNotificationChannel()
        );


    }

    public AlertSubscription toDomain() {
        return new AlertSubscription(
                this.id,
                this.alertId ,
                this.userId,
                this.active,
                this.subscribedAt,
                this.notificationChannel
        );
    }
}
