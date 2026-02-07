package itacademy.pawalert.application.port.inbound;
import itacademy.pawalert.domain.alert.model.Alert;
import itacademy.pawalert.domain.alert.model.Description;
import itacademy.pawalert.domain.alert.model.GeographicLocation;
import itacademy.pawalert.domain.alert.model.Title;

import java.util.UUID;

public interface CreateAlertUseCase {
    Alert createOpenedAlert(UUID petId, Title title, Description description, UUID userId, GeographicLocation location);

}
