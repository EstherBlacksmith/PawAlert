package itacademy.pawalert.infrastructure.persistence;

import itacademy.pawalert.domain.*;
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

    public AlertEventEntity() {}

    public AlertEventEntity(String id, String previousStatus, String newStatus,
                            LocalDateTime changedAt, String changedByUserId) {
        this.id = id;
        this.previousStatus = previousStatus;
        this.newStatus = newStatus;
        this.changedAt = changedAt;
        this.changedByUserId = changedByUserId;
    }

    // Conversion Domain -> Entity
    public static AlertEventEntity fromDomain(AlertEvent event, AlertEntity alert) {
        return new AlertEventEntity(
                event.getId().toString(),
                event.getPreviousStatus() != null ? event.getPreviousStatus().name() : null,
                event.getNewStatus().name(),
                event.getChangedAt().value(),
                event.getChangedBy().value()
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
