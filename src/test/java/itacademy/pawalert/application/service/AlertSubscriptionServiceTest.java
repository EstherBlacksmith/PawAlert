package itacademy.pawalert.application.service;

import itacademy.pawalert.application.alert.service.AlertSubscriptionService;
import itacademy.pawalert.application.exception.SubscriptionAlreadyExistsException;
import itacademy.pawalert.application.exception.SubscriptionNotFoundException;
import itacademy.pawalert.application.alert.port.outbound.AlertRepositoryPort;
import itacademy.pawalert.application.alert.port.outbound.AlertSubscriptionRepositoryPort;
import itacademy.pawalert.domain.alert.model.Alert;
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
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("AlertSubscriptionService Tests")
class AlertSubscriptionServiceTest {

    @Mock
    private AlertSubscriptionRepositoryPort subscriptionRepository;

    @Mock
    private AlertRepositoryPort alertRepository;

    @InjectMocks
    private AlertSubscriptionService subscriptionService;

    private UUID alertId;
    private UUID userId;
    private AlertSubscription subscription;
    private Alert alert;

    @BeforeEach
    void setUp() {
        alertId = UUID.randomUUID();
        userId = UUID.randomUUID();
        subscription = AlertSubscription.create(alertId, userId);
        alert = TestAlertFactory.createTestAlert(alertId);
    }

    // ===== TEST: SUSCRIBIRSE =====

    @Test
    @DisplayName("subscribeToAlert -Success")
    void subscribeToAlert_Success() {
        // Given
        when(alertRepository.findById(alertId)).thenReturn(Optional.of(alert));
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
        verify(subscriptionRepository).save(any(AlertSubscription.class));
    }



    @Test
    @DisplayName("subscribeToAlert - AlreadyExists")
    void subscribeToAlert_AlreadyExists_ThrowsException() {
        // Given
        when(alertRepository.findById(alertId)).thenReturn(Optional.of(alert));
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

        // When
        subscriptionService.unsubscribeFromAlert(alertId, userId);

        // Then
        verify(subscriptionRepository).deleteById(subscription.getId());
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


}

