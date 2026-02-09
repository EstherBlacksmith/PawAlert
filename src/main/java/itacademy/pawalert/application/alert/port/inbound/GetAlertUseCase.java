package itacademy.pawalert.application.alert.port.inbound;

import itacademy.pawalert.domain.alert.model.Alert;
import itacademy.pawalert.domain.alert.model.AlertEvent;
import itacademy.pawalert.infrastructure.rest.alert.dto.AlertWithContactDTO;

import java.util.List;
import java.util.UUID;

public interface GetAlertUseCase {
    Alert getAlertById(UUID alertId);
    List<Alert> getAlertsByPetId(UUID petId);
    List<AlertEvent> getAlertHistory(UUID alertId);
    AlertWithContactDTO getAlertWithCreatorPhone(UUID alertId);
}
