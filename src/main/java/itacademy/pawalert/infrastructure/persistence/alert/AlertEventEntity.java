package itacademy.pawalert.infrastructure.persistence.alert;

import itacademy.pawalert.domain.alert.model.*;
import jakarta.persistence.*;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.UUID;

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
    @Column(name = "latitude")
    private Double latitude;
    @Column(name = "longitude")
    private Double longitude;
    @ManyToOne
    @JoinColumn(name = "alert_id")
    private AlertEntity alert;

    public AlertEventEntity() {
    }

    //For status changes
    public AlertEventEntity(String id, String previousStatus, String newStatus,
                            LocalDateTime changedAt, String changedByUserId,
                            GeographicLocation location) {
        this.id = id;
        this.eventType = "STATUS_CHANGED";
        this.previousStatus = previousStatus;
        this.newStatus = newStatus;
        this.changedAt = changedAt;
        this.changedByUserId = changedByUserId;
        this.latitude = location.latitude();
        this.longitude = location.longitude();
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
    public static AlertEventEntity fromDomain(AlertEvent event, AlertEntity alert) {
        LocalDateTime changedAt = event.getChangedAt().value();
        String userId = event.getChangedBy().toString();

        String previousStatus = event.getPreviousStatus() != null
                ? event.getPreviousStatus().name()
                : null;
        String newStatus = event.getNewStatus() != null
                ? event.getNewStatus().name()
                : null;
        String eventType = event.getEventType().name();

        // Using the correct constructor in base at the type of the event
        if (event.getOldValue() != null) {
            // TITLE_CHANGED o DESCRIPTION_CHANGED
            return new AlertEventEntity(
                    event.getId().toString(),
                    eventType,
                    event.getOldValue(),
                    event.getNewValue(),
                    changedAt,
                    userId
            );
        } else {
            // STATUS_CHANGED
            assert event.getPreviousStatus() != null;
            assert event.getNewStatus() != null;
            return new AlertEventEntity(
                    event.getId().toString(),
                    event.getPreviousStatus().name(),
                    event.getNewStatus().name(),
                    event.getChangedAt().value(),
                    event.getChangedBy().toString(),
                    event.getLocation()
            );
        }
    }

    // Conversion Entity -> Domain
    public AlertEvent toDomain() {
        // EventType from String to enum
        EventType type = EventType.valueOf(eventType);


        StatusNames previous = previousStatus != null
                ? StatusNames.valueOf(previousStatus)
                : null;


        StatusNames newStat = newStatus != null
                ? StatusNames.valueOf(newStatus)
                : null;

        GeographicLocation location = null;
        if (latitude != null && longitude != null) {
            location = GeographicLocation.of(latitude, longitude);
        }

        return switch (type) {
            case STATUS_CHANGED -> AlertEvent.createStatusEvent(previous, newStat, UUID.fromString(changedByUserId),location );
            case TITLE_CHANGED -> AlertEvent.createTitleEvent(Title.of(oldValue), Title.of(newValue), UUID.fromString(changedByUserId));
            case DESCRIPTION_CHANGED -> AlertEvent.createDescriptionEvent(Description.of(oldValue),Description.of( newValue), UUID.fromString(changedByUserId));
        };
    }

}
