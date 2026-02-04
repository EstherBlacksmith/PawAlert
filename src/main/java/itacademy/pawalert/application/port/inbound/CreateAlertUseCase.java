package itacademy.pawalert.application.port.inbound;
import itacademy.pawalert.domain.alert.model.Alert;
import itacademy.pawalert.domain.alert.model.StatusNames;

public interface CreateAlertUseCase {
    Alert createOpenedAlert(String petId, String title, String description, String userId);

}
