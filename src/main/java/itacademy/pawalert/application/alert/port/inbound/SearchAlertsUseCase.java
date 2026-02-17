package itacademy.pawalert.application.alert.port.inbound;

import itacademy.pawalert.domain.alert.model.Alert;
import itacademy.pawalert.domain.alert.model.StatusNames;

import java.time.LocalDateTime;
import java.util.List;

public interface SearchAlertsUseCase {
    List<Alert> search(
            StatusNames status,
            String petName,
            String species,
            LocalDateTime createdFrom,
            LocalDateTime createdTo,
            LocalDateTime updatedFrom,
            LocalDateTime updatedTo
    );

    List<Alert> search();
}
