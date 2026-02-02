package itacademy.pawalert.infrastructure.persistence.alert;

import itacademy.pawalert.domain.alert.model.*;
import jakarta.persistence.*;
import lombok.Getter;

import java.time.LocalDateTime;

/**
 * JPA Entity for persistence AlertEvent in database.
 * Maps the domain into the table alert_events.
 */
@Getter
@Entity
@Table(name = "alert_events")
public class AlertEventEntity {

    @Id
    private String id;

    @Column(name = "event_type")
    private String eventType;

    @Column(name = "old_value")
    private String oldValue;

    @Column(name = "new_value")
    private String newValue;
    @Column(name = "previous_status")
    private String previousStatus;

    @Column(name = "new_status")
    private String newStatus;

    @Column(name = "changed_at")
    private LocalDateTime changedAt;

    @Column(name = "changed_by_user_id")
    private String changedByUserId;

    @ManyToOne
    @JoinColumn(name = "alert_id")
    private AlertEntity alert;

    public AlertEventEntity() {
    }

    //For status changes
    public AlertEventEntity(String id, String previousStatus, String newStatus,
                            LocalDateTime changedAt, String changedByUserId) {
        this.id = id;
        this.eventType = "STATUS_CHANGED";
        this.previousStatus = previousStatus;
        this.newStatus = newStatus;
        this.changedAt = changedAt;
        this.changedByUserId = changedByUserId;
    }

    //For title and description events
    public AlertEventEntity(String id, String eventType, String oldValue, String newValue,
                            LocalDateTime changedAt, String changedByUserId) {
        this.id = id;
        this.eventType = eventType;
        this.oldValue = oldValue;
        this.newValue = newValue;
        this.changedAt = changedAt;
        this.changedByUserId = changedByUserId;
    }

    // Conversion Domain -> Entity
    public static AlertEventEntity fromDomain(DomainEvent event, AlertEntity alert) {
        LocalDateTime changedAt = event.getChangedAt().value();
        String userId = event.getUserId().value();

        String previousStatus = null;
        String newStatus = null;

        if (event instanceof StatusChangedEvent statusEvent) {
            previousStatus = statusEvent.getPreviousStatus().name();
            newStatus = statusEvent.getNewStatus().name();
        }

        return new AlertEventEntity(
                event.getAlertId().toString(),
                previousStatus,
                newStatus,
                changedAt,
                userId
        );

    }

    // Conversion Entity -> Domain
    public AlertEvent toDomain() {
        StatusNames previous = previousStatus != null
                ? StatusNames.valueOf(previousStatus)
                : null;

        return AlertEvent.create(
                previous,
                StatusNames.valueOf(newStatus),
                new UserId(changedByUserId)
        );
    }
}
