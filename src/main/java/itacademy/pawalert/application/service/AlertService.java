package itacademy.pawalert.application.service;

import itacademy.pawalert.application.exception.AlertNotFoundException;
import itacademy.pawalert.application.exception.UnauthorizedException;
import itacademy.pawalert.domain.alert.model.*;
import itacademy.pawalert.domain.alert.service.AlertFactory;
import itacademy.pawalert.infrastructure.persistence.alert.*;
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
    public Alert createOpenedAlert(String petId, String titleString, String descriptionString, String userId) {

        Title title = new Title(titleString);
        Description description = new Description(descriptionString);
        UserId creatorId = new UserId(userId);

        Alert alert =  AlertFactory.createAlert(
                UUID.fromString(petId),
                new UserId(userId),
                title,
                description);

        //Persist the object
        AlertEntity entity = alert.toEntity();
        AlertEntity savedAlert = alertRepository.save(entity);

        AlertEventEntity event = AlertEventFactory.createStatusChangedEvent(
                alert, StatusNames.OPENED,StatusNames.OPENED,creatorId
        );

        eventRepository.save(event);

        return savedAlert.toDomain();
    }

    public List<Alert> findAllByPetId(String petId) {
        return alertRepository.findAllByPetId(petId)
                .stream().map(AlertEntity::toDomain)
                .filter(alert -> alert.getPetId().toString().equals(petId))
                .toList();
    }

    public Alert findById(String alertId) {
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
    public Alert markAsClose(String alertId, String userId) {
        return changeStatus(alertId, StatusNames.CLOSED, userId);
    }

    @Transactional
    public Alert changeStatus(String alertId, StatusNames newStatus, String userId) {

        Alert alert = findById(alertId);
        StatusNames previousStatus = alert.currentStatus().getStatusName();

        Alert alertCopy = null;
        switch (newStatus) {
            case SEEN -> alertCopy = alert.seen();
            case SAFE -> alertCopy = alert.safe();
            case CLOSED ->alertCopy = alert.closed();
            case OPENED -> alertCopy = alert.open();
        }

        // Use factory
        AlertEventEntity event = AlertEventFactory.createStatusChangedEvent(
                alertCopy, previousStatus, newStatus, new UserId(userId)
        );

        eventRepository.save(event);

        return alertRepository.save(alertCopy.toEntity()).toDomain();
    }

    @Transactional
    public Alert updateTitle(String alertId,String userId, String title) {
        Alert alert = findById(alertId);

        if (!alert.getUserID().value().equals(userId)) {
            throw new UnauthorizedException("Just authorized users can modify this alert");
        }

        String oldTitle = alert.getTitle().getValue();
        UserId editorId = new UserId(userId);

        AlertEventEntity eventEntity = AlertEventFactory.createTitleChangedEvent(
                alert, oldTitle, title, editorId
        );

        eventRepository.save(eventEntity);

        Alert alertCopy = alert.updateTitle(new Title(title));
        return alertRepository.save(alertCopy.toEntity()).toDomain();
    }

    @Transactional
    public Alert updateDescription(String alertId,String userId,String  description) {
        Alert alert = findById(alertId);
        if (!alert.getUserID().value().equals(userId)) {
            throw new UnauthorizedException("Just authorized users can modify this alert" + "---" +
                    alert.getUserID() +"---"+ userId);
        }

        String oldDescription= alert.getDescription().description();
        UserId editorId = new UserId(userId);

        AlertEventEntity eventEntity = AlertEventFactory.createDescriptionChangedEvent(
                alert, oldDescription, description, editorId
        );

        eventRepository.save(eventEntity);


        Alert alertCopy = alert.updateDescription( new Description(description));
        return alertRepository.save(alertCopy.toEntity()).toDomain();
    }

}
