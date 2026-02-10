package itacademy.pawalert.domain.alert.model;

import java.util.UUID;

public record AlertStatusChangedEvent(
        UUID alertId,
        StatusNames oldStatus,
        StatusNames newStatus
) {}
