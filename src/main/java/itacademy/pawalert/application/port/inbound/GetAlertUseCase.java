package itacademy.pawalert.application.port.inbound;

import itacademy.pawalert.domain.alert.model.Alert;
import itacademy.pawalert.domain.alert.model.AlertEvent;

import java.util.List;

public interface GetAlertUseCase {
    Alert getAlertById(String alertId);
    List<Alert> getAlertsByPetId(String petId);
    List<AlertEvent> getAlertHistory(String alertId);


}
