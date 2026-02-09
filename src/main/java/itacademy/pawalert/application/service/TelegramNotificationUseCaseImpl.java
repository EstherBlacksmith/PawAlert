package itacademy.pawalert.application.service;

import itacademy.pawalert.application.port.inbound.TelegramNotificationUseCase;
import itacademy.pawalert.application.port.outbound.AlertSubscriptionRepositoryPort;
import itacademy.pawalert.domain.alert.model.StatusNames;
import itacademy.pawalert.infrastructure.notification.telegram.TelegramNotificationService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class TelegramNotificationUseCaseImpl implements TelegramNotificationUseCase {

    private final AlertSubscriptionRepositoryPort subscriptionRepository;
    private final TelegramNotificationService telegramService;
    private final AlertNotificationFormatter formatter;

    public TelegramNotificationUseCaseImpl(AlertSubscriptionRepositoryPort subscriptionRepository,
                                           TelegramNotificationService telegramService,
                                           AlertNotificationFormatter formatter) {
        this.subscriptionRepository = subscriptionRepository;
        this.telegramService = telegramService;
        this.formatter = formatter;
    }

    @Override
    public void notifyStatusChange(UUID alertId, StatusNames oldStatus, StatusNames newStatus) {
        List<String> chatIds = subscriptionRepository.findTelegramChatIdsByAlertId(alertId);
        String message = formatter.formatStatusChangeMessage(alertId, oldStatus, newStatus);

        telegramService.sendToAll(chatIds, message);
    }
}
