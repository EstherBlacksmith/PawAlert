package itacademy.pawalert.domain.alert.model;

import lombok.Getter;

import java.util.UUID;

@Getter
public abstract class AbstractAlertEvent implements DomainEvent {
    private final UUID alertId;
    private final UserId userId;
    private final ChangedAt changedAt;

    protected AbstractAlertEvent(UUID alertId, UserId userId) {
        this.alertId = alertId;
        this.userId = userId;
        this.changedAt = ChangedAt.now();
    }

}
