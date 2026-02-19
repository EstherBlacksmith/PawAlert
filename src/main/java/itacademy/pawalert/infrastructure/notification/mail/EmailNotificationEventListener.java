package itacademy.pawalert.infrastructure.notification.mail;

import itacademy.pawalert.application.alert.port.outbound.AlertRepositoryPort;
import itacademy.pawalert.application.alert.port.outbound.AlertSubscriptionRepositoryPort;
import itacademy.pawalert.application.alert.service.AlertNotificationFormatter;
import itacademy.pawalert.application.exception.AlertNotFoundException;
import itacademy.pawalert.application.pet.service.PetService;
import itacademy.pawalert.domain.alert.model.Alert;
import itacademy.pawalert.domain.alert.model.AlertStatusChangedEvent;
import itacademy.pawalert.domain.pet.model.Pet;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class EmailNotificationEventListener {
    private final AlertSubscriptionRepositoryPort subscriptionRepository;
    private final EmailServiceImpl emailService;
    private final AlertNotificationFormatter formatter;
    private final AlertRepositoryPort alertRepository;
    private final PetService petService;

    @EventListener
    public void handleAlertStatusChanged(AlertStatusChangedEvent event) {

        Alert alert = alertRepository.findById(event.alertId())
                .orElseThrow(() -> new AlertNotFoundException("Alert not found: " + event.alertId()));
        Pet pet = petService.getPetById(alert.getPetId());

        List<String> emails = subscriptionRepository.findEmailsByAlertIdAndActiveTrue(event.alertId());
        String subject = formatter.formatEmailSubject(event.newStatus());
        String body = formatter.formatEmailBody(alert, pet, event.oldStatus(), event.newStatus());

        for (String email : emails) {
            emailService.sendToUser(email, subject, body);
        }
    }
}
