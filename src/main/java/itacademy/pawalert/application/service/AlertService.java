package itacademy.pawalert.application.service;

import itacademy.pawalert.application.AlertNotFoundException;
import itacademy.pawalert.domain.*;
import itacademy.pawalert.domain.exception.InvalidAlertStatusChange;
import itacademy.pawalert.infrastructure.persistence.AlertEntity;
import itacademy.pawalert.infrastructure.persistence.AlertEventEntity;
import itacademy.pawalert.infrastructure.persistence.AlertEventRepository;
import itacademy.pawalert.infrastructure.persistence.AlertRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class AlertService {
    private final AlertRepository alertRepository;
    private final AlertEventRepository eventRepository;

    public AlertService(AlertRepository alertRepository, AlertEventRepository eventRepository) {
        this.alertRepository = alertRepository;
        this.eventRepository = eventRepository;
    }

    @Transactional
    public Alert createOpenedAlert(String petId, String tittleString, String descriptionString, String userId) {

        Tittle tittle = new Tittle(tittleString);
        Description description = new Description(descriptionString);
        Alert alert = new Alert( UUID.fromString(petId), new UserId(userId), tittle, description);
        UserId creatorId = new UserId(userId);

        //New event
        AlertEvent initialEvent = AlertEvent.initialEvent(creatorId);

        //Persist the object
        AlertEntity entity = alert.toEntity();
        AlertEntity savedAlert = alertRepository.save(entity);

        //Persist the event
        AlertEventEntity eventEntity = AlertEventEntity.fromDomain(initialEvent, savedAlert);
        eventRepository.save(eventEntity);

        return savedAlert.toDomain();
    }

    public List<Alert> findAllByPetId(String petId) {
        return alertRepository.findAllByPetId(petId)
                .stream().map(AlertEntity::toDomain)
                .filter(alert -> alert.getPetId().toString().equals(petId))
                .toList();
    }

    public Alert findById(String alertId){
        return alertRepository.findById(alertId)
                .map(AlertEntity::toDomain)
                .orElseThrow(() -> new AlertNotFoundException("Alert not found: " + alertId));
    }

    public List<AlertEvent> getAlertHistory(String alertId) {
        return eventRepository.findByAlertIdOrderByChangedAtDesc(alertId)
                .stream()
                .map(AlertEventEntity::toDomain)
                .toList();
    }

    @Transactional
    public Alert markAsSeen(String alertId, String userId) {
        return changeStatus(alertId, StatusNames.SEEN, userId);
    }

    @Transactional
    public Alert markAsSafe(String alertId, String userId) {
        return changeStatus(alertId, StatusNames.SAFE, userId);
    }

    @Transactional
    public Alert close(String alertId, String userId) {
        return changeStatus(alertId, StatusNames.CLOSED, userId);
    }

    @Transactional
    public Alert changeStatus(String alertId, StatusNames newStatus, String userId) {

        AlertEntity alertEntity = alertRepository.findById(alertId)
                .orElseThrow(() -> InvalidAlertStatusChange.alertNotFound(alertId));

        Alert alert = alertEntity.toDomain();

        if (alert.currentStatus().getStatusName().equals(StatusNames.CLOSED)) {
            throw InvalidAlertStatusChange.alreadyClosed(alertId);
        }

        StatusNames previousStatus = alert.currentStatus().getStatusName();

        // Changing the status in the domain
        switch (newStatus) {
            case SEEN -> alert.seen();
            case SAFE -> alert.safe();
            case CLOSED -> alert.closed();
            case OPENED -> alert.open();
        }

        // New event
        AlertEvent event = AlertEvent.create(
                previousStatus,
                newStatus,
                new UserId(userId)
        );

        // Persist changes
        AlertEntity updatedAlert = alertRepository.save(alert.toEntity());

        // Persist event
        AlertEventEntity eventEntity = AlertEventEntity.fromDomain(event, updatedAlert);
        eventRepository.save(eventEntity);

        return updatedAlert.toDomain();
    }


}
