package itacademy.pawalert.application.notification.service;

import itacademy.pawalert.application.alert.port.outbound.AlertRepositoryPort;
import itacademy.pawalert.application.alert.service.AlertNotificationFormatter;
import itacademy.pawalert.application.exception.AlertNotFoundException;
import itacademy.pawalert.application.notification.port.inbound.EmailNotificationUseCase;
import itacademy.pawalert.application.notification.port.outbound.EmailServicePort;
import itacademy.pawalert.application.pet.port.inbound.GetPetUseCase;
import itacademy.pawalert.application.user.port.outbound.UserRepositoryPort;
import itacademy.pawalert.domain.alert.model.Alert;
import itacademy.pawalert.domain.alert.model.StatusNames;
import itacademy.pawalert.domain.pet.model.Pet;
import itacademy.pawalert.domain.user.User;
import itacademy.pawalert.domain.user.exception.UserNotFoundException;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class EmailNotificationUseCaseImpl implements EmailNotificationUseCase {

    private final EmailServicePort emailService;
    private final UserRepositoryPort userRepository;
    private final GetPetUseCase getPetUseCase;
    private final AlertRepositoryPort alertRepository;
    private final AlertNotificationFormatter formatter;

    public EmailNotificationUseCaseImpl(
            EmailServicePort emailService,
            UserRepositoryPort userRepository,
            GetPetUseCase getPetUseCase,
            AlertRepositoryPort alertRepository,
            AlertNotificationFormatter formatter) {
        this.emailService = emailService;
        this.userRepository = userRepository;
        this.getPetUseCase = getPetUseCase;
        this.alertRepository = alertRepository;
        this.formatter = formatter;
    }

    @Override
    public void notifyStatusChange(UUID userId, UUID alertId, StatusNames newStatus) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User not found: " + userId));

        if (!user.isEmailNotificationsEnabled()) {
            return;
        }

        Alert alert = alertRepository.findById(alertId)
                .orElseThrow(() -> new AlertNotFoundException("Alert not found: " + alertId));

        Pet pet = getPetUseCase.getPetById(alert.getPetId());

        String subject = formatter.formatEmailSubject(newStatus);
        // Use formatEmailBody to get HTML with pet image
        String body = formatter.formatEmailBody(alert, pet, newStatus, newStatus);

        emailService.sendToUser(user.getEmail().value(), subject, body);
    }
}
