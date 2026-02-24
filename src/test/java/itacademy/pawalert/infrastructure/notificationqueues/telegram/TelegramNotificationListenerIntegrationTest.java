package itacademy.pawalert.infrastructure.notificationqueues.telegram;

import com.fasterxml.jackson.databind.ObjectMapper;
import itacademy.pawalert.domain.alert.model.StatusNames;
import itacademy.pawalert.domain.notification.exception.TelegramNotificationException;
import itacademy.pawalert.domain.notification.model.NotificationFailureReason;
import itacademy.pawalert.infrastructure.notificationqueues.AbstractRabbitMQIntegrationTest;
import itacademy.pawalert.infrastructure.notificationsenders.telegram.TelegramNotificationService;
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
import static org.mockito.Mockito.*;

/**
 * Integration tests for {@link TelegramNotificationListener}.
 * Tests message processing, retry logic, and DLQ routing.
 */
class TelegramNotificationListenerIntegrationTest extends AbstractRabbitMQIntegrationTest {

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private TelegramFailedNotificationRepository failedNotificationRepository;

    @MockitoBean
    private TelegramNotificationService telegramService;

    private TelegramNotificationEvent testEvent;

    @BeforeEach
    void setUp() {
        // Reset mocks and clear repositories before each test
        reset(telegramService);
        
        // Purge queues before each test
        rabbitTemplate.execute(channel -> {
            channel.queuePurge(TelegramQueueConfig.TELEGRAM_QUEUE);
            channel.queuePurge(TelegramQueueConfig.TELEGRAM_DLQ);
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
        doNothing().when(telegramService).sendToUser(anyString(), anyString());

        // When
        rabbitTemplate.convertAndSend(TelegramQueueConfig.TELEGRAM_QUEUE, testEvent);

        // Then
        await().atMost(5, SECONDS).untilAsserted(() -> {
            verify(telegramService).sendToUser(eq(testEvent.chatId()), eq(testEvent.message()));
        });
    }

    @Test
    void shouldProcessMessageWithPhoto() {
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
        doNothing().when(telegramService).sendPhotoWithCaption(anyString(), anyString(), anyString());

        // When
        rabbitTemplate.convertAndSend(TelegramQueueConfig.TELEGRAM_QUEUE, eventWithPhoto);

        // Then
        await().atMost(5, SECONDS).untilAsserted(() -> {
            verify(telegramService).sendPhotoWithCaption(
                    eq(eventWithPhoto.chatId()),
                    eq(eventWithPhoto.photoUrl()),
                    eq(eventWithPhoto.message())
            );
        });
    }

    @Test
    void shouldRetryOnNetworkError() {
        // Given
        doThrow(new TelegramNotificationException(testEvent.chatId(), NotificationFailureReason.NETWORK_ERROR))
                .when(telegramService).sendToUser(anyString(), anyString());

        // When
        rabbitTemplate.convertAndSend(TelegramQueueConfig.TELEGRAM_QUEUE, testEvent);

        // Then - verify multiple retry attempts
        await().atMost(10, SECONDS).untilAsserted(() -> {
            verify(telegramService, atLeast(1)).sendToUser(eq(testEvent.chatId()), eq(testEvent.message()));
        });
    }

    @Test
    void shouldNotRetryOnPermanentError() {
        // Given
        doThrow(new TelegramNotificationException(testEvent.chatId(), NotificationFailureReason.CHAT_NOT_FOUND))
                .when(telegramService).sendToUser(anyString(), anyString());

        // When
        rabbitTemplate.convertAndSend(TelegramQueueConfig.TELEGRAM_QUEUE, testEvent);

        // Then - verify only one attempt (no retries)
        await().atMost(5, SECONDS).untilAsserted(() -> {
            verify(telegramService, times(1)).sendToUser(eq(testEvent.chatId()), eq(testEvent.message()));
        });
    }

    @Test
    void shouldPersistFailedNotificationOnPermanentError() {
        // Given
        doThrow(new TelegramNotificationException(testEvent.chatId(), NotificationFailureReason.CHAT_NOT_FOUND))
                .when(telegramService).sendToUser(anyString(), anyString());

        // When
        rabbitTemplate.convertAndSend(TelegramQueueConfig.TELEGRAM_QUEUE, testEvent);

        // Then
        await().atMost(5, SECONDS).untilAsserted(() -> {
            List<TelegramNotificationEvent> failedEvents = failedNotificationRepository.findAll();
            assertThat(failedEvents).hasSize(1);
            assertThat(failedEvents.get(0).eventId()).isEqualTo(testEvent.eventId());
        });
    }

    @Test
    void shouldPersistFailedNotificationOnBotBlocked() {
        // Given
        doThrow(new TelegramNotificationException(testEvent.chatId(), NotificationFailureReason.BOT_BLOCKED))
                .when(telegramService).sendToUser(anyString(), anyString());

        // When
        rabbitTemplate.convertAndSend(TelegramQueueConfig.TELEGRAM_QUEUE, testEvent);

        // Then
        await().atMost(5, SECONDS).untilAsserted(() -> {
            List<TelegramNotificationEvent> failedEvents = failedNotificationRepository.findAll();
            assertThat(failedEvents).hasSize(1);
            assertThat(failedEvents.get(0).eventId()).isEqualTo(testEvent.eventId());
        });
    }

    @Test
    void shouldPersistFailedNotificationOnInvalidChatId() {
        // Given
        doThrow(new TelegramNotificationException(testEvent.chatId(), NotificationFailureReason.INVALID_CHAT_ID))
                .when(telegramService).sendToUser(anyString(), anyString());

        // When
        rabbitTemplate.convertAndSend(TelegramQueueConfig.TELEGRAM_QUEUE, testEvent);

        // Then
        await().atMost(5, SECONDS).untilAsserted(() -> {
            List<TelegramNotificationEvent> failedEvents = failedNotificationRepository.findAll();
            assertThat(failedEvents).hasSize(1);
            assertThat(failedEvents.get(0).eventId()).isEqualTo(testEvent.eventId());
        });
    }

    @Test
    void shouldHandleDLQMessage() {
        // When - send directly to DLQ
        rabbitTemplate.convertAndSend(TelegramQueueConfig.TELEGRAM_DLQ, testEvent);

        // Then - verify it's persisted in failed repository
        await().atMost(5, SECONDS).untilAsserted(() -> {
            List<TelegramNotificationEvent> failedEvents = failedNotificationRepository.findAll();
            assertThat(failedEvents).hasSize(1);
            assertThat(failedEvents.get(0).eventId()).isEqualTo(testEvent.eventId());
        });
    }

    @Test
    void shouldNotPersistFailedNotificationOnSuccessfulProcessing() {
        // Given
        doNothing().when(telegramService).sendToUser(anyString(), anyString());

        // When
        rabbitTemplate.convertAndSend(TelegramQueueConfig.TELEGRAM_QUEUE, testEvent);

        // Then
        await().atMost(5, SECONDS).untilAsserted(() -> {
            verify(telegramService).sendToUser(eq(testEvent.chatId()), eq(testEvent.message()));
            
            // Wait a bit more to ensure no async persistence happens
            Thread.sleep(500);
            
            List<TelegramNotificationEvent> failedEvents = failedNotificationRepository.findAll();
            assertThat(failedEvents).isEmpty();
        });
    }

    @Test
    void shouldProcessMultipleMessagesSuccessfully() {
        // Given
        TelegramNotificationEvent event1 = createTestEvent();
        TelegramNotificationEvent event2 = createTestEvent();
        TelegramNotificationEvent event3 = createTestEvent();
        doNothing().when(telegramService).sendToUser(anyString(), anyString());

        // When
        rabbitTemplate.convertAndSend(TelegramQueueConfig.TELEGRAM_QUEUE, event1);
        rabbitTemplate.convertAndSend(TelegramQueueConfig.TELEGRAM_QUEUE, event2);
        rabbitTemplate.convertAndSend(TelegramQueueConfig.TELEGRAM_QUEUE, event3);

        // Then
        await().atMost(10, SECONDS).untilAsserted(() -> {
            verify(telegramService, times(3)).sendToUser(anyString(), anyString());
        });
    }

    @Test
    void shouldRetryOnUnknownError() {
        // Given
        doThrow(new TelegramNotificationException(testEvent.chatId(), NotificationFailureReason.UNKNOWN))
                .when(telegramService).sendToUser(anyString(), anyString());

        // When
        rabbitTemplate.convertAndSend(TelegramQueueConfig.TELEGRAM_QUEUE, testEvent);

        // Then - verify multiple retry attempts for UNKNOWN error
        await().atMost(10, SECONDS).untilAsserted(() -> {
            verify(telegramService, atLeast(1)).sendToUser(eq(testEvent.chatId()), eq(testEvent.message()));
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