package itacademy.pawalert.domain;

import lombok.Getter;

import java.util.UUID;

@Getter
public class StatusChangedEvent extends AbstractAlertEvent {
    private final StatusNames previousStatus;
    private final StatusNames newStatus;

    public StatusChangedEvent(UUID alertId, UserId userId,
                              StatusNames previousStatus, StatusNames newStatus) {
        super(alertId, userId);
        this.previousStatus = previousStatus;
        this.newStatus = newStatus;
    }

    public EventType getEventType() {
        return EventType.STATUS_CHANGED;
    }

}
