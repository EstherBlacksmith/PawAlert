package itacademy.pawalert.infrastructure.notification.mail;

import itacademy.pawalert.application.alert.port.outbound.AlertRepositoryPort;

import itacademy.pawalert.application.alert.service.AlertNotificationFormatter;
import itacademy.pawalert.application.exception.AlertNotFoundException;
import itacademy.pawalert.application.notification.port.outbound.NotificationRepositoryPort;
import itacademy.pawalert.application.pet.service.PetService;
import itacademy.pawalert.domain.alert.model.Alert;
import itacademy.pawalert.domain.alert.model.AlertStatusChangedEvent;
import itacademy.pawalert.domain.pet.model.Pet;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class EmailNotificationEventListener {
    private final NotificationRepositoryPort notificationRepository;
    private final EmailServiceImpl emailService;
    private final AlertNotificationFormatter formatter;
    private final AlertRepositoryPort alertRepository;
    private final PetService petService;

    @EventListener
    public void handleAlertStatusChanged(AlertStatusChangedEvent event) {
        log.info("[EMAIL-NOTIF] Event received: alertId={}, oldStatus={}, newStatus={}", 
                event.alertId(), event.oldStatus(), event.newStatus());

        Alert alert = alertRepository.findById(event.alertId())
                .orElseThrow(() -> new AlertNotFoundException("Alert not found: " + event.alertId()));
        log.info("[EMAIL-NOTIF] Alert found: id={}, petId={}", alert.getId(), alert.getPetId());
        
        Pet pet = petService.getPetById(alert.getPetId());
        log.info("[EMAIL-NOTIF] Pet found: name={}, image={}", 
                pet.getOfficialPetName(), 
                pet.getPetImage() != null ? pet.getPetImage().value() : "NO IMAGE");

        List<String> emails = notificationRepository.findSubscriberEmailsByAlertId(event.alertId());
        log.info("[EMAIL-NOTIF] Found {} subscriber emails for alert {}", emails.size(), event.alertId());
        
        if (emails.isEmpty()) {
            log.warn("[EMAIL-NOTIF] No subscribers with email notifications enabled for alert {}", event.alertId());
            return;
        }
        
        String subject = formatter.formatEmailSubject(event.newStatus());
        String body = formatter.formatEmailBody(alert, pet, event.oldStatus(), event.newStatus());
        log.info("[EMAIL-NOTIF] Email body length: {}", body != null ? body.length() : 0);
        log.info("[EMAIL-NOTIF] Pet image in body: {}", body != null && body.contains("img src") ? "YES" : "NO");
        log.info("[EMAIL-NOTIF] Sending emails to: {}", emails);

        for (String email : emails) {
            log.info("[EMAIL-NOTIF] Sending email to: {}", email);
            emailService.sendToUser(email, subject, body);
        }
    }
}
