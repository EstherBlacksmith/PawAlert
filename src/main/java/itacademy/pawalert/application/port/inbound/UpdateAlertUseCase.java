package itacademy.pawalert.application.port.inbound;

import itacademy.pawalert.domain.alert.model.Alert;

public interface UpdateAlertUseCase {
    Alert updateTitle(String alertId, String userId, String newTitle);
    Alert updateDescription(String alertId, String userId, String newDescription);

}
