package itacademy.pawalert.infrastructure.notificationqueues.telegram;

import itacademy.pawalert.application.notification.port.outbound.NotificationPublisherPort;
import itacademy.pawalert.domain.alert.model.NotificationChannel;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;


@Slf4j
@Component
@RequiredArgsConstructor
public class TelegramNotificationPublisher implements NotificationPublisherPort<TelegramNotificationEvent>{

    private final RabbitTemplate rabbitTemplate;

    // Publish a notification event to the queue.
    // This is non-blocking - the message will be processed asynchronously.
    @Override
    public void publish(TelegramNotificationEvent event) {
        log.info("Publishing Telegram notification event: eventId={}, userId={}, alertId={}",
                event.eventId(), event.userId(), event.alertId());

        rabbitTemplate.convertAndSend(
                TelegramQueueConfig.TELEGRAM_QUEUE,
                event
        );

        log.debug("Event published successfully to queue: {}",
                TelegramQueueConfig.TELEGRAM_QUEUE);
    }

    @Override
    public NotificationChannel getChannel() {
        return NotificationChannel.TELEGRAM;
    }
}
