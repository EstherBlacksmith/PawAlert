package itacademy.pawalert.application.service;

import itacademy.pawalert.application.port.inbound.RelaunchAlertNotification;
import itacademy.pawalert.application.port.outbound.AlertSubscriptionRepositoryPort;
import itacademy.pawalert.domain.alert.model.Alert;
import itacademy.pawalert.domain.alert.model.StatusNames;
import itacademy.pawalert.infrastructure.notification.mail.EmailServiceImpl;
import itacademy.pawalert.infrastructure.notification.telegram.TelegramNotificationService;
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

    private final TelegramNotificationService telegramService;

    public NotificationService(TelegramNotificationService telegramService) {
        this.telegramService = telegramService;
    }

    @Override
    public Alert relaunchNotification(UUID alertId) {
        return null;
    }

    @Override
    public void notifyStatusChange(UUID alertId, StatusNames oldStatusNames, StatusNames newStatusNames) {
        List<String> emails = subscriptionRepository.findEmailsByAlertIdAndActiveTrue(alertId);
        String subject = "Alet actualized: " + newStatusNames;
        String body = "The alert changes from " + oldStatusNames + " to " + newStatusNames;

        for (String email : emails) {
            emailService.sendHtmlEmail(email, subject, body);
        }
    }

    private String formatAlertMessage(Alert alert) {
        return String.format(
                "🔔 <b>Alert PawAlert</b>\n\n" +
                        "🐕 Pet: <b>%s</b>\n" +
                        "📍 Location: %s\n" +
                        "📊 Status: %s\n\n" +
                        "Help us to find %s!",
                alert.getPetName(),
                alert.getLocation(),
                alert.getStatus(),
                alert.getPetName()
        );
    }

    public void notifySubscribers(UUID alertId) {
        List<String> chatIds = subscriptionRepository.findTelegramChatIdsByAlertId(alertId);
        String message = formatAlertMessage(alertService.getById(alertId));

        // Telegram service only sends, doesn't format
        telegramService.sendToAll(chatIds, message);
    }
}
