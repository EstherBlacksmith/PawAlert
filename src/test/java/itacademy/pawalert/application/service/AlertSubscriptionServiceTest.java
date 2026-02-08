package itacademy.pawalert.application.service;

import itacademy.pawalert.application.exception.SubscriptionAlreadyExistsException;
import itacademy.pawalert.application.exception.SubscriptionNotFoundException;
import itacademy.pawalert.application.port.outbound.AlertSubscriptionRepositoryPort;
import itacademy.pawalert.domain.alert.model.AlertSubscription;
import itacademy.pawalert.domain.alert.model.NotificationChannel;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("AlertSubscriptionService Tests")
class AlertSubscriptionServiceTest {

    @Mock
    private AlertSubscriptionRepositoryPort subscriptionRepository;

    @InjectMocks
    private AlertSubscriptionService subscriptionService;

    private UUID alertId;
    private UUID userId;
    private AlertSubscription subscription;

    @BeforeEach
    void setUp() {
        alertId = UUID.randomUUID();
        userId = UUID.randomUUID();
        subscription = AlertSubscription.create(alertId, userId);
    }

    // ===== TEST: SUSCRIBIRSE =====

    @Test
    @DisplayName("subscribeToAlert -Success")
    void subscribeToAlert_Success() {
        // Given
        when(subscriptionRepository.existsByAlertIdAndUserId(alertId, userId))
                .thenReturn(false);
        when(subscriptionRepository.save(any(AlertSubscription.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        // When
        AlertSubscription result = subscriptionService.subscribeToAlert(alertId, userId);

        // Then
        assertNotNull(result);
        assertEquals(alertId, result.getAlertId());
        assertEquals(userId, result.getUserId());
        assertTrue(result.isActive());
        verify(subscriptionRepository).save(any(AlertSubscription.class));
    }

    @Test
    @DisplayName("subscribeToAlert - WithChannel")
    void subscribeToAlert_WithChannel() {
        // Given
        when(subscriptionRepository.existsByAlertIdAndUserId(alertId, userId))
                .thenReturn(false);
        when(subscriptionRepository.save(any(AlertSubscription.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        // When
        AlertSubscription result = subscriptionService.subscribeToAlert(
                alertId, userId, NotificationChannel.PUSH);

        // Then
        assertEquals(NotificationChannel.PUSH, result.getNotificationChannel());
    }

    @Test
    @DisplayName("subscribeToAlert - AlreadyExists")
    void subscribeToAlert_AlreadyExists_ThrowsException() {
        // Given
        when(subscriptionRepository.existsByAlertIdAndUserId(alertId, userId))
                .thenReturn(true);

        // When & Then
        assertThrows(SubscriptionAlreadyExistsException.class, () ->
                subscriptionService.subscribeToAlert(alertId, userId));

        verify(subscriptionRepository, never()).save(any());
    }

    // ===== TEST: CANCEL SUBSCRIPTION =====

    @Test
    @DisplayName("unsubscribeFromAlert - Success")
    void unsubscribeFromAlert_Success() {
        // Given
        List<AlertSubscription> subscriptions = Arrays.asList(subscription);
        when(subscriptionRepository.findByUserId(userId))
                .thenReturn(subscriptions);
        when(subscriptionRepository.save(any(AlertSubscription.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        // When
        subscriptionService.unsubscribeFromAlert(alertId, userId);

        // Then
        assertFalse(subscription.isActive());
        verify(subscriptionRepository).save(subscription);
    }

    @Test
    @DisplayName("unsubscribeFromAlert - NotFound")
    void unsubscribeFromAlert_NotFound_ThrowsException() {
        // Given
        when(subscriptionRepository.findByUserId(userId))
                .thenReturn(Arrays.asList());

        // When & Then
        assertThrows(SubscriptionNotFoundException.class, () ->
                subscriptionService.unsubscribeFromAlert(alertId, userId));
    }

    // ===== TEST: REACTIVATE SUBSCRIPTION =====

    @Test
    @DisplayName("resubscribeToAlert -Success")
    void resubscribeToAlert_Success() {
        // Given
        subscription.cancel();
        List<AlertSubscription> subscriptions = Arrays.asList(subscription);
        when(subscriptionRepository.findByUserId(userId))
                .thenReturn(subscriptions);
        when(subscriptionRepository.save(any(AlertSubscription.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        // When
        AlertSubscription result = subscriptionService.resubscribeToAlert(alertId, userId);

        // Then
        assertTrue(result.isActive());
    }

    // ===== TEST: GET SUBSCRIPTIONS =====

    @Test
    @DisplayName("getUserSubscriptions -Returns all")
    void getUserSubscriptions_ReturnsAll() {
        // Given
        AlertSubscription sub2 = AlertSubscription.create(UUID.randomUUID(), userId);
        List<AlertSubscription> subscriptions = Arrays.asList(subscription, sub2);
        when(subscriptionRepository.findByUserId(userId))
                .thenReturn(subscriptions);

        // When
        List<AlertSubscription> result = subscriptionService.getUserSubscriptions(userId);

        // Then
        assertEquals(2, result.size());
    }

    @Test
    @DisplayName("getUserActiveSubscriptions - Just the active ones")
    void getUserActiveSubscriptions_ReturnsOnlyActive() {
        // Given
        AlertSubscription inactiveSub = AlertSubscription.create(UUID.randomUUID(), userId);
        inactiveSub.cancel();List<AlertSubscription> subscriptions = Arrays.asList(subscription);

        when(subscriptionRepository.findByUserIdAndActiveTrue(userId))
                .thenReturn(subscriptions);

        // When
        List<AlertSubscription> result =
                subscriptionService.getUserActiveSubscriptions(userId);

        // Then
        assertEquals(1, result.size());
        assertTrue(result.get(0).isActive());
    }

    @Test
    @DisplayName("isUserSubscribed - Verify subscription")
    void isUserSubscribed_ReturnsTrue() {
        // Given
        when(subscriptionRepository.existsByAlertIdAndUserId(alertId, userId))
                .thenReturn(true);

        // When
        boolean result = subscriptionService.isUserSubscribed(alertId, userId);

        // Then
        assertTrue(result);
    }

    @Test
    @DisplayName("isUserSubscribed - returns not subscribed")
    void isUserSubscribed_ReturnsFalse() {
        // Given
        when(subscriptionRepository.existsByAlertIdAndUserId(alertId, userId))
                .thenReturn(false);

        // When
        boolean result = subscriptionService.isUserSubscribed(alertId, userId);

        // Then
        assertFalse(result);
    }

    // ===== TEST: CHANGE CHANEL =====

    @Test
    @DisplayName("changeNotificationChannel - Success")
    void changeNotificationChannel_Success() {
        // Given
        List<AlertSubscription> subscriptions = Arrays.asList(subscription);
        when(subscriptionRepository.findByUserId(userId))
                .thenReturn(subscriptions);
        when(subscriptionRepository.save(any(AlertSubscription.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        // When
        AlertSubscription result = subscriptionService.changeNotificationChannel(
                alertId, userId, NotificationChannel.EMAIL);

        // Then
        assertEquals(NotificationChannel.EMAIL, result.getNotificationChannel());
    }
}

