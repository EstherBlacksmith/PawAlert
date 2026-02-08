package itacademy.pawalert.application.port.inbound;

import itacademy.pawalert.domain.alert.model.Alert;
import itacademy.pawalert.domain.alert.model.StatusNames;

import java.util.UUID;

public interface RelaunchAlertNotification {
    Alert relaunchNotification(UUID alertId);

    void notifyStatusChange(UUID alertID, StatusNames oldStatusNames, StatusNames newStatusNames);
}
