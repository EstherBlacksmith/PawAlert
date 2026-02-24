package itacademy.pawalert.infrastructure.notification.telegram;

import itacademy.pawalert.domain.notification.exception.TelegramNotificationException;
import itacademy.pawalert.domain.notification.model.NotificationFailureReason;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TelegramNotificationServiceTest {

    @Mock
    private RestTemplate restTemplate;

    private TelegramNotificationService telegramService;

    private static final String BOT_TOKEN = "test-bot-token";
    private static final String CHAT_ID = "123456789";
    private static final String MESSAGE = "Test message";

    @BeforeEach
    void setUp() {
        telegramService = new TelegramNotificationService();
        ReflectionTestUtils.setField(telegramService, "restTemplate", restTemplate);
        ReflectionTestUtils.setField(telegramService, "botToken", BOT_TOKEN);
        ReflectionTestUtils.setField(telegramService, "chatId", CHAT_ID);
    }

    @Test
    void sendToUser_shouldCallRestTemplateWithCorrectParameters() {
        // Arrange
        when(restTemplate.postForEntity(any(String.class), any(HttpEntity.class), eq(String.class)))
                .thenReturn(ResponseEntity.ok("ok"));

        // Act
        telegramService.sendToUser(CHAT_ID, MESSAGE);

        // Assert
        verify(restTemplate, times(1)).postForEntity(
                eq("https://api.telegram.org/bot" + BOT_TOKEN + "/sendMessage"),
                any(HttpEntity.class),
                eq(String.class)
        );
    }

    @Test
    void sendToUser_shouldThrowException_whenRestClientFails() {
        // Arrange
        RestClientException exception = new RestClientException("chat not found");
        when(restTemplate.postForEntity(any(String.class), any(HttpEntity.class), eq(String.class)))
                .thenThrow(exception);

        // Act & Assert
        TelegramNotificationException thrown = assertThrows(
                TelegramNotificationException.class,
                () -> telegramService.sendToUser(CHAT_ID, MESSAGE)
        );
        assertEquals(NotificationFailureReason.CHAT_NOT_FOUND, thrown.getReason());
    }

    @Test
    void sendPhotoWithCaption_shouldCallRestTemplateWithCorrectParameters() {
        // Arrange
        String photoUrl = "https://example.com/photo.jpg";
        when(restTemplate.postForEntity(any(String.class), any(HttpEntity.class), eq(String.class)))
                .thenReturn(ResponseEntity.ok("ok"));

        // Act
        telegramService.sendPhotoWithCaption(CHAT_ID, photoUrl, MESSAGE);

        // Assert
        verify(restTemplate, times(1)).postForEntity(
                eq("https://api.telegram.org/bot" + BOT_TOKEN + "/sendPhoto"),
                any(HttpEntity.class),
                eq(String.class)
        );
    }

    @Test
    void sendPhotoWithCaption_shouldThrowException_whenPhotoIsBase64() {
        // Arrange
        String base64Photo = "data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAAAEAAAABCAYAAAAfFcSJAAAADUlEQVR42mNk+M9QDwADhgGAWjR9awAAAABJRU5ErkJggg==";

        // Act & Assert
        TelegramNotificationException thrown = assertThrows(
                TelegramNotificationException.class,
                () -> telegramService.sendPhotoWithCaption(CHAT_ID, base64Photo, MESSAGE)
        );
        assertEquals(NotificationFailureReason.INVALID_CHAT_ID, thrown.getReason());
        verify(restTemplate, never()).postForEntity(any(String.class), any(HttpEntity.class), eq(String.class));
    }

    @Test
    void sendPhotoWithCaption_shouldThrowException_whenRestClientFails() {
        // Arrange
        String photoUrl = "https://example.com/photo.jpg";
        RestClientException exception = new RestClientException("Forbidden: bot was blocked by the user");
        when(restTemplate.postForEntity(any(String.class), any(HttpEntity.class), eq(String.class)))
                .thenThrow(exception);

        // Act & Assert
        TelegramNotificationException thrown = assertThrows(
                TelegramNotificationException.class,
                () -> telegramService.sendPhotoWithCaption(CHAT_ID, photoUrl, MESSAGE)
        );
        assertEquals(NotificationFailureReason.BOT_BLOCKED, thrown.getReason());
    }

    @Test
    void sendToAll_shouldSendToAllValidChatIds() {
        // Arrange
        List<String> chatIds = Arrays.asList("111111111", "222222222", "333333333");
        when(restTemplate.postForEntity(any(String.class), any(HttpEntity.class), eq(String.class)))
                .thenReturn(ResponseEntity.ok("ok"));

        // Act
        telegramService.sendToAll(chatIds, MESSAGE);

        // Assert
        verify(restTemplate, times(3)).postForEntity(
                any(String.class),
                any(HttpEntity.class),
                eq(String.class)
        );
    }

    @Test
    void sendToAll_shouldFilterOutNullAndBlankChatIds() {
        // Arrange - use Arrays.asList instead of List.of to allow nulls
        List<String> chatIds = Arrays.asList("111111111", null, "", "  ", "222222222");
        when(restTemplate.postForEntity(any(String.class), any(HttpEntity.class), eq(String.class)))
                .thenReturn(ResponseEntity.ok("ok"));

        // Act
        telegramService.sendToAll(chatIds, MESSAGE);

        // Assert - only 2 valid chat IDs should trigger calls
        verify(restTemplate, times(2)).postForEntity(
                any(String.class),
                any(HttpEntity.class),
                eq(String.class)
        );
    }

    @Test
    void sendToAll_shouldHandleEmptyList() {
        // Arrange
        List<String> chatIds = List.of();

        // Act
        telegramService.sendToAll(chatIds, MESSAGE);

        // Assert
        verify(restTemplate, never()).postForEntity(
                any(String.class),
                any(HttpEntity.class),
                eq(String.class)
        );
    }
}
