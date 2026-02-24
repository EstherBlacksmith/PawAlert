package itacademy.pawalert.infrastructure.messaging.email;

import itacademy.pawalert.application.notification.port.outbound.EmailServicePort;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class EmailNotificationListener {

    private final EmailServicePort emailService;
    private final EmailFailedNotificationRepository failedRepository;

    @RabbitListener(queues = EmailQueueConfig.EMAIL_QUEUE)
    public void handleEmailNotification(EmailNotificationEvent event) {
        log.info("Processing Email notification: eventId={}, to={}",
                event.eventId(), maskEmail(event.email()));

        try {
            emailService.sendToUser(event.email(), event.subject(), event.body());
            log.info("Email sent successfully: eventId={}", event.eventId());

        } catch (Exception e) {
            log.error("Failed to send email: eventId={}, error={}",
                    event.eventId(), e.getMessage());
            // Re-throw to trigger retry/DLQ
            throw new EmailNotificationException(
                    event.email(),
                    "Failed to send email: " + e.getMessage(),
                    e
            );
        }
    }

    @RabbitListener(queues = EmailQueueConfig.EMAIL_DLQ)
    public void handleFailedEmail(EmailNotificationEvent event) {
        log.error("Email moved to DLQ: eventId={}, email={}",
                event.eventId(), maskEmail(event.email()));
        failedRepository.save(event);
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
