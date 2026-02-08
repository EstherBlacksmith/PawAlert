// src/test/java/itacademy/pawalert/domain/alert/model/AlertSubscriptionTest.java
package itacademy.pawalert.domain.alert.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("AlertSubscription Tests")
class AlertSubscriptionTest {

    private final UUID alertId = UUID.randomUUID();
    private final UUID userId = UUID.randomUUID();

    @Test
    @DisplayName("Crear suscripción exitosamente")
    void shouldCreateSubscription() {
        // When
        AlertSubscription subscription = AlertSubscription.create(alertId, userId);

        // Then
        assertNotNull(subscription.getId());
        assertEquals(alertId, subscription.getAlertId());
        assertEquals(userId, subscription.getUserId());
        assertTrue(subscription.isActive());
        assertNotNull(subscription.getSubscribedAt());
        assertEquals(NotificationChannel.ALL, subscription.getNotificationChannel());
    }

    @Test
    @DisplayName("Crear suscripción con canal específico")
    void shouldCreateSubscriptionWithChannel() {
        // When
        AlertSubscription subscription = AlertSubscription.create(
                alertId, userId, NotificationChannel.PUSH);

        // Then
        assertEquals(NotificationChannel.PUSH, subscription.getNotificationChannel());
    }

    @Test
    @DisplayName("Cancelar suscripción")
    void shouldCancelSubscription() {
        // Given
        AlertSubscription subscription = AlertSubscription.create(alertId, userId);

        // When
        subscription.cancel();

        // Then
        assertFalse(subscription.isActive());
    }

    @Test
    @DisplayName("Reactivar suscripción")
    void shouldReactivateSubscription() {
        // Given
        AlertSubscription subscription = AlertSubscription.create(alertId, userId);
        subscription.cancel();

        // When
        subscription.reactivate();

        // Then
        assertTrue(subscription.isActive());
    }

    @Test
    @DisplayName("Cambiar canal de notificación")
    void shouldChangeNotificationChannel() {
        // Given
        AlertSubscription subscription = AlertSubscription.create(alertId, userId);

        // When
        subscription.changeChannel(NotificationChannel.EMAIL);

        // Then
        assertEquals(NotificationChannel.EMAIL, subscription.getNotificationChannel());
    }

    @Test
    @DisplayName("Lanzar excepción cuando alertId es null")
    void shouldThrowExceptionWhenAlertIdIsNull() {
        // When & Then
        assertThrows(IllegalArgumentException.class, () ->
                AlertSubscription.create(null, userId));
    }

    @Test
    @DisplayName("Lanzar excepción cuando userId es null")
    void shouldThrowExceptionWhenUserIdIsNull() {
        // When & Then
        assertThrows(IllegalArgumentException.class, () ->
                AlertSubscription.create(alertId, null));
    }
}
