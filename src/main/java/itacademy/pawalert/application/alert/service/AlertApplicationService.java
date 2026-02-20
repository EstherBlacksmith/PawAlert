package itacademy.pawalert.application.alert.service;

import itacademy.pawalert.application.alert.port.inbound.AlertSubscriptionUseCase;
import itacademy.pawalert.application.alert.port.inbound.CreateAlertUseCase;
import itacademy.pawalert.domain.alert.model.Alert;
import itacademy.pawalert.domain.alert.model.Description;
import itacademy.pawalert.domain.alert.model.GeographicLocation;
import itacademy.pawalert.domain.alert.model.Title;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

import java.util.UUID;

/**
 * Application Service that orchestrates alert creation with auto-subscription.
 * 
 * This service follows the Domain-Driven Design (DDD) and SOLID principles:
 * - SRP: Single responsibility - orchestrates the creation workflow
 * - OCP: Open for extension, closed for modification
 * - Clear separation between domain operations (alert creation vs subscription)
 * 
 * @see CreateAlertUseCase
 * @see AlertSubscriptionUseCase
 */
@Slf4j
@Service
@Primary
public class AlertApplicationService implements CreateAlertUseCase {

    private final CreateAlertUseCase alertCreationUseCase;
    private final AlertSubscriptionUseCase alertSubscriptionUseCase;

    public AlertApplicationService(CreateAlertUseCase alertCreationUseCase,
                                   AlertSubscriptionUseCase alertSubscriptionUseCase) {
        this.alertCreationUseCase = alertCreationUseCase;
        this.alertSubscriptionUseCase = alertSubscriptionUseCase;
    }

    /**
     * Creates an alert and automatically subscribes the creator to the alert.
     * This is the orchestration method that coordinates alert creation with auto-subscription.
     *
     * @param petId       the ID of the pet associated with the alert
     * @param title       the title of the alert
     * @param description the description of the alert
     * @param userId      the ID of the user creating the alert (who will also be auto-subscribed)
     * @param location    the geographic location of the alert
     * @return the created alert
     */
    @Override
    public Alert createOpenedAlert(UUID petId, Title title, Description description, 
                                   UUID userId, GeographicLocation location) {
        log.info("[ALERT-APPLICATION-SERVICE] Starting orchestrated alert creation for petId={}, userId={}", 
                petId, userId);

        // Step 1: Create the alert (using the domain service that handles alert creation only)
        Alert createdAlert = alertCreationUseCase.createOpenedAlert(petId, title, description, userId, location);
        log.info("[ALERT-APPLICATION-SERVICE] Alert created with ID: {}", createdAlert.getId());

        // Step 2: Auto-subscribe the creator to their own alert
        try {
            alertSubscriptionUseCase.subscribeToAlert(createdAlert.getId(), userId);
            log.info("[ALERT-APPLICATION-SERVICE] Auto-subscribed user {} to alert {}", 
                    userId, createdAlert.getId());
        } catch (Exception e) {
            // Log the error but don't fail the alert creation
            // The alert was successfully created, subscription failure shouldn't rollback the alert
            log.error("[ALERT-APPLICATION-SERVICE] Failed to auto-subscribe user {} to alert {}: {}", 
                    userId, createdAlert.getId(), e.getMessage());
        }

        return createdAlert;
    }
}
