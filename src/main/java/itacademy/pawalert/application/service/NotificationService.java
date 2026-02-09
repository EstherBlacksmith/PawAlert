package itacademy.pawalert.application.service;

import itacademy.pawalert.application.port.inbound.EmailNotificationUseCase;
import itacademy.pawalert.application.port.inbound.LaunchAlertNotification;
import itacademy.pawalert.application.port.inbound.TelegramNotificationUseCase;
import itacademy.pawalert.domain.alert.model.*;

import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class NotificationService implements LaunchAlertNotification {

    private final EmailNotificationUseCase emailUseCase;
    private final TelegramNotificationUseCase telegramUseCase;
    private final AlertService alertService;

    public NotificationService(EmailNotificationUseCase emailUseCase, TelegramNotificationUseCase telegramUseCase, AlertService alertService) {
        this.emailUseCase = emailUseCase;
        this.telegramUseCase = telegramUseCase;
        this.alertService = alertService;
    }

    @Override
    public void relaunchNotification(UUID alertId) {
       StatusNames currentStatus = alertService.getLastStatusById(alertId);
        notifyStatusChange(alertId,currentStatus,currentStatus);
    }

    @Override
    public void notifyStatusChange(UUID alertId, StatusNames oldStatusNames, StatusNames newStatusNames) {
        emailUseCase.notifyStatusChange(alertId, oldStatusNames, newStatusNames);
        telegramUseCase.notifyStatusChange(alertId, oldStatusNames, newStatusNames);

    }


}
