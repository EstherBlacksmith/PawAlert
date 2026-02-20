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
        assertNotNull(subscription.getSubscribedAt());

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
