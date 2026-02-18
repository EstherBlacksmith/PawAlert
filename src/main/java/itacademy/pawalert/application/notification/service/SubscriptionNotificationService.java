package itacademy.pawalert.application.notification.service;

import itacademy.pawalert.application.alert.port.outbound.AlertSubscriptionRepositoryPort;
import itacademy.pawalert.application.notification.port.inbound.EmailNotificationUseCase;
import itacademy.pawalert.application.notification.port.inbound.TelegramNotificationUseCase;
import itacademy.pawalert.application.user.port.outbound.UserRepositoryPort;
import itacademy.pawalert.domain.alert.model.AlertSubscription;
import itacademy.pawalert.domain.alert.model.StatusNames;
import itacademy.pawalert.domain.user.User;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;


@Service
public class SubscriptionNotificationService {

    private final AlertSubscriptionRepositoryPort subscriptionRepository;
    private final UserRepositoryPort userRepository;
    private final EmailNotificationUseCase emailUseCase;
    private final TelegramNotificationUseCase telegramUseCase;

    public SubscriptionNotificationService(
            AlertSubscriptionRepositoryPort subscriptionRepository,
            UserRepositoryPort userRepository,
            EmailNotificationUseCase emailUseCase,
            TelegramNotificationUseCase telegramUseCase) {
        this.subscriptionRepository = subscriptionRepository;
        this.userRepository = userRepository;
        this.emailUseCase = emailUseCase;
        this.telegramUseCase = telegramUseCase;
    }

    public void notifySubscribers(UUID alertId, StatusNames newStatus) {
        List<AlertSubscription> subscriptions = subscriptionRepository.findByAlertIdAndActiveTrue(alertId);

        for (AlertSubscription sub : subscriptions) {
            Optional<User> userOpt = userRepository.findById(sub.getUserId());

            if (userOpt.isEmpty()) {
                continue; // this subscriber doesn't exist
            }

            User subscriber = userOpt.get();

            if (subscriber.isEmailNotificationsEnabled()) {
                emailUseCase.notifyStatusChange(subscriber.getId(), alertId, newStatus);
            }

            if (subscriber.isTelegramNotificationsEnabled() && subscriber.getTelegramChatId() != null) {
                telegramUseCase.notifyStatusChange(subscriber.getId(), alertId, newStatus);
            }
        }
    }
}