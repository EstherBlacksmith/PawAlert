package itacademy.pawalert.infrastructure.messaging.email;

import itacademy.pawalert.application.notification.port.outbound.NotificationPublisherPort;
import itacademy.pawalert.domain.alert.model.NotificationChannel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;


@Slf4j
@Component
public class EmailNotificationPublisher implements NotificationPublisherPort<EmailNotificationEvent> {

    private final RabbitTemplate rabbitTemplate;

    public EmailNotificationPublisher(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    @Override
    public void publish(EmailNotificationEvent event) {
        log.info("Publishing Email notification: eventId={}, email={}",
                event.eventId(), maskEmail(event.email()));

        rabbitTemplate.convertAndSend(
                EmailQueueConfig.EMAIL_QUEUE,
                event
        );

        log.debug("Event published to queue: {}", EmailQueueConfig.EMAIL_QUEUE);
    }

    @Override
    public NotificationChannel getChannel() {
        return NotificationChannel.EMAIL;
    }

    private String maskEmail(String email) {
        if (email == null || !email.contains("@")) {
            return "***";
        }
        String[] parts = email.split("@");
        String localPart = parts[0];
        String domain = parts[1];
        return localPart.substring(0, Math.min(2, localPart.length())) + "***@" + domain;
    }
}
