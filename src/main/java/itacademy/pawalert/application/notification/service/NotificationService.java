package itacademy.pawalert.application.notification.service;

import itacademy.pawalert.application.alert.port.outbound.AlertRepositoryPort;
import itacademy.pawalert.application.notification.port.inbound.EmailNotificationUseCase;
import itacademy.pawalert.application.notification.port.inbound.LaunchAlertNotification;
import itacademy.pawalert.application.notification.port.inbound.TelegramNotificationUseCase;
import itacademy.pawalert.domain.alert.model.*;
import org.springframework.stereotype.Service;


import java.util.UUID;

@Service
public class NotificationService implements LaunchAlertNotification {

    private final EmailNotificationUseCase emailUseCase;
    private final TelegramNotificationUseCase telegramUseCase;
    private final AlertRepositoryPort alertRepository;
    public NotificationService(EmailNotificationUseCase emailUseCase, TelegramNotificationUseCase telegramUseCase, AlertRepositoryPort alertRepository) {
        this.emailUseCase = emailUseCase;
        this.telegramUseCase = telegramUseCase;
        this.alertRepository = alertRepository;
    }

    @Override
    public void relaunchNotification(UUID alertId) {
       StatusNames currentStatus = alertRepository.getLastStatusById(alertId);
        notifyStatusChange(alertId,alertId,currentStatus);
    }

    @Override
    public void notifyStatusChange(UUID userId, UUID alertId, StatusNames newStatusNames) {
        emailUseCase.notifyStatusChange(userId, alertId,  newStatusNames);
        telegramUseCase.notifyStatusChange(userId, alertId,  newStatusNames);

    }


}
