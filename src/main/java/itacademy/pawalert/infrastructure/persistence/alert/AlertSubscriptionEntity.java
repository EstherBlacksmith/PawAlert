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

    // Empty constructor required by JPA/Hibernate
    public AlertSubscriptionEntity() {
    }

    public AlertSubscriptionEntity(UUID id, UUID alertId, UUID userID, boolean active, LocalDateTime subscribedAt) {
        this.id = id;
        this.alertId = alertId;
        this.userId = userID;
        this.active = active;
        this.subscribedAt = subscribedAt;
    }

    public static AlertSubscriptionEntity fromDomain(AlertSubscription subscription) {
        return new AlertSubscriptionEntity(
                subscription.getId(),
                subscription.getAlertId(),
                subscription.getUserId(),
                subscription.isActive(),
                subscription.getSubscribedAt()
        );
    }

    public AlertSubscription toDomain() {
        return new AlertSubscription(
                this.id,
                this.alertId ,
                this.userId,
                this.active,
                this.subscribedAt
        );
    }
}
