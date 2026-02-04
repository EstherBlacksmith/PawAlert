package itacademy.pawalert.application.port.inbound;

import itacademy.pawalert.domain.alert.model.Alert;
import jakarta.transaction.Transactional;

public interface UpdateAlertUseCase {
    @Transactional
    void deleteAlertById(String alertId);

    Alert updateTitle(String alertId, String userId, String newTitle);
    Alert updateDescription(String alertId, String userId, String newDescription);

}
