package itacademy.pawalert.application.service;

import itacademy.pawalert.application.exception.AlertNotFoundException;
import itacademy.pawalert.application.exception.UnauthorizedException;
import itacademy.pawalert.application.port.inbound.*;
import itacademy.pawalert.domain.alert.model.*;
import itacademy.pawalert.domain.alert.service.AlertFactory;
import itacademy.pawalert.application.port.outbound.AlertRepositoryPort;
import itacademy.pawalert.application.port.outbound.AlertEventRepositoryPort;
import itacademy.pawalert.domain.alert.specification.AlertSpecifications;
import itacademy.pawalert.infrastructure.persistence.alert.AlertEventFactory;
import itacademy.pawalert.infrastructure.rest.alert.mapper.AlertMapper;
import jakarta.transaction.Transactional;
import itacademy.pawalert.domain.user.User;
import org.springframework.data.jpa.domain.Specification;
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
    private final GetUserUseCase userUseCase;
    private final AlertMapper alertMapper;

    public AlertService(AlertRepositoryPort alertRepository, AlertEventRepositoryPort eventRepository, GetUserUseCase userUseCase, AlertMapper alertMapper){
        this.alertRepository = alertRepository;
        this.eventRepository = eventRepository;
        this.userUseCase = userUseCase;
        this.alertMapper = alertMapper;
    }


    public List<Alert> findOpenAlertsWithTitle(String title) {
        Specification<Alert> spec = AlertSpecifications.withStatus(StatusNames.OPENED)
                .and(AlertSpecifications.titleContains(title));

        return alertRepository.findAll(spec);
    }


    @Transactional
    public Alert createOpenedAlert(UUID petId, Title title, Description description, UUID userId) {

        Alert alert = AlertFactory.createAlert(
                petId,
                userId,
                title,
                description);

        //Persist the object
        Alert savedAlert = alertRepository.save(alert);
        AlertEvent event = AlertEventFactory.createStatusChangedEvent(
                alert, StatusNames.OPENED, StatusNames.OPENED, userId
        );

        eventRepository.save(event);

        return savedAlert;
    }

    @Override
    public Alert getAlertById(UUID alertId) {
        return alertRepository.findById(alertId)
                .orElseThrow(() -> new AlertNotFoundException("Alert not found: " + alertId));
    }

    @Override
    public List<Alert> getAlertsByPetId(UUID petId) {
        return alertRepository.findAllByPetId(petId);
    }

    @Override
    public List<AlertEvent> getAlertHistory(UUID alertId) {
        return eventRepository.findByAlertIdOrderByChangedAtDesc(alertId);
    }

    @Override
    public AlertWithContactDTO getAlertWithCreatorPhone(UUID alertId) {
        Alert alert = getAlertById(alertId);
        User creator = userUseCase.getById(alert.getUserId());
        return alertMapper.toWithContact(alert, creator);
    }

    @Transactional
    @Override
    public Alert markAsSeen(UUID alertId, UUID userId) {
        return changeStatus(alertId, StatusNames.SEEN, userId);
    }

    @Transactional
    @Override
    public Alert markAsSafe(UUID alertId, UUID userId) {
        return changeStatus(alertId, StatusNames.SAFE, userId);
    }

    @Transactional
    public Alert markAsClosed(UUID alertId, UUID userId) {
        return changeStatus(alertId, StatusNames.CLOSED, userId);
    }

    @Transactional
    @Override
    public Alert changeStatus(UUID alertId, StatusNames newStatus, UUID userId) {

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
                alertCopy, previousStatus, newStatus, userId
        );

        eventRepository.save(event);

        return alertRepository.save(alertCopy);
    }

    @Transactional
    @Override
    public void deleteAlertById(UUID alertId) {
        if (!alertRepository.existsById(alertId)) {
            throw new AlertNotFoundException("Alert not found: " + alertId);
        }
        alertRepository.deleteById(alertId);
    }

    @Transactional
    @Override
    public Alert updateTitle(UUID alertId, UUID userId, Title title) {
        Alert alert = getAlertById(alertId);

        if (!alert.getUserId().equals(userId)) {
            throw new UnauthorizedException("Just authorized users can modify this alert");
        }

        Title oldTitle = alert.getTitle();


        AlertEvent event = AlertEventFactory.createTitleChangedEvent(
                alert, oldTitle, title, userId
        );

        eventRepository.save(event);

        Alert alertCopy = alert.updateTitle(title);
        return alertRepository.save(alertCopy);
    }

    @Transactional
    @Override
    public Alert updateDescription(UUID alertId, UUID userId, Description description) {
        Alert alert = getAlertById(alertId);

        if (!alert.getUserId().equals(userId)) {
            throw new UnauthorizedException("Just authorized users can modify this alert");
        }

        Description oldDescription = alert.getDescription();

        AlertEvent event = AlertEventFactory.createDescriptionChangedEvent(
                alert, oldDescription, description, userId
        );

        eventRepository.save(event);

        Alert alertCopy = alert.updateDescription(description);
        return alertRepository.save(alertCopy);
    }

   }
