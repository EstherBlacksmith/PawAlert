package itacademy.pawalert.infrastructure.persistence.alert;

import itacademy.pawalert.application.alert.port.outbound.AlertEventRepositoryPort;
import itacademy.pawalert.domain.alert.model.AlertEvent;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class AlertEventRepositoryAdapter implements AlertEventRepositoryPort {
    private final AlertEventRepository eventRepository;
    private final AlertRepository alertRepository;

    public AlertEventRepositoryAdapter(AlertEventRepository eventRepository, AlertRepository alertRepository) {
        this.eventRepository = eventRepository;
        this.alertRepository = alertRepository;
    }

    @Override
    public AlertEvent save(AlertEvent event) {
        // Get the AlertEntity reference from the event's alertId
        AlertEntity alertEntity = alertRepository.getReferenceById(event.getAlertId().toString());
        AlertEventEntity entity = AlertEventEntity.fromDomain(event, alertEntity);
        AlertEventEntity saved = eventRepository.save(entity);
        return saved.toDomain();
    }


    @Override
    public List<AlertEvent> findByAlertIdOrderByChangedAtDesc(UUID alertId) {
        return eventRepository.findByAlertIdWithAlertOrderByChangedAtDesc(alertId.toString())
                .stream()
                .map(AlertEventEntity::toDomain).toList();
    }

    @Override
    public Optional<AlertEvent> findLatestByAlertId(UUID alertId) {
        return eventRepository.findFirstByAlertIdWithAlertOrderByChangedAtDesc(alertId.toString())
                .map(AlertEventEntity::toDomain);
    }

}
