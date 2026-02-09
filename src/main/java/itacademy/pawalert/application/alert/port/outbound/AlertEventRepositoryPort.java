package itacademy.pawalert.application.alert.port.outbound;

import itacademy.pawalert.domain.alert.model.AlertEvent;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Component
public interface AlertEventRepositoryPort {
    AlertEvent save(AlertEvent event);

    List<AlertEvent> findByAlertIdOrderByChangedAtDesc(UUID alertId);

    Optional<AlertEvent> findLatestByAlertId(UUID alertId);
}
