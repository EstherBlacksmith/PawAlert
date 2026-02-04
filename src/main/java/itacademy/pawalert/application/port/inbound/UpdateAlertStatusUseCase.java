package itacademy.pawalert.application.port.inbound;

import itacademy.pawalert.domain.alert.model.Alert;
import itacademy.pawalert.domain.alert.model.StatusNames;

public interface UpdateAlertStatusUseCase {
    Alert changeStatus(String alertId, StatusNames newStatus, String userId);

    Alert markAsSeen(String alertId, String userId);

    Alert markAsSafe(String alertId, String userId);

    Alert close(String alertId, String userId);
}
