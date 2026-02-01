package itacademy.pawalert.domain.alert.model;

import lombok.Getter;

import java.time.LocalDateTime;
import java.util.UUID;

public class AlertEvent {
    @Getter
    private final UUID id;
    @Getter
    private final StatusNames previousStatus;
    @Getter
    private final StatusNames newStatus;
    @Getter
    private final ChangedAt changedAt;
    @Getter
    private final UserId changedBy;

    public AlertEvent(UUID id, StatusNames previous, StatusNames newStatus, UserId userId) {
        this.id = id;
        this.previousStatus = previous;
        this.newStatus = newStatus;
        this.changedAt = new ChangedAt(LocalDateTime.now());
        this.changedBy = userId;
    }

    // Private constructor - uses factory method
    private AlertEvent(StatusNames previous, StatusNames newStatus,
                       ChangedAt changedAt, UserId changedBy) {
        this.id = UUID.randomUUID();
        this.previousStatus = previous;
        this.newStatus = newStatus;
        this.changedAt = changedAt;
        this.changedBy = changedBy;
    }

    // Factory method for create the initial event
    public static AlertEvent initialEvent(UserId userId) {
        return new AlertEvent(null, StatusNames.OPENED, ChangedAt.now(), userId);
    }

    public static AlertEvent create(StatusNames previous, StatusNames statusNames, UserId userId) {
        return new AlertEvent(previous, statusNames, ChangedAt.now(), userId);
    }

    @Override
    public String toString() {
        return String.format("AlertEvent[%s: %s → %s, at=%s, by=%s]",
                id, previousStatus, newStatus, changedAt, changedBy);
    }

}