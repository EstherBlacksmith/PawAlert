package itacademy.pawalert.application.port.outbound;

import itacademy.pawalert.domain.alert.model.AlertEvent;

import java.util.List;
import java.util.UUID;

public interface AlertEventRepositoryPort {
    AlertEvent save(AlertEvent event);

    List<AlertEvent> findByAlertIdOrderByChangedAtDesc(UUID alertId);
}
