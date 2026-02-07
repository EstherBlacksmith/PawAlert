package itacademy.pawalert.application.port.inbound;

import itacademy.pawalert.domain.alert.model.Alert;
import itacademy.pawalert.domain.alert.model.StatusNames;

import java.util.List;

public interface SearchAlertsUseCase {
    List<Alert> search(
            StatusNames status,
            String petName,
            String species
    );
}
