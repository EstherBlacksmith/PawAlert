package itacademy.pawalert.infrastructure.persistence.alert;

import itacademy.pawalert.domain.alert.model.AlertSubscription;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "alert_subscriptions")
public class AlertSubscriptionEntity {

    @Id
    @Column(name = "id", nullable = false)
    private UUID id;
    @Column(name = "user_id", nullable = false)
    private UUID userId;
    @Column(name = "subscribed_at", nullable = false)
    private LocalDateTime subscribedAt;
    @Column(name = "alert_id", nullable = false)
    private UUID alertId;

    // Empty constructor required by JPA/Hibernate
    public AlertSubscriptionEntity() {
    }

    public AlertSubscriptionEntity(UUID id, UUID alertId, UUID userId, LocalDateTime subscribedAt) {
        this.id = id;
        this.alertId = alertId;
        this.userId = userId;
        this.subscribedAt = subscribedAt;
    }

    public static AlertSubscriptionEntity fromDomain(AlertSubscription subscription) {
        return new AlertSubscriptionEntity(
                subscription.id(),
                subscription.alertId(),
                subscription.userId(),
                subscription.subscribedAt()
        );
    }

    public AlertSubscription toDomain() {
        return new AlertSubscription(
                this.id,
                this.alertId,
                this.userId,
                this.subscribedAt
        );
    }
}
