package itacademy.pawalert.application.service;

import itacademy.pawalert.application.port.inbound.RelaunchAlertNotification;
import itacademy.pawalert.application.port.outbound.AlertSubscriptionRepositoryPort;
import itacademy.pawalert.domain.alert.model.Alert;
import itacademy.pawalert.domain.alert.model.StatusNames;
import itacademy.pawalert.infrastructure.notification.mail.EmailServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class NotificationService implements RelaunchAlertNotification {
    @Autowired
    private AlertSubscriptionRepositoryPort subscriptionRepository;

    @Autowired
    private EmailServiceImpl emailService;

    @Override
    public Alert relaunchNotification(UUID alertId) {
        return null;
    }

    @Override
    public void notifyStatusChange(UUID alertId, StatusNames oldStatusNames, StatusNames newStatusNames) {
        List<String> emails = subscriptionRepository.findEmailsByAlertIdAndActiveTrue(alertId);
        String subject = "Alerta actualizada: " + newStatusNames;
        String body = "La alerta ha cambiado de " + oldStatusNames + " a " + newStatusNames;

        for (String email : emails) {
            emailService.sendHtmlEmail(email, subject, body);
        }
    }
}
