package itacademy.pawalert.application.notification.service;

import itacademy.pawalert.application.alert.port.outbound.AlertRepositoryPort;
import itacademy.pawalert.application.alert.service.AlertNotificationFormatter;
import itacademy.pawalert.application.exception.AlertNotFoundException;
import itacademy.pawalert.application.notification.port.inbound.TelegramNotificationUseCase;
import itacademy.pawalert.application.pet.service.PetService;
import itacademy.pawalert.application.user.port.outbound.UserRepositoryPort;
import itacademy.pawalert.domain.alert.model.Alert;
import itacademy.pawalert.domain.alert.model.StatusNames;
import itacademy.pawalert.domain.pet.model.Pet;
import itacademy.pawalert.domain.user.User;
import itacademy.pawalert.domain.user.model.TelegramChatId;
import itacademy.pawalert.infrastructure.notification.telegram.TelegramNotificationService;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class TelegramNotificationUseCaseImpl implements TelegramNotificationUseCase {

    private final UserRepositoryPort userRepository;  // ← ADD
    private final TelegramNotificationService telegramService;
    private final AlertNotificationFormatter formatter;
    private final AlertRepositoryPort alertRepository;
    private final PetService petService;

    public TelegramNotificationUseCaseImpl(
            UserRepositoryPort userRepository,  // ← ADD
            TelegramNotificationService telegramService,
            AlertNotificationFormatter formatter,
            AlertRepositoryPort alertRepository,
            PetService petService) {
        this.userRepository = userRepository;
        this.telegramService = telegramService;
        this.formatter = formatter;
        this.alertRepository = alertRepository;
        this.petService = petService;
    }

    @Override
    public void notifyStatusChange(UUID userId, UUID alertId, StatusNames newStatus) {
        // 1. Get the specific user
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found: " + userId));

        // 2. Check if user wants telegram notifications
        if (!user.isTelegramNotificationsEnabled()) {
            return;  // Skip silently
        }

        // 3. Check if user has telegram chat ID
        TelegramChatId chatId = user.getTelegramChatId();
        if (chatId == null || !chatId.isLinked()) {
            return;  // Skip silently
        }

        // 4. Get alert details
        Alert alert = alertRepository.findById(alertId)
                .orElseThrow(() -> new AlertNotFoundException("Alert not found: " + alertId));
        Pet pet = petService.getPetById(alert.getPetId());

        // 5. Format and send
        String message = formatter.formatStatusChangeMessage(alert, pet, newStatus);
        String chatIdValue = user.getTelegramChatId().value();
        telegramService.sendToUser(chatIdValue, message);
    }
}
