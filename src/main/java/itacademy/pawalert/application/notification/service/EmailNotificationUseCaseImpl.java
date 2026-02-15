package itacademy.pawalert.application.notification.service;

import itacademy.pawalert.application.alert.port.outbound.AlertRepositoryPort;
import itacademy.pawalert.application.alert.service.AlertNotificationFormatter;
import itacademy.pawalert.application.exception.AlertNotFoundException;
import itacademy.pawalert.application.notification.port.inbound.EmailNotificationUseCase;
import itacademy.pawalert.application.alert.port.outbound.AlertSubscriptionRepositoryPort;
import itacademy.pawalert.application.pet.service.PetService;
import itacademy.pawalert.domain.alert.model.Alert;
import itacademy.pawalert.domain.alert.model.StatusNames;
import itacademy.pawalert.domain.pet.model.Pet;
import itacademy.pawalert.infrastructure.notification.mail.EmailServiceImpl;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class EmailNotificationUseCaseImpl implements EmailNotificationUseCase {

    private final AlertSubscriptionRepositoryPort subscriptionRepository;
    private final EmailServiceImpl emailService;
    private final AlertNotificationFormatter formatter;
    private final AlertRepositoryPort alertRepository;
    private final PetService petService;

    public EmailNotificationUseCaseImpl(AlertSubscriptionRepositoryPort subscriptionRepository, EmailServiceImpl emailService, AlertNotificationFormatter formatter, AlertRepositoryPort alertRepository, PetService petService) {
        this.subscriptionRepository = subscriptionRepository;
        this.emailService = emailService;
        this.formatter = formatter;
        this.alertRepository = alertRepository;
        this.petService = petService;
    }

    @Override
    public void notifyStatusChange(UUID alertId, StatusNames oldStatus, StatusNames newStatus) {
        List<String> emails = subscriptionRepository.findEmailsByAlertIdAndActiveTrue(alertId.toString());

        Alert alert = alertRepository.findById(alertId)
                .orElseThrow(() -> new AlertNotFoundException("Alert not found: " + alertId));
        Pet pet = petService.getPetById(alert.getPetId());
        String subject = formatter.formatEmailSubject(newStatus);
        String body = formatter.formatStatusChangeMessage(alert, pet, oldStatus, newStatus);

        for (String email : emails) {
            emailService.sendToUser(email, subject, body);
        }
    }
}
