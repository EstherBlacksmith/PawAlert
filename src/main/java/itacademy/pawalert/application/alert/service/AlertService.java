package itacademy.pawalert.application.alert.service;

import itacademy.pawalert.application.alert.port.inbound.*;
import itacademy.pawalert.application.alert.port.outbound.CurrentUserProviderPort;
import itacademy.pawalert.application.exception.AlertNotFoundException;
import itacademy.pawalert.application.user.port.inbound.GetUserUseCase;
import itacademy.pawalert.domain.alert.exception.AlertAccessDeniedException;
import itacademy.pawalert.domain.alert.exception.PetAlreadyHasActiveAlertException;
import itacademy.pawalert.domain.alert.model.*;
import itacademy.pawalert.domain.alert.service.AlertFactory;
import itacademy.pawalert.application.alert.port.outbound.AlertRepositoryPort;
import itacademy.pawalert.application.alert.port.outbound.AlertEventRepositoryPort;
import itacademy.pawalert.domain.alert.specification.AlertSpecifications;
import itacademy.pawalert.infrastructure.persistence.alert.AlertEventFactory;
import itacademy.pawalert.infrastructure.rest.alert.dto.AlertWithContactDTO;
import itacademy.pawalert.infrastructure.rest.alert.mapper.AlertMapper;
import jakarta.transaction.Transactional;
import itacademy.pawalert.domain.user.User;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static itacademy.pawalert.domain.alert.model.StatusNames.OPENED;

@Service
public class AlertService implements
        CreateAlertUseCase,
        GetAlertUseCase,
        UpdateAlertStatusUseCase,
        UpdateAlertUseCase,
        DeleteAlertUseCase,
        SearchAlertsUseCase {

    private final AlertRepositoryPort alertRepository;
    private final AlertEventRepositoryPort eventRepository;
    private final GetUserUseCase userUseCase;
    private final AlertMapper alertMapper;
    private final ApplicationEventPublisher eventPublisher;
    private final CurrentUserProviderPort currentUserProvider;

    public AlertService(AlertRepositoryPort alertRepository, AlertEventRepositoryPort eventRepository,
                        GetUserUseCase userUseCase, AlertMapper alertMapper, ApplicationEventPublisher eventPublisher,
                        CurrentUserProviderPort currentUserProvider){
        this.alertRepository = alertRepository;
        this.eventRepository = eventRepository;
        this.userUseCase = userUseCase;
        this.alertMapper = alertMapper;
        this.eventPublisher = eventPublisher;
        this.currentUserProvider = currentUserProvider;
    }

    public List<Alert> findOpenAlertsWithTitle(String title) {
        Specification<Alert> spec = AlertSpecifications.withStatus(OPENED)
                .and(AlertSpecifications.titleContains(title));

        return alertRepository.findAll(spec);
    }


    @Transactional
    public Alert createOpenedAlert(UUID petId, Title title, Description description, UUID userId, GeographicLocation location) {

        if (alertRepository.existsActiveAlertByPetId(petId)) {
            throw PetAlreadyHasActiveAlertException.forPet(petId.toString());
        }

        Alert alert = AlertFactory.createAlert(
                petId,
                userId,
                title,
                description);

        //Persist the object
        Alert savedAlert = alertRepository.save(alert);
        AlertEvent event = AlertEventFactory.createStatusChangedEvent(
                savedAlert, OPENED, OPENED, userId, location
        );

        eventRepository.save(event);

        eventPublisher.publishEvent(
                new AlertStatusChangedEvent(savedAlert.getId(), OPENED, OPENED)
        );

        // the owner must be subscribed to his own alerts
        eventPublisher.publishEvent(AlertCreatedEvent.of(savedAlert.getId(), userId));

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
    public Alert markAsSeen(UUID alertId, UUID userId, GeographicLocation location) {
        return changeStatus(alertId, StatusNames.SEEN, userId, location, null);
    }

    @Transactional
    @Override
    public Alert markAsClosed(UUID alertId, UUID userId, GeographicLocation location, ClosureReason closureReason) {
        // Business rule: closing an alert always requires a reason
        if (closureReason == null) {
            throw new IllegalArgumentException("Closure reason is required when closing an alert");
        }

        Alert alert = getAlertById(alertId);
        checkAuthorizationOwerOrAdmin(alert);

        return changeStatus(alertId, StatusNames.CLOSED, userId, location, closureReason);

    }


    @Transactional
    @Override
    public Alert markAsSafe(UUID alertId, UUID userId, GeographicLocation location) {
        return changeStatus(alertId, StatusNames.SAFE, userId, location, null);
    }

    @Transactional
    @Override
    public Alert closeAlert(UUID alertId, UUID userId, GeographicLocation location, ClosureReason closureReason) {
        // Business rule: closing an alert always requires a reason
        if (closureReason == null) {
            throw new IllegalArgumentException("Closure reason is required when closing an alert");
        }

        // Authorization check: only owner or admin can close an alert
        Alert alert = getAlertById(alertId);
        checkAuthorizationOwerOrAdmin(alert);

        // Delegate to changeStatus with CLOSED status
        return changeStatus(alertId, StatusNames.CLOSED, userId, location, closureReason);
    }

    @Transactional
    @Override
    public Alert changeStatus(UUID alertId, StatusNames newStatus, UUID userId, GeographicLocation location, ClosureReason closureReason) {

        Alert alert = getAlertById(alertId);
        StatusNames previousStatus = alert.currentStatus().getStatusName();

        Alert alertCopy;
        switch (newStatus) {

            case SEEN -> alertCopy = alert.seen();

            case SAFE -> alertCopy = alert.safe();

            case CLOSED -> alertCopy = alert.closed();

            case OPENED -> alertCopy = alert.open();

            default -> throw new IllegalArgumentException("Invalid alert state: " + newStatus);
        }

        eventPublisher.publishEvent(
                new AlertStatusChangedEvent(alertId, previousStatus, newStatus)
        );

        // Use appropriate factory method based on status change type
        AlertEvent event;
        if (newStatus == StatusNames.CLOSED ) {
            // For closure events, include the closure reason
            event = AlertEventFactory.createClosureEvent(alertCopy, previousStatus, userId, location, closureReason);
        } else {
            // For other status changes
            event = AlertEventFactory.createStatusChangedEvent(alertCopy, previousStatus, newStatus, userId, location);
        }

        eventRepository.save(event);

        return alertRepository.save(alertCopy);
    }

    @Transactional
    @Override
    public void deleteAlertById(UUID alertId) {
        Alert alert = getAlertById(alertId);
        checkAuthorizationOwerOrAdmin(alert);
        alertRepository.deleteById(alertId);
    }

    @Transactional
    @Override
    public Alert updateTitle(UUID alertId, Title title) {
        Alert alert = getAlertById(alertId);
        UUID userId = checkAuthorizationOwerOrAdmin(alert);

        Title oldTitle = alert.getTitle();

        GeographicLocation lastLocation = eventRepository
                .findLatestByAlertId(alertId)
                .map(AlertEvent::getLocation)
                .orElse(null);

        AlertEvent event = AlertEventFactory.createTitleChangedEvent(
                alert, oldTitle, title, userId
        );

        eventRepository.save(event);

        boolean isAdmin = currentUserProvider.isCurrentUserAdmin();
        Alert alertCopy = alert.updateTitle(title, isAdmin);
        
        return alertRepository.save(alertCopy);
    }

    @Transactional
    @Override
    public Alert updateDescription(UUID alertId, Description description) {
        Alert alert = getAlertById(alertId);
        UUID userId = checkAuthorizationOwerOrAdmin(alert);

        Description oldDescription = alert.getDescription();

        AlertEvent event = AlertEventFactory.createDescriptionChangedEvent(
                alert, oldDescription, description, userId
        );

        eventRepository.save(event);

        boolean isAdmin = currentUserProvider.isCurrentUserAdmin();
        Alert alertCopy = alert.updateDescription(description, isAdmin);
        return alertRepository.save(alertCopy);
    }


    private UUID checkAuthorizationOwerOrAdmin(Alert alert) {
        UUID currentUserId = currentUserProvider.getCurrentUserId();
        boolean isAdmin = currentUserProvider.isCurrentUserAdmin();
        
        boolean isCreator = alert.getUserId().equals(currentUserId);
        if (!isAdmin && !isCreator) {
            throw new AlertAccessDeniedException(alert.getId(), currentUserId);
        }
        return currentUserId;
    }

    private UUID checkAuthorizationLoggedUser(Alert alert) {
        UUID currentUserId = currentUserProvider.getCurrentUserId();

        boolean isAdmin = currentUserProvider.isCurrentUserAdmin();

        boolean isCreator = alert.getUserId().equals(currentUserId);
        if (!isAdmin && !isCreator) {
            throw new AlertAccessDeniedException(alert.getId(), currentUserId);
        }
        return currentUserId;
    }

    @Override
    public List<Alert> search(StatusNames status,
                              String petName,
                              String species,
                              LocalDateTime createdFrom,
                              LocalDateTime createdTo,
                              LocalDateTime updatedFrom,
                              LocalDateTime updatedTo) {
        Specification<Alert> spec = null;

        //By status
        if (status != null) {
            spec = (spec == null) ? AlertSpecifications.withStatus(status) : spec.and(AlertSpecifications.withStatus(status));
        }

        // By pet name
        if (petName != null && !petName.isBlank()) {
            Specification<Alert> petSpec = AlertSpecifications.petNameContains(petName);
            spec = (spec == null) ? petSpec : spec.and(petSpec);
        }

        // By specie
        if (species != null && !species.isBlank()) {
            Specification<Alert> speciesSpec = AlertSpecifications.withPetSpecies(species);
            spec = (spec == null) ? speciesSpec : spec.and(speciesSpec);
        }

        // Specification to Repository
        if (spec == null) {
            return alertRepository.findAll();  // Return all alerts instead of empty list
        }

        if (createdFrom != null) {
            Specification<Alert> fromSpec = AlertSpecifications.createdAfter(createdFrom);
            spec = (spec == null) ? fromSpec : spec.and(fromSpec);
        }
        if (createdTo != null) {
            Specification<Alert> toSpec = AlertSpecifications.createdBefore(createdTo);
            spec = (spec == null) ? toSpec : spec.and(toSpec);
        }
        if (updatedFrom != null) {
            Specification<Alert> updatedFromSpec = AlertSpecifications.lastUpdatedAfter(updatedFrom);
            spec = (spec == null) ? updatedFromSpec : spec.and(updatedFromSpec);
        }
        if (updatedTo != null) {
            Specification<Alert> updatedToSpec = AlertSpecifications.lastUpdatedBefore(updatedTo);
            spec = (spec == null) ? updatedToSpec : spec.and(updatedToSpec);
        }

        if (spec == null) {
            return alertRepository.findAll();
        }
        return alertRepository.findAll(spec);
    }

    @Override
    public List<Alert> search() {
        return alertRepository.findAll();
    }

    @Override
    public List<Alert> searchNearby(Double latitude, Double longitude, Double radiusKm) {

        List<Alert> activeAlerts = alertRepository.findAll();

        // Filter by status (exclude CLOSED)
        List<Alert> nonClosedAlerts = activeAlerts.stream()
                .filter(alert -> alert.currentStatus().getStatusName() != StatusNames.CLOSED)
                .toList();

        // Get the latest location for each alert
        GeographicLocation center = GeographicLocation.of(latitude, longitude);
        return nonClosedAlerts.stream()
                .filter(alert -> {
                    // Get latest event with location using the proper event repository
                    List<AlertEvent> events = eventRepository.findByAlertIdOrderByChangedAtDesc(alert.getId());
                    return events.stream()
                            .filter(alertEvent -> alertEvent.getLocation() != null)
                            .max(Comparator.comparing(alertEvent -> alertEvent.getChangedAt().value()))
                            .map(event -> event.getLocation().isWithinRadius(center, radiusKm))
                            .orElse(false);
                })
                .toList();
    }

    public UUID getCreatorById(UUID alertId) {
        Alert alert = alertRepository.findById(alertId)
                .orElseThrow(() -> new AlertNotFoundException("Alert not found: " + alertId));
        return alert.getUserId();

    }

    public Title getTitleById(UUID alertId) {
        Alert alert = alertRepository.findById(alertId)
                .orElseThrow(() -> new AlertNotFoundException("Alert not found: " + alertId));
        return  alert.getTitle();
    }


    public Description getDescriptionById(UUID alertId) {
        Alert alert = alertRepository.findById(alertId)
                .orElseThrow(() -> new AlertNotFoundException("Alert not found: " + alertId));
        return  alert.getDescription();
    }

    public GeographicLocation getLastLocationById(UUID alertId) {

       return  eventRepository
                .findLatestByAlertId(alertId)
                .map(AlertEvent::getLocation)
                .orElse(null);

    }

    public StatusNames getLastStatusById(UUID alertId) {
        return  eventRepository
                .findLatestByAlertId(alertId)
                .map(AlertEvent::getNewStatus)
                .orElse(null);
    }

    @Override
    public Optional<Alert> getActiveAlertByPetId(UUID petId) {
        return alertRepository.findActiveAlertByPetId(petId);
    }


}
