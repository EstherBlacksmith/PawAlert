package itacademy.pawalert.infrastructure.notificationqueues.email;

import com.fasterxml.jackson.databind.ObjectMapper;
import itacademy.pawalert.domain.alert.model.StatusNames;
import itacademy.pawalert.infrastructure.notificationqueues.AbstractRabbitMQIntegrationTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;
import static java.util.concurrent.TimeUnit.SECONDS;

/**
 * Integration tests for {@link EmailNotificationPublisher}.
 * Tests message publishing and serialization to RabbitMQ queue.
 */
class EmailNotificationPublisherIntegrationTest extends AbstractRabbitMQIntegrationTest {

    @Autowired
    private EmailNotificationPublisher publisher;

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Autowired
    private ObjectMapper objectMapper;

    private EmailNotificationEvent testEvent;

    @BeforeEach
    void setUp() {
        // Purge the queue before each test
        rabbitTemplate.execute(channel -> {
            channel.queuePurge(EmailQueueConfig.EMAIL_QUEUE);
            return null;
        });

        testEvent = createTestEvent();
    }

    @Test
    void shouldPublishEventToQueue() {
        // When
        publisher.publish(testEvent);

        // Then
        await().atMost(5, SECONDS).untilAsserted(() -> {
            Message message = rabbitTemplate.receive(EmailQueueConfig.EMAIL_QUEUE, 1000);
            assertThat(message).isNotNull();
        });
    }

    @Test
    void shouldSerializeEventCorrectly() throws Exception {
        // When
        publisher.publish(testEvent);

        // Then
        await().atMost(5, SECONDS).untilAsserted(() -> {
            Message message = rabbitTemplate.receive(EmailQueueConfig.EMAIL_QUEUE, 1000);
            assertThat(message).isNotNull();
            
            String jsonBody = new String(message.getBody());
            EmailNotificationEvent receivedEvent = objectMapper.readValue(jsonBody, EmailNotificationEvent.class);
            
            assertThat(receivedEvent.eventId()).isEqualTo(testEvent.eventId());
            assertThat(receivedEvent.userId()).isEqualTo(testEvent.userId());
            assertThat(receivedEvent.alertId()).isEqualTo(testEvent.alertId());
            assertThat(receivedEvent.email()).isEqualTo(testEvent.email());
            assertThat(receivedEvent.subject()).isEqualTo(testEvent.subject());
            assertThat(receivedEvent.body()).isEqualTo(testEvent.body());
            assertThat(receivedEvent.newStatus()).isEqualTo(testEvent.newStatus());
            assertThat(receivedEvent.retryCount()).isEqualTo(testEvent.retryCount());
        });
    }

    @Test
    void shouldPublishMultipleEventsToQueue() {
        // Given
        EmailNotificationEvent event1 = createTestEvent();
        EmailNotificationEvent event2 = createTestEvent();
        EmailNotificationEvent event3 = createTestEvent();

        // When
        publisher.publish(event1);
        publisher.publish(event2);
        publisher.publish(event3);

        // Then
        await().atMost(5, SECONDS).untilAsserted(() -> {
            Message message1 = rabbitTemplate.receive(EmailQueueConfig.EMAIL_QUEUE, 1000);
            Message message2 = rabbitTemplate.receive(EmailQueueConfig.EMAIL_QUEUE, 1000);
            Message message3 = rabbitTemplate.receive(EmailQueueConfig.EMAIL_QUEUE, 1000);
            
            assertThat(message1).isNotNull();
            assertThat(message2).isNotNull();
            assertThat(message3).isNotNull();
        });
    }

    @Test
    void shouldContainCorrectMessageProperties() {
        // When
        publisher.publish(testEvent);

        // Then
        await().atMost(5, SECONDS).untilAsserted(() -> {
            Message message = rabbitTemplate.receive(EmailQueueConfig.EMAIL_QUEUE, 1000);
            assertThat(message).isNotNull();
            
            // Verify content type is JSON
            assertThat(message.getMessageProperties().getContentType()).contains("json");
            
            // Verify the message body is not empty
            assertThat(message.getBody()).isNotEmpty();
            assertThat(message.getBody().length).isGreaterThan(0);
        });
    }

    @Test
    void shouldSerializeEventWithRetryCount() throws Exception {
        // Given
        EmailNotificationEvent eventWithRetry = new EmailNotificationEvent(
                UUID.randomUUID(),
                UUID.randomUUID(),
                UUID.randomUUID(),
                StatusNames.OPENED,
                "test@example.com",
                "Test Subject",
                "Test body with retry",
                LocalDateTime.now(),
                3
        );

        // When
        publisher.publish(eventWithRetry);

        // Then
        await().atMost(5, SECONDS).untilAsserted(() -> {
            Message message = rabbitTemplate.receive(EmailQueueConfig.EMAIL_QUEUE, 1000);
            assertThat(message).isNotNull();
            
            String jsonBody = new String(message.getBody());
            EmailNotificationEvent receivedEvent = objectMapper.readValue(jsonBody, EmailNotificationEvent.class);
            
            assertThat(receivedEvent.retryCount()).isEqualTo(3);
        });
    }

    @Test
    void shouldSerializeEventWithLongBody() throws Exception {
        // Given
        String longBody = "This is a very long email body that contains multiple lines and characters. ".repeat(10);
        EmailNotificationEvent eventWithLongBody = new EmailNotificationEvent(
                UUID.randomUUID(),
                UUID.randomUUID(),
                UUID.randomUUID(),
                StatusNames.OPENED,
                "longemail@example.com",
                "Test Subject with Long Body",
                longBody,
                LocalDateTime.now(),
                0
        );

        // When
        publisher.publish(eventWithLongBody);

        // Then
        await().atMost(5, SECONDS).untilAsserted(() -> {
            Message message = rabbitTemplate.receive(EmailQueueConfig.EMAIL_QUEUE, 1000);
            assertThat(message).isNotNull();
            
            String jsonBody = new String(message.getBody());
            EmailNotificationEvent receivedEvent = objectMapper.readValue(jsonBody, EmailNotificationEvent.class);
            
            assertThat(receivedEvent.body()).isEqualTo(longBody);
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