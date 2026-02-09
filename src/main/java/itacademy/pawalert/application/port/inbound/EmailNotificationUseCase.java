package itacademy.pawalert.application.port.inbound;

import itacademy.pawalert.domain.alert.model.StatusNames;

import java.util.UUID;

public interface EmailNotificationUseCase {
    void notifyStatusChange(UUID alertId, StatusNames oldStatus, StatusNames newStatus);
}