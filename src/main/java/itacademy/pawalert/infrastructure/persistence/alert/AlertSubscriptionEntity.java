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
    @Column(name ="user_id", nullable = false)
    private UUID userId;
    @Column(name = "subscribed_at", nullable = false)
    private LocalDateTime subscribedAt;
    @ManyToOne
    @JoinColumn(name = "alert_id", nullable = false)
    private AlertEntity alert;

    // Empty constructor required by JPA/Hibernate
    public AlertSubscriptionEntity() {
    }

    public AlertSubscriptionEntity(UUID id, AlertEntity alert, UUID userId, LocalDateTime subscribedAt) {
        this.id = id;
        this.alert = alert;
        this.userId = userId;
        this.subscribedAt = subscribedAt;
    }

    public static AlertSubscriptionEntity fromDomain(AlertSubscription subscription, AlertEntity alert) {
        return new AlertSubscriptionEntity(
                subscription.getId(),
                alert,
                subscription.getUserId(),
                subscription.getSubscribedAt()
        );
    }

    public AlertSubscription toDomain() {
        return new AlertSubscription(
                this.id,
                UUID.fromString(this.alert.getId()),
                this.userId,
                this.subscribedAt
        );
    }
}
