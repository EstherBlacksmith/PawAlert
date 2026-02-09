package itacademy.pawalert.application.alert.port.inbound;

import itacademy.pawalert.domain.alert.model.Alert;
import itacademy.pawalert.domain.alert.model.GeographicLocation;
import itacademy.pawalert.domain.alert.model.StatusNames;
import jakarta.transaction.Transactional;

import java.util.UUID;

public interface UpdateAlertStatusUseCase {

    @Transactional
    Alert markAsSeen(UUID alertId, UUID userId,GeographicLocation location);

    @Transactional
    Alert markAsClosed(UUID alertId, UUID userId,GeographicLocation location);

    @Transactional
    Alert markAsSafe(UUID alertId, UUID userId, GeographicLocation location);

    @Transactional
    Alert changeStatus(UUID alertId, StatusNames newStatus, UUID userId, GeographicLocation location);
}
