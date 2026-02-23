package itacademy.pawalert.infrastructure.persistence.alert;

import itacademy.pawalert.application.alert.port.outbound.AlertEventRepositoryPort;
import itacademy.pawalert.domain.alert.model.AlertEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Slf4j
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
        // Debug logging
        log.debug("[ALERT-EVENT-SAVE] Saving event for alertId={}, eventId={}, eventType={}",
                event.getAlertId(), event.getId(), event.getEventType());

        // Get the AlertEntity reference from the event's alertId
        AlertEntity alertEntity = alertRepository.getReferenceById(event.getAlertId().toString());
        AlertEventEntity entity = AlertEventEntity.fromDomain(event, alertEntity);
        AlertEventEntity saved = eventRepository.save(entity);

        log.debug("[ALERT-EVENT-SAVE] Event saved successfully: id={}", saved.getId());
        return saved.toDomain();
    }


    @Override
    public List<AlertEvent> findByAlertIdOrderByChangedAtDesc(UUID alertId) {
        log.debug("[FIND-EVENTS] Fetching events for alertId={}", alertId);
        List<AlertEvent> events = eventRepository.findByAlertIdWithAlertOrderByChangedAtDesc(alertId.toString())
                .stream()
                .map(AlertEventEntity::toDomain).toList();
        log.debug("[FIND-EVENTS] Found {} events for alertId={}", events.size(), alertId);
        return events;
    }

    @Override
    public Optional<AlertEvent> findLatestByAlertId(UUID alertId) {
        return eventRepository.findFirstByAlertIdWithAlertOrderByChangedAtDesc(alertId.toString())
                .map(AlertEventEntity::toDomain);
    }

}
