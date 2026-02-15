package itacademy.pawalert.application.notification.service;

import itacademy.pawalert.application.alert.port.outbound.AlertRepositoryPort;
import itacademy.pawalert.application.alert.service.AlertNotificationFormatter;
import itacademy.pawalert.application.exception.AlertNotFoundException;
import itacademy.pawalert.application.notification.port.inbound.TelegramNotificationUseCase;
import itacademy.pawalert.application.alert.port.outbound.AlertSubscriptionRepositoryPort;
import itacademy.pawalert.application.pet.service.PetService;
import itacademy.pawalert.domain.alert.model.Alert;
import itacademy.pawalert.domain.alert.model.StatusNames;
import itacademy.pawalert.domain.pet.model.Pet;
import itacademy.pawalert.infrastructure.notification.telegram.TelegramNotificationService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class TelegramNotificationUseCaseImpl implements TelegramNotificationUseCase {

    private final AlertSubscriptionRepositoryPort subscriptionRepository;
    private final TelegramNotificationService telegramService;
    private final AlertNotificationFormatter formatter;
    private final AlertRepositoryPort alertRepository;
    private final PetService petService;

    public TelegramNotificationUseCaseImpl(AlertSubscriptionRepositoryPort subscriptionRepository,
                                           TelegramNotificationService telegramService,
                                           AlertNotificationFormatter formatter, AlertRepositoryPort alertRepository, PetService petService) {
        this.subscriptionRepository = subscriptionRepository;
        this.telegramService = telegramService;
        this.formatter = formatter;
        this.alertRepository = alertRepository;
        this.petService = petService;
    }

    @Override
    public void notifyStatusChange(UUID alertId, StatusNames oldStatus, StatusNames newStatus) {
        List<String> chatIds = subscriptionRepository.findTelegramChatIdsByAlertId(alertId.toString());

        Alert alert = alertRepository.findById(alertId)
                .orElseThrow(() -> new AlertNotFoundException("Alert not found: " + alertId));
        Pet pet = petService.getPetById(alert.getPetId());
        String message = formatter.formatStatusChangeMessage(alert,pet, oldStatus, newStatus);

        telegramService.sendToAll(chatIds, message);
    }
}
