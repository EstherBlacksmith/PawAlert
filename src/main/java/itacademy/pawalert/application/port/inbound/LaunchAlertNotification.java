package itacademy.pawalert.application.port.inbound;

import itacademy.pawalert.domain.alert.model.StatusNames;

import java.util.UUID;

public interface LaunchAlertNotification {
    void relaunchNotification(UUID alertId);

    void notifyStatusChange(UUID alertID, StatusNames oldStatusNames, StatusNames newStatusNames);
}
