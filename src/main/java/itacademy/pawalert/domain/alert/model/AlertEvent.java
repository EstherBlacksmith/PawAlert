package itacademy.pawalert.domain.alert.model;

import lombok.Getter;

import java.time.LocalDateTime;
import java.util.UUID;

public class AlertEvent {
    @Getter
    private final UUID id;
    @Getter
    private final UUID alertId;
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
    @Getter
    private final ClosureReason closureReason;

    public AlertEvent(UUID id, EventType eventType, StatusNames previous, StatusNames newStatus,
                      String oldValue, String newValue, UUID userId, GeographicLocation location) {
        this.id = id;
        this.alertId = null;
        this.eventType = eventType;
        this.previousStatus = previous;
        this.newStatus = newStatus;
        this.oldValue = oldValue;
        this.newValue = newValue;
        this.location = location;
        this.changedAt = new ChangedAt(LocalDateTime.now());
        this.changedBy = userId;
        this.closureReason = null;
    }

    // Private constructor - uses factory method
    private AlertEvent(UUID alertId, EventType eventType, StatusNames previous, StatusNames newStatus, String oldValue, String newValue,
                       ChangedAt changedAt, UUID changedBy, GeographicLocation location, ClosureReason closureReason) {
        this.alertId = alertId;
        this.eventType = eventType;
        this.oldValue = oldValue;
        this.newValue = newValue;
        this.location = location;
        this.id = UUID.randomUUID();
        this.previousStatus = previous;
        this.newStatus = newStatus;
        this.changedAt = changedAt;
        this.changedBy = changedBy;
        this.closureReason = closureReason;
    }

    // Factory method for status events with specific timestamp
    public static AlertEvent createStatusEvent(UUID alertId, StatusNames previousStatus, StatusNames newStatus, UUID userId, GeographicLocation location, ChangedAt changedAt) {
        return new AlertEvent(alertId, EventType.STATUS_CHANGED, previousStatus, newStatus,
                null, null, changedAt, userId, location, null);
    }

    // Factory method for closure events with specific timestamp
    public static AlertEvent createClosureEvent(UUID alertId, StatusNames previousStatus, UUID userId,
                                                GeographicLocation location, ClosureReason closureReason, ChangedAt changedAt) {
        return new AlertEvent(alertId, EventType.STATUS_CHANGED, previousStatus, StatusNames.CLOSED,
                null, null, changedAt, userId, location, closureReason);
    }

    // Factory method for title events with specific timestamp
    public static AlertEvent createTitleEvent(UUID alertId, Title oldTitle, Title newTitle, UUID userId, ChangedAt changedAt) {
        return new AlertEvent(alertId, EventType.TITLE_CHANGED, null, null,
                oldTitle.getValue(), newTitle.getValue(), changedAt, userId, null, null);
    }

    // Factory method for description events with specific timestamp
    public static AlertEvent createDescriptionEvent(UUID alertId, Description oldDescription, Description newDescription, UUID userId, ChangedAt changedAt) {
        return new AlertEvent(alertId, EventType.DESCRIPTION_CHANGED, null, null,
                oldDescription.getValue(), newDescription.getValue(), changedAt, userId, null, null);
    }

    @Override
    public String toString() {
        return String.format("AlertEvent[%s: %s → %s, at=%s, by=%s]",
                id, previousStatus, newStatus, changedAt, changedBy);
    }


}