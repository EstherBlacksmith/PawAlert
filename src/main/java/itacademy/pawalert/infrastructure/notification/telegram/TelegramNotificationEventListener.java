package itacademy.pawalert.infrastructure.notification.telegram;

import itacademy.pawalert.application.alert.port.outbound.AlertRepositoryPort;
import itacademy.pawalert.application.alert.port.outbound.AlertSubscriptionRepositoryPort;
import itacademy.pawalert.application.alert.service.AlertNotificationFormatter;
import itacademy.pawalert.application.exception.AlertNotFoundException;
import itacademy.pawalert.application.pet.service.PetService;
import itacademy.pawalert.domain.alert.model.Alert;
import itacademy.pawalert.domain.alert.model.AlertStatusChangedEvent;
import itacademy.pawalert.domain.pet.model.Pet;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class TelegramNotificationEventListener {
    private final AlertSubscriptionRepositoryPort subscriptionRepository;
    private final TelegramNotificationService telegramNotificationService;
    private final AlertNotificationFormatter formatter;
    private final AlertRepositoryPort alertRepository;
    private final PetService petService;

    @EventListener
    public void handleAlertStatusChanged(AlertStatusChangedEvent event) {

        Alert alert = alertRepository.findById(event.alertId())
                .orElseThrow(() -> new AlertNotFoundException("Alert not found: " + event.alertId()));
        Pet pet = petService.getPetById(alert.getPetId());

        List<String> telegramChatIds = subscriptionRepository.findTelegramChatIdsByAlertId(event.alertId().toString());
        String message = formatter.formatStatusChangeMessage(alert,pet, event.newStatus());

        for (String chatId : telegramChatIds) {
            telegramNotificationService.sendToUser(chatId,message);
        }
    }
}
