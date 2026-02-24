package itacademy.pawalert.application.notification.service;

import itacademy.pawalert.application.alert.port.outbound.AlertRepositoryPort;
import itacademy.pawalert.application.alert.service.AlertNotificationFormatter;
import itacademy.pawalert.application.exception.AlertNotFoundException;
import itacademy.pawalert.application.pet.service.PetService;
import itacademy.pawalert.application.user.port.outbound.UserRepositoryPort;
import itacademy.pawalert.domain.alert.model.Alert;
import itacademy.pawalert.domain.alert.model.StatusNames;
import itacademy.pawalert.domain.pet.model.*;
import itacademy.pawalert.domain.user.User;
import itacademy.pawalert.domain.user.exception.UserNotFoundException;
import itacademy.pawalert.domain.user.model.*;
import itacademy.pawalert.infrastructure.notificationsenders.telegram.TelegramNotificationService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TelegramNotificationUseCaseImplTest {

    @Mock
    private UserRepositoryPort userRepository;

    @Mock
    private TelegramNotificationService telegramService;

    @Mock
    private AlertNotificationFormatter formatter;

    @Mock
    private AlertRepositoryPort alertRepository;

    @Mock
    private PetService petService;

    @InjectMocks
    private TelegramNotificationUseCaseImpl telegramUseCase;

    @Test
    void notifyStatusChange_shouldSendPhotoWithCaption_whenUserHasTelegramEnabledAndPetHasImage() {
        // Arrange
        UUID userId = UUID.randomUUID();
        UUID alertId = UUID.randomUUID();
        UUID petId = UUID.randomUUID();

        User user = new User(
                userId,
                Username.of("testuser"),
                Email.of("test@example.com"),
                Surname.of("Test Surname"),
                PhoneNumber.of("123456789"),
                itacademy.pawalert.domain.user.Role.USER,
                new TelegramChatId("123456789"),
                false,  // emailNotificationsEnabled
                true    // telegramNotificationsEnabled
        );

        Alert alert = mock(Alert.class);
        lenient().when(alert.getId()).thenReturn(alertId);
        when(alert.getPetId()).thenReturn(petId);

        Pet pet = mock(Pet.class);
        when(pet.getPetImage()).thenReturn(PetImage.of("https://example.com/photo.jpg"));

        String expectedMessage = "Test notification message";

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(alertRepository.findById(alertId)).thenReturn(Optional.of(alert));
        when(petService.getPetById(petId)).thenReturn(pet);
        when(formatter.formatTelegramMessage(alert, pet, StatusNames.SEEN)).thenReturn(expectedMessage);

        // Act
        telegramUseCase.notifyStatusChange(userId, alertId, StatusNames.SEEN);

        // Assert
        verify(telegramService).sendPhotoWithCaption(
                eq("123456789"),
                eq("https://example.com/photo.jpg"),
                eq(expectedMessage)
        );
        verify(telegramService, never()).sendToUser(any(), any());
    }

    @Test
    void notifyStatusChange_shouldSendTextMessage_whenPetHasNoImage() {
        // Arrange
        UUID userId = UUID.randomUUID();
        UUID alertId = UUID.randomUUID();
        UUID petId = UUID.randomUUID();

        User user = new User(
                userId,
                Username.of("testuser"),
                Email.of("test@example.com"),
                Surname.of("Test Surname"),
                PhoneNumber.of("123456789"),
                itacademy.pawalert.domain.user.Role.USER,
                new TelegramChatId("123456789"),
                false,
                true
        );

        Alert alert = mock(Alert.class);
        lenient().when(alert.getId()).thenReturn(alertId);
        when(alert.getPetId()).thenReturn(petId);

        Pet pet = mock(Pet.class);
        when(pet.getPetImage()).thenReturn(null);

        String expectedMessage = "Test notification message";

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(alertRepository.findById(alertId)).thenReturn(Optional.of(alert));
        when(petService.getPetById(petId)).thenReturn(pet);
        when(formatter.formatTelegramMessage(alert, pet, StatusNames.SEEN)).thenReturn(expectedMessage);

        // Act
        telegramUseCase.notifyStatusChange(userId, alertId, StatusNames.SEEN);

        // Assert
        verify(telegramService).sendToUser(
                eq("123456789"),
                eq(expectedMessage)
        );
        verify(telegramService, never()).sendPhotoWithCaption(any(), any(), any());
    }

    @Test
    void notifyStatusChange_shouldSendTextMessage_whenPetImageIsEmpty() {
        // Arrange
        UUID userId = UUID.randomUUID();
        UUID alertId = UUID.randomUUID();
        UUID petId = UUID.randomUUID();

        User user = new User(
                userId,
                Username.of("testuser"),
                Email.of("test@example.com"),
                Surname.of("Test Surname"),
                PhoneNumber.of("123456789"),
                itacademy.pawalert.domain.user.Role.USER,
                new TelegramChatId("123456789"),
                false,
                true
        );

        Alert alert = mock(Alert.class);
        lenient().when(alert.getId()).thenReturn(alertId);
        when(alert.getPetId()).thenReturn(petId);

        Pet pet = mock(Pet.class);
        when(pet.getPetImage()).thenReturn(PetImage.ofNullable(""));

        String expectedMessage = "Test notification message";

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(alertRepository.findById(alertId)).thenReturn(Optional.of(alert));
        when(petService.getPetById(petId)).thenReturn(pet);
        when(formatter.formatTelegramMessage(alert, pet, StatusNames.SEEN)).thenReturn(expectedMessage);

        // Act
        telegramUseCase.notifyStatusChange(userId, alertId, StatusNames.SEEN);

        // Assert
        verify(telegramService).sendToUser(
                eq("123456789"),
                eq(expectedMessage)
        );
        verify(telegramService, never()).sendPhotoWithCaption(any(), any(), any());
    }

    @Test
    void notifyStatusChange_shouldThrowUserNotFoundException_whenUserDoesNotExist() {
        // Arrange
        UUID userId = UUID.randomUUID();
        UUID alertId = UUID.randomUUID();

        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(UserNotFoundException.class,
                () -> telegramUseCase.notifyStatusChange(userId, alertId, StatusNames.SEEN));

        verify(telegramService, never()).sendToUser(any(), any());
        verify(telegramService, never()).sendPhotoWithCaption(any(), any(), any());
    }

    @Test
    void notifyStatusChange_shouldDoNothing_whenTelegramNotificationsDisabled() {
        // Arrange
        UUID userId = UUID.randomUUID();
        UUID alertId = UUID.randomUUID();

        User userWithoutTelegram = new User(
                userId,
                Username.of("testuser"),
                Email.of("test@example.com"),
                Surname.of("Test Surname"),
                PhoneNumber.of("123456789"),
                itacademy.pawalert.domain.user.Role.USER,
                new TelegramChatId("123456789"),
                false,  // emailNotificationsEnabled
                false   // telegramNotificationsEnabled - disabled
        );

        when(userRepository.findById(userId)).thenReturn(Optional.of(userWithoutTelegram));

        // Act
        telegramUseCase.notifyStatusChange(userId, alertId, StatusNames.SEEN);

        // Assert
        verify(telegramService, never()).sendToUser(any(), any());
        verify(telegramService, never()).sendPhotoWithCaption(any(), any(), any());
    }

    @Test
    void notifyStatusChange_shouldDoNothing_whenTelegramChatIdIsNull() {
        // Arrange
        UUID userId = UUID.randomUUID();
        UUID alertId = UUID.randomUUID();

        User userWithoutChatId = new User(
                userId,
                Username.of("testuser"),
                Email.of("test@example.com"),
                Surname.of("Test Surname"),
                PhoneNumber.of("123456789"),
                itacademy.pawalert.domain.user.Role.USER,
                null,   // telegramChatId is null
                false,
                true    // telegramNotificationsEnabled
        );

        when(userRepository.findById(userId)).thenReturn(Optional.of(userWithoutChatId));

        // Act
        telegramUseCase.notifyStatusChange(userId, alertId, StatusNames.SEEN);

        // Assert
        verify(telegramService, never()).sendToUser(any(), any());
        verify(telegramService, never()).sendPhotoWithCaption(any(), any(), any());
    }

    @Test
    void notifyStatusChange_shouldDoNothing_whenTelegramChatIdIsNotLinked() {
        // Arrange
        UUID userId = UUID.randomUUID();
        UUID alertId = UUID.randomUUID();

        User userWithUnlinkedChatId = new User(
                userId,
                Username.of("testuser"),
                Email.of("test@example.com"),
                Surname.of("Test Surname"),
                PhoneNumber.of("123456789"),
                itacademy.pawalert.domain.user.Role.USER,
                TelegramChatId.of(""),  // Empty chat ID - not linked
                false,
                true    // telegramNotificationsEnabled
        );

        when(userRepository.findById(userId)).thenReturn(Optional.of(userWithUnlinkedChatId));

        // Act
        telegramUseCase.notifyStatusChange(userId, alertId, StatusNames.SEEN);

        // Assert
        verify(telegramService, never()).sendToUser(any(), any());
        verify(telegramService, never()).sendPhotoWithCaption(any(), any(), any());
    }

    @Test
    void notifyStatusChange_shouldThrowAlertNotFoundException_whenAlertDoesNotExist() {
        // Arrange
        UUID userId = UUID.randomUUID();
        UUID alertId = UUID.randomUUID();

        User user = new User(
                userId,
                Username.of("testuser"),
                Email.of("test@example.com"),
                Surname.of("Test Surname"),
                PhoneNumber.of("123456789"),
                itacademy.pawalert.domain.user.Role.USER,
                new TelegramChatId("123456789"),
                false,
                true
        );

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(alertRepository.findById(alertId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(AlertNotFoundException.class,
                () -> telegramUseCase.notifyStatusChange(userId, alertId, StatusNames.SEEN));
    }

    @Test
    void notifyStatusChange_shouldFormatMessageWithCorrectStatus() {
        // Arrange
        UUID userId = UUID.randomUUID();
        UUID alertId = UUID.randomUUID();
        UUID petId = UUID.randomUUID();

        User user = new User(
                userId,
                Username.of("testuser"),
                Email.of("test@example.com"),
                Surname.of("Test Surname"),
                PhoneNumber.of("123456789"),
                itacademy.pawalert.domain.user.Role.USER,
                new TelegramChatId("123456789"),
                false,
                true
        );

        Alert alert = mock(Alert.class);
        lenient().when(alert.getId()).thenReturn(alertId);
        when(alert.getPetId()).thenReturn(petId);

        Pet pet = mock(Pet.class);
        when(pet.getPetImage()).thenReturn(PetImage.of("https://example.com/photo.jpg"));

        String expectedMessage = "Status changed to SAFE";

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(alertRepository.findById(alertId)).thenReturn(Optional.of(alert));
        when(petService.getPetById(petId)).thenReturn(pet);
        when(formatter.formatTelegramMessage(alert, pet, StatusNames.SAFE)).thenReturn(expectedMessage);

        // Act
        telegramUseCase.notifyStatusChange(userId, alertId, StatusNames.SAFE);

        // Assert
        verify(formatter).formatTelegramMessage(alert, pet, StatusNames.SAFE);
    }

    @Test
    void notifyStatusChange_shouldHandleDifferentStatusTypes() {
        // Arrange
        UUID userId = UUID.randomUUID();
        UUID alertId = UUID.randomUUID();
        UUID petId = UUID.randomUUID();

        User user = new User(
                userId,
                Username.of("testuser"),
                Email.of("test@example.com"),
                Surname.of("Test Surname"),
                PhoneNumber.of("123456789"),
                itacademy.pawalert.domain.user.Role.USER,
                new TelegramChatId("123456789"),
                false,
                true
        );

        Alert alert = mock(Alert.class);
        lenient().when(alert.getId()).thenReturn(alertId);
        when(alert.getPetId()).thenReturn(petId);

        Pet pet = mock(Pet.class);
        when(pet.getPetImage()).thenReturn(PetImage.of("https://example.com/photo.jpg"));

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(alertRepository.findById(alertId)).thenReturn(Optional.of(alert));
        when(petService.getPetById(petId)).thenReturn(pet);

        // Test OPENED status
        when(formatter.formatTelegramMessage(alert, pet, StatusNames.OPENED)).thenReturn("Message for OPENED");
        telegramUseCase.notifyStatusChange(userId, alertId, StatusNames.OPENED);
        verify(formatter).formatTelegramMessage(alert, pet, StatusNames.OPENED);

        // Test SEEN status
        when(formatter.formatTelegramMessage(alert, pet, StatusNames.SEEN)).thenReturn("Message for SEEN");
        telegramUseCase.notifyStatusChange(userId, alertId, StatusNames.SEEN);
        verify(formatter).formatTelegramMessage(alert, pet, StatusNames.SEEN);

        // Test SAFE status
        when(formatter.formatTelegramMessage(alert, pet, StatusNames.SAFE)).thenReturn("Message for SAFE");
        telegramUseCase.notifyStatusChange(userId, alertId, StatusNames.SAFE);
        verify(formatter).formatTelegramMessage(alert, pet, StatusNames.SAFE);

        // Test CLOSED status
        when(formatter.formatTelegramMessage(alert, pet, StatusNames.CLOSED)).thenReturn("Message for CLOSED");
        telegramUseCase.notifyStatusChange(userId, alertId, StatusNames.CLOSED);
        verify(formatter).formatTelegramMessage(alert, pet, StatusNames.CLOSED);
    }
}
