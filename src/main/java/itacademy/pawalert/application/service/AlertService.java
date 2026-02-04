package itacademy.pawalert.application.service;

import itacademy.pawalert.application.exception.AlertNotFoundException;
import itacademy.pawalert.application.exception.UnauthorizedException;
import itacademy.pawalert.application.port.inbound.CreateAlertUseCase;
import itacademy.pawalert.application.port.inbound.GetAlertUseCase;
import itacademy.pawalert.application.port.inbound.UpdateAlertStatusUseCase;
import itacademy.pawalert.application.port.inbound.UpdateAlertUseCase;
import itacademy.pawalert.domain.alert.model.*;
import itacademy.pawalert.domain.alert.service.AlertFactory;
import itacademy.pawalert.application.port.outbound.AlertRepositoryPort;
import itacademy.pawalert.application.port.outbound.AlertEventRepositoryPort;
import itacademy.pawalert.infrastructure.persistence.alert.AlertEventFactory;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class AlertService implements
        CreateAlertUseCase,
        GetAlertUseCase,
        UpdateAlertStatusUseCase,
        UpdateAlertUseCase {

    private final AlertRepositoryPort alertRepository;
    private final AlertEventRepositoryPort eventRepository;

    public AlertService(AlertRepositoryPort alertRepository, AlertEventRepositoryPort eventRepository){
        this.alertRepository = alertRepository;
        this.eventRepository = eventRepository;
    }

    @Transactional
    public Alert createOpenedAlert(String petId, String titleString, String descriptionString, String userId) {

        Title title = new Title(titleString);
        Description description = new Description(descriptionString);
        UserId creatorId = new UserId(userId);

        Alert alert = AlertFactory.createAlert(
                UUID.fromString(petId),
                new UserId(userId),
                title,
                description);

        //Persist the object
        Alert savedAlert = alertRepository.save(alert);
        AlertEvent event = AlertEventFactory.createStatusChangedEvent(
                alert, StatusNames.OPENED, StatusNames.OPENED, creatorId
        );

        eventRepository.save(event);

        return savedAlert;
    }

    @Override
    public Alert getAlertById(String alertId) {
        return alertRepository.findById(alertId)
                .orElseThrow(() -> new AlertNotFoundException("Alert not found: " + alertId));
    }


    @Override
    public List<Alert> getAlertsByPetId(String petId) {
        return alertRepository.findAllByPetId(petId);
    }

    @Override
    public List<AlertEvent> getAlertHistory(String alertId) {
        return eventRepository.findByAlertIdOrderByChangedAtDesc(alertId);
    }

    @Override
    @Transactional
    public Alert markAsSeen(String alertId, String userId) {
        return changeStatus(alertId, StatusNames.SEEN, userId);
    }

    @Override
    @Transactional
    public Alert markAsSafe(String alertId, String userId) {
        return changeStatus(alertId, StatusNames.SAFE, userId);
    }

    @Override
    @Transactional
    public Alert close(String alertId, String userId) {
        return changeStatus(alertId, StatusNames.CLOSED, userId);
    }

    @Transactional
    public Alert markAsClose(String alertId, String userId) {
        return changeStatus(alertId, StatusNames.CLOSED, userId);
    }

    @Override
    @Transactional
    public Alert changeStatus(String alertId, StatusNames newStatus, String userId) {

        Alert alert = getAlertById(alertId);
        StatusNames previousStatus = alert.currentStatus().getStatusName();

        Alert alertCopy ;
        switch (newStatus) {
            case SEEN -> alertCopy = alert.seen();
            case SAFE -> alertCopy = alert.safe();
            case CLOSED -> alertCopy = alert.closed();
            case OPENED -> alertCopy = alert.open();
            default -> throw new IllegalArgumentException("Invalid alert state: " + newStatus);
        }

        // Use factory
        AlertEvent  event = AlertEventFactory.createStatusChangedEvent(
                alertCopy, previousStatus, newStatus, new UserId(userId)
        );

        eventRepository.save(event);

        return alertRepository.save(alertCopy);
    }

    @Override
    @Transactional
    public Alert updateTitle(String alertId, String userId, String title) {
        Alert alert = getAlertById(alertId);

        if (!alert.getUserID().value().equals(userId)) {
            throw new UnauthorizedException("Just authorized users can modify this alert");
        }

        String oldTitle = alert.getTitle().getValue();
        UserId editorId = new UserId(userId);

        AlertEvent event = AlertEventFactory.createTitleChangedEvent(
                alert, oldTitle, title, editorId
        );

        eventRepository.save(event);

        Alert alertCopy = alert.updateTitle(new Title(title));
        return alertRepository.save(alertCopy);
    }

    @Override
    @Transactional
    public Alert updateDescription(String alertId, String userId, String description) {
        Alert alert = getAlertById(alertId);

        if (!alert.getUserID().value().equals(userId)) {
            throw new UnauthorizedException("Just authorized users can modify this alert");
        }

        String oldDescription = alert.getDescription().description();
        UserId editorId = new UserId(userId);

        AlertEvent event = AlertEventFactory.createDescriptionChangedEvent(
                alert, oldDescription, description, editorId
        );

        eventRepository.save(event);

        Alert alertCopy = alert.updateDescription(new Description(description));
        return alertRepository.save(alertCopy);
    }

}
