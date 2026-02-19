package itacademy.pawalert.domain.alert.model;

import java.util.UUID;

public record AlertCreatedEvent(
        UUID alertId,
        UUID creatorId
) {
    public static AlertCreatedEvent of(UUID alertId, UUID creatorId) {
        return new AlertCreatedEvent(alertId, creatorId);
    }
}
