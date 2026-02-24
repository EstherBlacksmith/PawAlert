package itacademy.pawalert.application.notification.port.inbound;

import itacademy.pawalert.domain.alert.model.StatusNames;

import java.util.UUID;

public interface TelegramNotificationUseCase {
    void notifyStatusChange(UUID userId, UUID alertId, StatusNames newStatus);
}
