package itacademy.pawalert.application.service;

import itacademy.pawalert.application.port.inbound.EmailNotificationUseCase;
import itacademy.pawalert.application.port.outbound.AlertSubscriptionRepositoryPort;
import itacademy.pawalert.domain.alert.model.StatusNames;
import itacademy.pawalert.infrastructure.notification.mail.EmailServiceImpl;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class EmailNotificationUseCaseImpl implements EmailNotificationUseCase {

    private final AlertSubscriptionRepositoryPort subscriptionRepository;
    private final EmailServiceImpl emailService;
    private final AlertNotificationFormatter formatter;

    public EmailNotificationUseCaseImpl(AlertSubscriptionRepositoryPort subscriptionRepository, EmailServiceImpl emailService, AlertNotificationFormatter formatter) {
        this.subscriptionRepository = subscriptionRepository;
        this.emailService = emailService;
        this.formatter = formatter;
    }

    @Override
    public void notifyStatusChange(UUID alertId, StatusNames oldStatus, StatusNames newStatus) {
        List<String> emails = subscriptionRepository.findEmailsByAlertIdAndActiveTrue(alertId);
        String subject = formatter.formatEmailSubject(newStatus);
        String body = formatter.formatEmailBody(alertId, oldStatus, newStatus);

        for (String email : emails) {
            emailService.sendHtmlEmail(email, subject, body);
        }
    }
}
