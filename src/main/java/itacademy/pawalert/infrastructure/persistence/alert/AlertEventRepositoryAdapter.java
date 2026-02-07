package itacademy.pawalert.infrastructure.persistence.alert;

import aj.org.objectweb.asm.commons.Remapper;
import itacademy.pawalert.application.port.outbound.AlertEventRepositoryPort;
import itacademy.pawalert.domain.alert.model.AlertEvent;

import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
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
    public List<AlertEvent> findByAlertIdOrderByChangedAtDesc(UUID alertId) {
        return eventRepository.findByAlertIdOrderByChangedAtDesc(alertId.toString())
                .stream()
                .map(AlertEventEntity::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public Optional<AlertEvent> findLatestByAlertId(UUID alertId) {
        return eventRepository.findFirstByAlertIdOrderByChangedAtDesc(alertId.toString())
                .map(AlertEventEntity::toDomain);
    }

}
