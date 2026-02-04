package itacademy.pawalert.infrastructure.persistence.alert;

import itacademy.pawalert.application.port.outbound.AlertEventRepositoryPort;
import itacademy.pawalert.domain.alert.model.AlertEvent;

import java.util.List;

public class AlertEventRepositoryAdapter implements AlertEventRepositoryPort {
    private final AlertEventRepository eventRepository;

    public AlertEventRepositoryAdapter(AlertEventRepository eventRepository) {
        this.eventRepository = eventRepository;
    }

    @Override
    public AlertEvent save(AlertEvent event) {
        AlertEventEntity entity = AlertEventEntity.fromDomain(event, null);
        AlertEventEntity saved = eventRepository.save(entity);
        return saved.toDomain();
    }

    @Override
    public List<AlertEvent> findByAlertIdOrderByChangedAtDesc(String alertId) {
        return eventRepository.findByAlertIdOrderByChangedAtDesc(alertId)
                .stream()
                .map(AlertEventEntity::toDomain)
                .toList();
    }
}
