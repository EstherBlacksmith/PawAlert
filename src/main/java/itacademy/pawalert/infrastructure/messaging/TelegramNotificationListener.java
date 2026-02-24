package itacademy.pawalert.infrastructure.messaging;

import itacademy.pawalert.domain.notification.exception.TelegramNotificationException;
import itacademy.pawalert.domain.notification.model.NotificationFailureReason;
import itacademy.pawalert.infrastructure.messaging.event.TelegramNotificationEvent;
import itacademy.pawalert.infrastructure.messaging.telegram.TelegramFailedNotificationRepository;
import itacademy.pawalert.infrastructure.messaging.telegram.TelegramQueueConfig;
import itacademy.pawalert.infrastructure.notification.telegram.TelegramNotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;


@Slf4j
@Component
@RequiredArgsConstructor
public class TelegramNotificationListener {

    private final TelegramNotificationService telegramService;
    private final TelegramFailedNotificationRepository failedNotificationRepository;

    @RabbitListener(queues = TelegramQueueConfig.TELEGRAM_QUEUE)
    public void handleNotification(TelegramNotificationEvent event) {
        log.info("Processing Telegram notification: eventId={}, chatId={}",
                event.eventId(), maskChatId(event.chatId()));

        try {
            // Send notification
            if (event.photoUrl() != null && !event.photoUrl().isEmpty()) {
                telegramService.sendPhotoWithCaption(
                        event.chatId(),
                        event.photoUrl(),
                        event.message()
                );
            } else {
                telegramService.sendToUser(event.chatId(), event.message());
            }

            log.info("Telegram notification sent successfully: eventId={}", event.eventId());

        } catch (TelegramNotificationException e) {
            handleNotificationException(event, e);
        }
    }


    @RabbitListener(queues = TelegramQueueConfig.TELEGRAM_DLQ)
    public void handleFailedNotification(TelegramNotificationEvent event) {
        log.error("Message moved to DLQ after all retries failed: eventId={}, userId={}, alertId={}",
                event.eventId(), event.userId(), event.alertId());

        // Store in database for admin review
        failedNotificationRepository.save(event);

        // Optionally: Send alert to admin
        // adminNotificationService.notifyAdminOfFailure(event);
    }

    private void handleNotificationException(TelegramNotificationEvent event,
                                             TelegramNotificationException e) {
        NotificationFailureReason reason = e.getReason();

        log.warn("Telegram notification failed: eventId={}, reason={}, chatId={}",
                event.eventId(), reason, maskChatId(event.chatId()));

        // Check if this is a retryable error
        if (isRetryableError(reason)) {
            log.info("Retryable error detected, re-throwing to trigger retry: {}", reason);
            throw e; // This triggers retry and eventually DLQ
        }

        // For permanent errors, log and don't retry
        log.error("Permanent error for chat {}, not retrying: {}",
                maskChatId(event.chatId()), reason);

        // Store for admin review
        failedNotificationRepository.save(event);
    }

    /**
     * Determine if an error is retryable.
     * Network errors might be temporary, so we should retry.
     */
    private boolean isRetryableError(NotificationFailureReason reason) {
        return reason == NotificationFailureReason.NETWORK_ERROR ||
                reason == NotificationFailureReason.UNKNOWN;
    }

    private String maskChatId(String chatId) {
        if (chatId == null || chatId.length() <= 5) {
            return "***";
        }
        return chatId.substring(0, 3) + "..." + chatId.substring(chatId.length() - 2);
    }
}
