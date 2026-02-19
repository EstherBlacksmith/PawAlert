package itacademy.pawalert.application.notification.service;

import itacademy.pawalert.application.notification.port.inbound.EmailNotificationUseCase;
import itacademy.pawalert.application.notification.port.inbound.TelegramNotificationUseCase;
import itacademy.pawalert.application.notification.port.outbound.NotificationRepositoryPort;
import itacademy.pawalert.domain.alert.model.StatusNames;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class SubscriptionNotificationService {

    private final NotificationRepositoryPort notificationRepository;  // Port!
    private final EmailNotificationUseCase emailUseCase;
    private final TelegramNotificationUseCase telegramUseCase;

    public SubscriptionNotificationService(NotificationRepositoryPort notificationRepository,
                                           EmailNotificationUseCase emailUseCase,
                                           TelegramNotificationUseCase telegramUseCase) {
        this.notificationRepository = notificationRepository;
        this.emailUseCase = emailUseCase;
        this.telegramUseCase = telegramUseCase;
    }

    public void notifySubscribers(UUID alertId, StatusNames newStatus) {
        List<UUID> userIds = notificationRepository.findSubscriberUserIdsByAlertId(alertId);

        userIds.forEach(userId -> {
            emailUseCase.notifyStatusChange(userId, alertId, newStatus);
            telegramUseCase.notifyStatusChange(userId, alertId, newStatus);
        });
    }
}
