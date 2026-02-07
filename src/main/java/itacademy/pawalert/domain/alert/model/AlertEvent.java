package itacademy.pawalert.domain.alert.model;

import lombok.Getter;

import java.time.LocalDateTime;
import java.util.UUID;

public class AlertEvent {
    @Getter
    private final UUID id;
    @Getter
    private final EventType eventType;
    @Getter
    private final StatusNames previousStatus;
    @Getter
    private final StatusNames newStatus;
    @Getter
    private final String oldValue;
    @Getter
    private final String newValue;
    @Getter
    private final ChangedAt changedAt;
    @Getter
    private final UUID changedBy;
    @Getter
    private final GeographicLocation location;

    public AlertEvent(UUID id, EventType eventType, StatusNames previous, StatusNames newStatus,
                      String oldValue, String newValue, UUID userId, GeographicLocation location) {
        this.id = id;
        this.eventType = eventType;
        this.previousStatus = previous;
        this.newStatus = newStatus;
        this.oldValue = oldValue;
        this.newValue = newValue;
        this.location = location;
        this.changedAt = new ChangedAt(LocalDateTime.now());
        this.changedBy = userId;
    }

    // Private constructor - uses factory method
    private AlertEvent(EventType eventType, StatusNames previous, StatusNames newStatus, String oldValue, String newValue,
                       ChangedAt changedAt, UUID changedBy, GeographicLocation location) {
        this.eventType = eventType;
        this.oldValue = oldValue;
        this.newValue = newValue;
        this.location = location;
        this.id = UUID.randomUUID();
        this.previousStatus = previous;
        this.newStatus = newStatus;
        this.changedAt = changedAt;
        this.changedBy = changedBy;
    }

    // Factory method for create the initial event
    public static AlertEvent createStatusEvent(StatusNames previousStatus, StatusNames newStatus, UUID userId, GeographicLocation location) {
        return new AlertEvent(EventType.STATUS_CHANGED, previousStatus, newStatus,
                null, null, ChangedAt.now(), userId, location);
    }

    // Factory method for title events
    public static AlertEvent createTitleEvent(Title oldTitle, Title newTitle, UUID userId, GeographicLocation location) {
        return new AlertEvent(EventType.TITLE_CHANGED, null, null,
                oldTitle.getValue(), newTitle.getValue(), ChangedAt.now(), userId, location);
    }

    // Factory method for description events
    public static AlertEvent createDescriptionEvent(Description oldDescription, Description newDescription, UUID userId, GeographicLocation location) {
        return new AlertEvent(EventType.DESCRIPTION_CHANGED, null, null,
                oldDescription.getValue(), newDescription.getValue(), ChangedAt.now(), userId, location);
    }

    @Override
    public String toString() {
        return String.format("AlertEvent[%s: %s → %s, at=%s, by=%s]",
                id, previousStatus, newStatus, changedAt, changedBy);
    }

}