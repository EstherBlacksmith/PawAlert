package itacademy.pawalert.domain.alert.model;

import lombok.Getter;

import java.util.UUID;

@Getter
public abstract class AbstractAlertEvent implements DomainEvent {
    private final UUID alertId;
    private final UUID userId;
    private final ChangedAt changedAt;

    protected AbstractAlertEvent(UUID alertId, UUID userId) {
        this.alertId = alertId;
        this.userId = userId;
        this.changedAt = ChangedAt.now();
    }

}
