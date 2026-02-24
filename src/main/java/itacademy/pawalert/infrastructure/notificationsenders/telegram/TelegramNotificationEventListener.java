package itacademy.pawalert.infrastructure.notificationsenders.telegram;

import itacademy.pawalert.application.alert.port.outbound.AlertRepositoryPort;
import itacademy.pawalert.application.alert.service.AlertNotificationFormatter;
import itacademy.pawalert.application.exception.AlertNotFoundException;
import itacademy.pawalert.application.notification.port.outbound.NotificationRepositoryPort;
import itacademy.pawalert.application.pet.service.PetService;
import itacademy.pawalert.domain.alert.model.Alert;
import itacademy.pawalert.domain.alert.model.AlertStatusChangedEvent;
import itacademy.pawalert.domain.pet.model.Pet;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class TelegramNotificationEventListener {
    private final NotificationRepositoryPort notificationRepository;
    private final TelegramNotificationService telegramNotificationService;
    private final AlertNotificationFormatter formatter;
    private final AlertRepositoryPort alertRepository;
    private final PetService petService;

    @EventListener
    public void handleAlertStatusChanged(AlertStatusChangedEvent event) {

        Alert alert = alertRepository.findById(event.alertId())
                .orElseThrow(() -> new AlertNotFoundException("Alert not found: " + event.alertId()));
        Pet pet = petService.getPetById(alert.getPetId());

        List<String> telegramChatIds = notificationRepository.findSubscriberTelegramChatIdsByAlertId(event.alertId());

        // Use new formatted message with pet image
        String message = formatter.formatTelegramMessage(alert, pet, event.newStatus());

        // Try to send photo if available
        String petImageUrl = pet.getPetImage() != null ? pet.getPetImage().value() : null;

        for (String chatId : telegramChatIds) {
            if (petImageUrl != null && !petImageUrl.isEmpty()) {
                try {
                    telegramNotificationService.sendPhotoWithCaption(chatId, petImageUrl, message);
                } catch (Exception e) {
                    // Fallback to text message if photo fails
                    log.warn("Failed to send photo to Telegram, falling back to text: {}", e.getMessage());
                    telegramNotificationService.sendToUser(chatId, message);
                }
            } else {
                telegramNotificationService.sendToUser(chatId, message);
            }
        }
    }
}
