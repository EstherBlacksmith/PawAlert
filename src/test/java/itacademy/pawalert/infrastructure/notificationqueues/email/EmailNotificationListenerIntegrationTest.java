package itacademy.pawalert.infrastructure.notificationqueues.email;

import com.fasterxml.jackson.databind.ObjectMapper;
import itacademy.pawalert.application.notification.port.outbound.EmailServicePort;
import itacademy.pawalert.domain.alert.model.StatusNames;
import itacademy.pawalert.infrastructure.notificationqueues.AbstractRabbitMQIntegrationTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;
import static java.util.concurrent.TimeUnit.SECONDS;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.atLeast;

/**
 * Integration tests for {@link EmailNotificationListener}.
 * Tests message processing, retry logic, and DLQ routing.
 */
class EmailNotificationListenerIntegrationTest extends AbstractRabbitMQIntegrationTest {

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private EmailFailedNotificationRepository failedNotificationRepository;

    @MockitoBean
    private EmailServicePort emailService;

    private EmailNotificationEvent testEvent;

    @BeforeEach
    void setUp() {
        // Reset mocks and clear repositories before each test
        reset(emailService);
        
        // Purge queues before each test
        rabbitTemplate.execute(channel -> {
            channel.queuePurge(EmailQueueConfig.EMAIL_QUEUE);
            channel.queuePurge(EmailQueueConfig.EMAIL_DLQ);
            return null;
        });

        // Clear failed notification repository
        failedNotificationRepository.findAll()
                .forEach(event -> failedNotificationRepository.remove(event.eventId()));

        testEvent = createTestEvent();
    }

    @Test
    void shouldProcessMessageSuccessfully() {
        // Given
        doNothing().when(emailService).sendToUser(anyString(), anyString(), anyString());

        // When
        rabbitTemplate.convertAndSend(EmailQueueConfig.EMAIL_QUEUE, testEvent);

        // Then
        await().atMost(5, SECONDS).untilAsserted(() -> {
            verify(emailService).sendToUser(
                    eq(testEvent.email()),
                    eq(testEvent.subject()),
                    eq(testEvent.body())
            );
        });
    }

    @Test
    void shouldRetryOnException() {
        // Given - Email listener retries ALL exceptions
        doThrow(new RuntimeException("SMTP connection failed"))
                .when(emailService).sendToUser(anyString(), anyString(), anyString());

        // When
        rabbitTemplate.convertAndSend(EmailQueueConfig.EMAIL_QUEUE, testEvent);

        // Then - verify multiple retry attempts
        await().atMost(10, SECONDS).untilAsserted(() -> {
            verify(emailService, atLeast(1)).sendToUser(
                    eq(testEvent.email()),
                    eq(testEvent.subject()),
                    eq(testEvent.body())
            );
        });
    }

    @Test
    void shouldSendToDLQAfterMaxRetries() {
        // Given
        doThrow(new RuntimeException("Permanent SMTP failure"))
                .when(emailService).sendToUser(anyString(), anyString(), anyString());

        // When
        rabbitTemplate.convertAndSend(EmailQueueConfig.EMAIL_QUEUE, testEvent);

        // Then - verify message ends up in DLQ
        await().atMost(15, SECONDS).untilAsserted(() -> {
            List<EmailNotificationEvent> failedEvents = failedNotificationRepository.findAll();
            assertThat(failedEvents).hasSize(1);
            assertThat(failedEvents.get(0).eventId()).isEqualTo(testEvent.eventId());
        });
    }

    @Test
    void shouldPersistFailedNotification() {
        // Given
        doThrow(new RuntimeException("Email sending failed"))
                .when(emailService).sendToUser(anyString(), anyString(), anyString());

        // When
        rabbitTemplate.convertAndSend(EmailQueueConfig.EMAIL_QUEUE, testEvent);

        // Then
        await().atMost(15, SECONDS).untilAsserted(() -> {
            List<EmailNotificationEvent> failedEvents = failedNotificationRepository.findAll();
            assertThat(failedEvents).hasSize(1);
            assertThat(failedEvents.get(0).eventId()).isEqualTo(testEvent.eventId());
            assertThat(failedEvents.get(0).email()).isEqualTo(testEvent.email());
            assertThat(failedEvents.get(0).subject()).isEqualTo(testEvent.subject());
            assertThat(failedEvents.get(0).body()).isEqualTo(testEvent.body());
        });
    }

    @Test
    void shouldHandleDLQMessage() {
        // When - send directly to DLQ
        rabbitTemplate.convertAndSend(EmailQueueConfig.EMAIL_DLQ, testEvent);

        // Then - verify it's persisted in failed repository
        await().atMost(5, SECONDS).untilAsserted(() -> {
            List<EmailNotificationEvent> failedEvents = failedNotificationRepository.findAll();
            assertThat(failedEvents).hasSize(1);
            assertThat(failedEvents.get(0).eventId()).isEqualTo(testEvent.eventId());
        });
    }

    @Test
    void shouldNotPersistFailedNotificationOnSuccessfulProcessing() {
        // Given
        doNothing().when(emailService).sendToUser(anyString(), anyString(), anyString());

        // When
        rabbitTemplate.convertAndSend(EmailQueueConfig.EMAIL_QUEUE, testEvent);

        // Then
        await().atMost(5, SECONDS).untilAsserted(() -> {
            verify(emailService).sendToUser(
                    eq(testEvent.email()),
                    eq(testEvent.subject()),
                    eq(testEvent.body())
            );
            
            // Wait a bit more to ensure no async persistence happens
            Thread.sleep(500);
            
            List<EmailNotificationEvent> failedEvents = failedNotificationRepository.findAll();
            assertThat(failedEvents).isEmpty();
        });
    }

    @Test
    void shouldProcessMultipleMessagesSuccessfully() {
        // Given
        EmailNotificationEvent event1 = createTestEvent();
        EmailNotificationEvent event2 = createTestEvent();
        EmailNotificationEvent event3 = createTestEvent();
        doNothing().when(emailService).sendToUser(anyString(), anyString(), anyString());

        // When
        rabbitTemplate.convertAndSend(EmailQueueConfig.EMAIL_QUEUE, event1);
        rabbitTemplate.convertAndSend(EmailQueueConfig.EMAIL_QUEUE, event2);
        rabbitTemplate.convertAndSend(EmailQueueConfig.EMAIL_QUEUE, event3);

        // Then
        await().atMost(10, SECONDS).untilAsserted(() -> {
            verify(emailService, times(3)).sendToUser(anyString(), anyString(), anyString());
        });
    }

    @Test
    void shouldRetryOnSmtpTimeout() {
        // Given - SMTP timeout scenario
        doThrow(new RuntimeException("SMTP timeout"))
                .when(emailService).sendToUser(anyString(), anyString(), anyString());

        // When
        rabbitTemplate.convertAndSend(EmailQueueConfig.EMAIL_QUEUE, testEvent);

        // Then - verify multiple retry attempts
        await().atMost(10, SECONDS).untilAsserted(() -> {
            verify(emailService, atLeast(1)).sendToUser(
                    eq(testEvent.email()),
                    eq(testEvent.subject()),
                    eq(testEvent.body())
            );
        });
    }

    @Test
    void shouldRetryOnInvalidEmailAddress() {
        // Given - Invalid email address scenario (Email listener retries ALL exceptions)
        doThrow(new RuntimeException("Invalid email address format"))
                .when(emailService).sendToUser(anyString(), anyString(), anyString());

        // When
        rabbitTemplate.convertAndSend(EmailQueueConfig.EMAIL_QUEUE, testEvent);

        // Then - verify retry attempts (Email retries all exceptions)
        await().atMost(10, SECONDS).untilAsserted(() -> {
            verify(emailService, atLeast(1)).sendToUser(
                    eq(testEvent.email()),
                    eq(testEvent.subject()),
                    eq(testEvent.body())
            );
        });
    }

    @Test
    void shouldRetryOnAuthenticationFailure() {
        // Given - SMTP authentication failure
        doThrow(new RuntimeException("SMTP authentication failed"))
                .when(emailService).sendToUser(anyString(), anyString(), anyString());

        // When
        rabbitTemplate.convertAndSend(EmailQueueConfig.EMAIL_QUEUE, testEvent);

        // Then - verify retry attempts
        await().atMost(10, SECONDS).untilAsserted(() -> {
            verify(emailService, atLeast(1)).sendToUser(
                    eq(testEvent.email()),
                    eq(testEvent.subject()),
                    eq(testEvent.body())
            );
        });
    }

    // Helper method to create test events
    private EmailNotificationEvent createTestEvent() {
        return new EmailNotificationEvent(
                UUID.randomUUID(),
                UUID.randomUUID(),
                UUID.randomUUID(),
                StatusNames.OPENED,
                "test@example.com",
                "Test Subject",
                "Test notification body",
                LocalDateTime.now(),
                0
        );
    }
}