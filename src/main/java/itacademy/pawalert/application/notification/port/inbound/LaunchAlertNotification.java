package itacademy.pawalert.application.notification.port.inbound;

import itacademy.pawalert.domain.alert.model.StatusNames;

import java.util.UUID;

public interface LaunchAlertNotification {
    void relaunchNotification(UUID alertId);

    void notifyStatusChange(UUID userId, UUID alertId, StatusNames newStatusNames);
}
