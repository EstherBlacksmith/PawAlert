package itacademy.pawalert.application.port.inbound;

import itacademy.pawalert.domain.alert.model.Alert;
import itacademy.pawalert.domain.alert.model.StatusNames;
import jakarta.transaction.Transactional;

import java.util.UUID;

public interface UpdateAlertStatusUseCase {

    @Transactional
    Alert markAsSeen(UUID alertId, UUID userId);

    @Transactional
    Alert markAsSafe(UUID alertId, UUID userId);

    @Transactional
    Alert markAsClosed(UUID alertId, UUID userId);

    @Transactional
    Alert changeStatus(UUID alertId, StatusNames newStatus, UUID userId);
}
