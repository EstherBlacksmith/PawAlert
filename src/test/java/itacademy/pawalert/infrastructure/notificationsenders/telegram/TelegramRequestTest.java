package itacademy.pawalert.infrastructure.notificationsenders.telegram;

import itacademy.pawalert.domain.notification.exception.TelegramNotificationException;
import itacademy.pawalert.domain.notification.model.NotificationFailureReason;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TelegramRequestTest {

    @Mock
    private RestTemplate restTemplate;

    private static final String BOT_TOKEN = "test-bot-token";
    private static final String CHAT_ID = "123456789";
    private static final String MESSAGE = "Test message";
    private static final String PHOTO_URL = "https://example.com/photo.jpg";

    @Test
    void builder_shouldCreateSendMessageRequest() {
        // Arrange & Act
        TelegramRequest request = TelegramRequest.builder()
                .endpoint("sendMessage")
                .chatId(CHAT_ID)
                .message(MESSAGE)
                .build();

        // Assert - verify it doesn't throw when executed
        when(restTemplate.postForEntity(any(String.class), any(HttpEntity.class), eq(String.class)))
                .thenReturn(ResponseEntity.ok("ok"));

        assertDoesNotThrow(() -> request.execute(restTemplate, BOT_TOKEN));
    }

    @Test
    void builder_shouldCreateSendPhotoRequest() {
        // Arrange & Act
        TelegramRequest request = TelegramRequest.builder()
                .endpoint("sendPhoto")
                .chatId(CHAT_ID)
                .photo(PHOTO_URL)
                .message(MESSAGE)
                .build();

        // Assert - verify it doesn't throw when executed
        when(restTemplate.postForEntity(any(String.class), any(HttpEntity.class), eq(String.class)))
                .thenReturn(ResponseEntity.ok("ok"));

        assertDoesNotThrow(() -> request.execute(restTemplate, BOT_TOKEN));
    }

    @Test
    void execute_shouldCallRestTemplateWithCorrectUrl() {
        // Arrange
        TelegramRequest request = TelegramRequest.builder()
                .endpoint("sendMessage")
                .chatId(CHAT_ID)
                .message(MESSAGE)
                .build();

        when(restTemplate.postForEntity(any(String.class), any(HttpEntity.class), eq(String.class)))
                .thenReturn(ResponseEntity.ok("ok"));

        // Act
        request.execute(restTemplate, BOT_TOKEN);

        // Assert
        verify(restTemplate).postForEntity(
                eq("https://api.telegram.org/bot" + BOT_TOKEN + "/sendMessage"),
                any(HttpEntity.class),
                eq(String.class)
        );
    }

    @Test
    void execute_shouldThrowTelegramNotificationException_whenChatNotFound() {
        // Arrange
        TelegramRequest request = TelegramRequest.builder()
                .endpoint("sendMessage")
                .chatId(CHAT_ID)
                .message(MESSAGE)
                .build();

        when(restTemplate.postForEntity(any(String.class), any(HttpEntity.class), eq(String.class)))
                .thenThrow(new RestClientException("chat not found"));

        // Act & Assert
        TelegramNotificationException thrown = assertThrows(
                TelegramNotificationException.class,
                () -> request.execute(restTemplate, BOT_TOKEN)
        );
        assertEquals(NotificationFailureReason.CHAT_NOT_FOUND, thrown.getReason());
        assertEquals(CHAT_ID, thrown.getChatId());
    }

    @Test
    void execute_shouldThrowTelegramNotificationException_whenBotBlocked() {
        // Arrange
        TelegramRequest request = TelegramRequest.builder()
                .endpoint("sendMessage")
                .chatId(CHAT_ID)
                .message(MESSAGE)
                .build();

        when(restTemplate.postForEntity(any(String.class), any(HttpEntity.class), eq(String.class)))
                .thenThrow(new RestClientException("Forbidden: bot was blocked by the user"));

        // Act & Assert
        TelegramNotificationException thrown = assertThrows(
                TelegramNotificationException.class,
                () -> request.execute(restTemplate, BOT_TOKEN)
        );
        assertEquals(NotificationFailureReason.BOT_BLOCKED, thrown.getReason());
    }

    @Test
    void execute_shouldThrowTelegramNotificationException_whenInvalidChatId() {
        // Arrange
        TelegramRequest request = TelegramRequest.builder()
                .endpoint("sendMessage")
                .chatId(CHAT_ID)
                .message(MESSAGE)
                .build();

        when(restTemplate.postForEntity(any(String.class), any(HttpEntity.class), eq(String.class)))
                .thenThrow(new RestClientException("Bad Request: invalid chat id"));

        // Act & Assert
        TelegramNotificationException thrown = assertThrows(
                TelegramNotificationException.class,
                () -> request.execute(restTemplate, BOT_TOKEN)
        );
        assertEquals(NotificationFailureReason.INVALID_CHAT_ID, thrown.getReason());
    }

    @Test
    void execute_shouldThrowTelegramNotificationException_whenNetworkError() {
        // Arrange
        TelegramRequest request = TelegramRequest.builder()
                .endpoint("sendMessage")
                .chatId(CHAT_ID)
                .message(MESSAGE)
                .build();

        when(restTemplate.postForEntity(any(String.class), any(HttpEntity.class), eq(String.class)))
                .thenThrow(new RestClientException("I/O error: Connection refused"));

        // Act & Assert
        TelegramNotificationException thrown = assertThrows(
                TelegramNotificationException.class,
                () -> request.execute(restTemplate, BOT_TOKEN)
        );
        assertEquals(NotificationFailureReason.NETWORK_ERROR, thrown.getReason());
    }

    @Test
    void execute_shouldThrowTelegramNotificationException_whenTimeout() {
        // Arrange
        TelegramRequest request = TelegramRequest.builder()
                .endpoint("sendMessage")
                .chatId(CHAT_ID)
                .message(MESSAGE)
                .build();

        when(restTemplate.postForEntity(any(String.class), any(HttpEntity.class), eq(String.class)))
                .thenThrow(new RestClientException("timeout"));

        // Act & Assert
        TelegramNotificationException thrown = assertThrows(
                TelegramNotificationException.class,
                () -> request.execute(restTemplate, BOT_TOKEN)
        );
        assertEquals(NotificationFailureReason.NETWORK_ERROR, thrown.getReason());
    }

    @Test
    void execute_shouldThrowTelegramNotificationException_whenUnknownError() {
        // Arrange
        TelegramRequest request = TelegramRequest.builder()
                .endpoint("sendMessage")
                .chatId(CHAT_ID)
                .message(MESSAGE)
                .build();

        when(restTemplate.postForEntity(any(String.class), any(HttpEntity.class), eq(String.class)))
                .thenThrow(new RestClientException("Some unknown error"));

        // Act & Assert
        TelegramNotificationException thrown = assertThrows(
                TelegramNotificationException.class,
                () -> request.execute(restTemplate, BOT_TOKEN)
        );
        assertEquals(NotificationFailureReason.UNKNOWN, thrown.getReason());
    }

    @Test
    void execute_shouldHandleNullExceptionMessage() {
        // Arrange
        TelegramRequest request = TelegramRequest.builder()
                .endpoint("sendMessage")
                .chatId(CHAT_ID)
                .message(MESSAGE)
                .build();

        when(restTemplate.postForEntity(any(String.class), any(HttpEntity.class), eq(String.class)))
                .thenThrow(new RestClientException((String) null));

        // Act & Assert
        TelegramNotificationException thrown = assertThrows(
                TelegramNotificationException.class,
                () -> request.execute(restTemplate, BOT_TOKEN)
        );
        assertEquals(NotificationFailureReason.UNKNOWN, thrown.getReason());
    }

    @Test
    void builder_shouldHandleNullChatId() {
        // Arrange & Act
        TelegramRequest request = TelegramRequest.builder()
                .endpoint("sendMessage")
                .chatId(null)
                .message(MESSAGE)
                .build();

        when(restTemplate.postForEntity(any(String.class), any(HttpEntity.class), eq(String.class)))
                .thenReturn(ResponseEntity.ok("ok"));

        // Act & Assert - should not throw
        assertDoesNotThrow(() -> request.execute(restTemplate, BOT_TOKEN));
    }
}
