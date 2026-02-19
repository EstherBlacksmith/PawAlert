package itacademy.pawalert.infrastructure.notification.sse;

import itacademy.pawalert.application.alert.port.outbound.AlertRepositoryPort;
import itacademy.pawalert.application.exception.AlertNotFoundException;
import itacademy.pawalert.application.pet.service.PetService;
import itacademy.pawalert.domain.alert.model.Alert;
import itacademy.pawalert.domain.alert.model.AlertCreatedEvent;
import itacademy.pawalert.domain.alert.model.AlertStatusChangedEvent;
import itacademy.pawalert.domain.pet.model.Pet;
import itacademy.pawalert.infrastructure.rest.notification.dto.NotificationMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class SseNotificationEventListener {

    private final SseNotificationService sseNotificationService;
    private final AlertRepositoryPort alertRepository;
    private final PetService petService;


    @EventListener
    public void handleAlertStatusChanged(AlertStatusChangedEvent event) {
        log.info("Broadcasting alert status change: {}", event.alertId());

        try {
            Alert alert = alertRepository.findById(event.alertId())
                    .orElseThrow(() -> new AlertNotFoundException("Alert not found: " + event.alertId()));
            Pet pet = petService.getPetById(alert.getPetId());

            NotificationMessage notification = NotificationMessage.statusChange(
                    event.alertId(),
                    String.valueOf(pet.getOfficialPetName()),
                    event.oldStatus().toString(),
                    event.newStatus().toString()
            );

            sseNotificationService.broadcast(notification);

        } catch (AlertNotFoundException e) {
            log.error("Alert not found for status change notification: {}", event.alertId());
        } catch (Exception e) {
            // Client disconnection is handled by SseNotificationService.broadcast()
            // Log at DEBUG level to avoid noise from normal disconnection events
            log.debug("Error broadcasting alert status change: {}", e.getMessage());
        }
    }

    @EventListener
    public void handleAlertCreated(AlertCreatedEvent event) {
        log.info("Broadcasting new alert: {}", event.alertId());

        try {
            Alert alert = alertRepository.findById(event.alertId())
                    .orElseThrow(() -> new AlertNotFoundException("Alert not found: " + event.alertId()));
            Pet pet = petService.getPetById(alert.getPetId());

            NotificationMessage notification = NotificationMessage.newAlert(
                    event.alertId(),
                    pet.getOfficialPetName().toString()
            );

            sseNotificationService.broadcast(notification);

        } catch (AlertNotFoundException e) {
            log.error("Alert not found for new alert notification: {}", event.alertId());
        } catch (Exception e) {
            // Client disconnection is handled by SseNotificationService.broadcast()
            log.debug("\"Error broadcasting new alert: {}", e.getMessage());


        }
    }
}