package itacademy.pawalert.application.port.inbound;

import itacademy.pawalert.domain.alert.model.Alert;
import itacademy.pawalert.domain.alert.model.Description;
import itacademy.pawalert.domain.alert.model.Title;
import jakarta.transaction.Transactional;

import java.util.UUID;

public interface UpdateAlertUseCase {
    @Transactional
    void deleteAlertById(UUID alertId);

    @Transactional
    Alert updateTitle(UUID alertId, UUID userId, Title title);

    @Transactional
    Alert updateDescription(UUID alertId, UUID userId, Description description);
}
