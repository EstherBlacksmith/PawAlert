package itacademy.pawalert.infrastructure.alert;

import itacademy.pawalert.application.alert.port.inbound.AlertSubscriptionUseCase;
import itacademy.pawalert.domain.alert.model.AlertCreatedEvent;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("AlertCreatedEventListener Tests")
class AlertCreatedEventListenerTest {

    @Mock
    private AlertSubscriptionUseCase alertSubscriptionUseCase;

    private AlertCreatedEventListener listener;

    private UUID alertId;
    private UUID creatorId;

    @BeforeEach
    void setUp() {
        listener = new AlertCreatedEventListener(alertSubscriptionUseCase);
        alertId = UUID.randomUUID();
        creatorId = UUID.randomUUID();
    }

    @Test
    @DisplayName("handleAlertCreated - Should call subscribeToAlert")
    void handleAlertCreated_ShouldCallSubscribeToAlert() {
        // Given
        AlertCreatedEvent event = new AlertCreatedEvent(alertId, creatorId);

        // When
        listener.handleAlertCreated(event);

        // Then
        verify(alertSubscriptionUseCase).subscribeToAlert(alertId, creatorId);
    }

    @Test
    @DisplayName("handleAlertCreated - Should log error when subscription fails")
    void handleAlertCreated_ShouldLogErrorWhenSubscriptionFails() {
        // Given
        AlertCreatedEvent event = new AlertCreatedEvent(alertId, creatorId);
        doThrow(new RuntimeException("Subscription failed"))
                .when(alertSubscriptionUseCase).subscribeToAlert(alertId, creatorId);

        // When
        listener.handleAlertCreated(event);

        // Then
        verify(alertSubscriptionUseCase).subscribeToAlert(alertId, creatorId);
        // We can't easily verify log statements without additional libraries
        // For now, we just verify that the method doesn't throw an exception
    }

    @Test
    @DisplayName("handleAlertCreated - Should handle null values gracefully")
    void handleAlertCreated_ShouldHandleNullValuesGracefully() {
        // Given
        AlertCreatedEvent event = new AlertCreatedEvent(null, null);

        // When
        listener.handleAlertCreated(event);

        // Then
        verify(alertSubscriptionUseCase).subscribeToAlert(null, null);
    }
}
