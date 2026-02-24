package itacademy.pawalert.infrastructure.notificationqueues.telegram;

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
 * Integration tests for {@link TelegramNotificationPublisher}.
 * Tests message publishing and serialization to RabbitMQ queue.
 */
class TelegramNotificationPublisherIntegrationTest extends AbstractRabbitMQIntegrationTest {

    @Autowired
    private TelegramNotificationPublisher publisher;

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Autowired
    private ObjectMapper objectMapper;

    private TelegramNotificationEvent testEvent;

    @BeforeEach
    void setUp() {
        // Purge the queue before each test
        rabbitTemplate.execute(channel -> {
            channel.queuePurge(TelegramQueueConfig.TELEGRAM_QUEUE);
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
            Message message = rabbitTemplate.receive(TelegramQueueConfig.TELEGRAM_QUEUE, 1000);
            assertThat(message).isNotNull();
        });
    }

    @Test
    void shouldSerializeEventCorrectly() throws Exception {
        // When
        publisher.publish(testEvent);

        // Then
        await().atMost(5, SECONDS).untilAsserted(() -> {
            Message message = rabbitTemplate.receive(TelegramQueueConfig.TELEGRAM_QUEUE, 1000);
            assertThat(message).isNotNull();
            
            String jsonBody = new String(message.getBody());
            TelegramNotificationEvent receivedEvent = objectMapper.readValue(jsonBody, TelegramNotificationEvent.class);
            
            assertThat(receivedEvent.eventId()).isEqualTo(testEvent.eventId());
            assertThat(receivedEvent.userId()).isEqualTo(testEvent.userId());
            assertThat(receivedEvent.alertId()).isEqualTo(testEvent.alertId());
            assertThat(receivedEvent.chatId()).isEqualTo(testEvent.chatId());
            assertThat(receivedEvent.message()).isEqualTo(testEvent.message());
            assertThat(receivedEvent.newStatus()).isEqualTo(testEvent.newStatus());
            assertThat(receivedEvent.photoUrl()).isEqualTo(testEvent.photoUrl());
            assertThat(receivedEvent.retryCount()).isEqualTo(testEvent.retryCount());
        });
    }

    @Test
    void shouldPublishMultipleEventsToQueue() {
        // Given
        TelegramNotificationEvent event1 = createTestEvent();
        TelegramNotificationEvent event2 = createTestEvent();
        TelegramNotificationEvent event3 = createTestEvent();

        // When
        publisher.publish(event1);
        publisher.publish(event2);
        publisher.publish(event3);

        // Then
        await().atMost(5, SECONDS).untilAsserted(() -> {
            Message message1 = rabbitTemplate.receive(TelegramQueueConfig.TELEGRAM_QUEUE, 1000);
            Message message2 = rabbitTemplate.receive(TelegramQueueConfig.TELEGRAM_QUEUE, 1000);
            Message message3 = rabbitTemplate.receive(TelegramQueueConfig.TELEGRAM_QUEUE, 1000);
            
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
            Message message = rabbitTemplate.receive(TelegramQueueConfig.TELEGRAM_QUEUE, 1000);
            assertThat(message).isNotNull();
            
            // Verify content type is JSON
            assertThat(message.getMessageProperties().getContentType()).contains("json");
            
            // Verify the message body is not empty
            assertThat(message.getBody()).isNotEmpty();
            assertThat(message.getBody().length).isGreaterThan(0);
        });
    }

    @Test
    void shouldSerializeEventWithPhotoUrl() throws Exception {
        // Given
        TelegramNotificationEvent eventWithPhoto = new TelegramNotificationEvent(
                UUID.randomUUID(),
                UUID.randomUUID(),
                UUID.randomUUID(),
                StatusNames.OPENED,
                "123456789",
                "Test message with photo",
                "https://example.com/photo.jpg",
                LocalDateTime.now(),
                0
        );

        // When
        publisher.publish(eventWithPhoto);

        // Then
        await().atMost(5, SECONDS).untilAsserted(() -> {
            Message message = rabbitTemplate.receive(TelegramQueueConfig.TELEGRAM_QUEUE, 1000);
            assertThat(message).isNotNull();
            
            String jsonBody = new String(message.getBody());
            TelegramNotificationEvent receivedEvent = objectMapper.readValue(jsonBody, TelegramNotificationEvent.class);
            
            assertThat(receivedEvent.photoUrl()).isEqualTo("https://example.com/photo.jpg");
        });
    }

    @Test
    void shouldSerializeEventWithRetryCount() throws Exception {
        // Given
        TelegramNotificationEvent eventWithRetry = new TelegramNotificationEvent(
                UUID.randomUUID(),
                UUID.randomUUID(),
                UUID.randomUUID(),
                StatusNames.OPENED,
                "123456789",
                "Test message with retry",
                null,
                LocalDateTime.now(),
                3
        );

        // When
        publisher.publish(eventWithRetry);

        // Then
        await().atMost(5, SECONDS).untilAsserted(() -> {
            Message message = rabbitTemplate.receive(TelegramQueueConfig.TELEGRAM_QUEUE, 1000);
            assertThat(message).isNotNull();
            
            String jsonBody = new String(message.getBody());
            TelegramNotificationEvent receivedEvent = objectMapper.readValue(jsonBody, TelegramNotificationEvent.class);
            
            assertThat(receivedEvent.retryCount()).isEqualTo(3);
        });
    }

    // Helper method to create test events
    private TelegramNotificationEvent createTestEvent() {
        return new TelegramNotificationEvent(
                UUID.randomUUID(),
                UUID.randomUUID(),
                UUID.randomUUID(),
                StatusNames.OPENED,
                "123456789",
                "Test notification message",
                null,
                LocalDateTime.now(),
                0
        );
    }
}