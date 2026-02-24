package itacademy.pawalert.infrastructure.notificationsenders.telegram;

import itacademy.pawalert.domain.notification.exception.TelegramNotificationException;
import itacademy.pawalert.domain.notification.model.NotificationFailureReason;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

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

    @Test
    void builder_shouldTransferTextToCaption_whenPhotoCalledAfterMessage() {
        // Arrange & Act - This is the actual usage pattern: message() is called before photo()
        TelegramRequest request = TelegramRequest.builder()
                .endpoint("sendPhoto")
                .chatId(CHAT_ID)
                .message(MESSAGE)  // Sets "text" key
                .photo(PHOTO_URL)  // Should transfer "text" to "caption"
                .build();

        // Assert - verify the request executes successfully
        when(restTemplate.postForEntity(any(String.class), any(HttpEntity.class), eq(String.class)))
                .thenReturn(ResponseEntity.ok("ok"));

        // Capture the HttpEntity to verify the body contains correct caption
        ArgumentCaptor<HttpEntity<Map<String, Object>>> captor = ArgumentCaptor.forClass(HttpEntity.class);
        
        request.execute(restTemplate, BOT_TOKEN);
        
        verify(restTemplate).postForEntity(
                eq("https://api.telegram.org/bot" + BOT_TOKEN + "/sendPhoto"),
                captor.capture(),
                eq(String.class)
        );

        Map<String, Object> body = captor.getValue().getBody();
        assertNotNull(body);
        assertEquals(PHOTO_URL, body.get("photo"));
        assertEquals(MESSAGE, body.get("caption"));  // Caption should be the message text
        assertNull(body.get("text"));  // "text" key should be removed
        assertEquals("HTML", body.get("parse_mode"));
    }

    @Test
    void builder_shouldHandlePhotoWithoutMessage() {
        // Arrange & Act - Photo without any message/caption
        TelegramRequest request = TelegramRequest.builder()
                .endpoint("sendPhoto")
                .chatId(CHAT_ID)
                .photo(PHOTO_URL)  // No message() called before
                .build();

        // Assert - verify the request executes successfully
        when(restTemplate.postForEntity(any(String.class), any(HttpEntity.class), eq(String.class)))
                .thenReturn(ResponseEntity.ok("ok"));

        ArgumentCaptor<HttpEntity<Map<String, Object>>> captor = ArgumentCaptor.forClass(HttpEntity.class);
        
        request.execute(restTemplate, BOT_TOKEN);
        
        verify(restTemplate).postForEntity(
                any(String.class),
                captor.capture(),
                eq(String.class)
        );

        Map<String, Object> body = captor.getValue().getBody();
        assertNotNull(body);
        assertEquals(PHOTO_URL, body.get("photo"));
        assertNull(body.get("caption"));  // No caption when no message was set
        assertNull(body.get("text"));
    }

    @Test
    void builder_shouldHandlePhotoCalledBeforeMessage() {
        // Arrange & Act - Alternative order: photo() before message()
        // This tests the case where message() is called AFTER photo()
        TelegramRequest request = TelegramRequest.builder()
                .endpoint("sendPhoto")
                .chatId(CHAT_ID)
                .photo(PHOTO_URL)  // Called first - no "text" exists yet
                .message(MESSAGE)  // Sets "text" key (but for photos, caption is needed)
                .build();

        // Assert - verify the request executes
        when(restTemplate.postForEntity(any(String.class), any(HttpEntity.class), eq(String.class)))
                .thenReturn(ResponseEntity.ok("ok"));

        assertDoesNotThrow(() -> request.execute(restTemplate, BOT_TOKEN));
    }
}
