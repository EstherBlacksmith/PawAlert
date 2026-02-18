package itacademy.pawalert.infrastructure.rest.alert.controller;

import itacademy.pawalert.application.alert.port.inbound.AlertSubscriptionUseCase;

import itacademy.pawalert.application.alert.port.outbound.CurrentUserProviderPort;
import itacademy.pawalert.domain.alert.model.AlertSubscription;
import itacademy.pawalert.infrastructure.rest.alert.dto.AlertSubscriptionDTO;
import itacademy.pawalert.infrastructure.rest.alert.dto.SubscribedResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/alerts")
@RequiredArgsConstructor
public class AlertSubscriptionController {
    private final AlertSubscriptionUseCase alertSubscriptionUseCase;
    private final CurrentUserProviderPort currentUserProviderPort;

    @PostMapping("/{alertId}/subscribe")
    public ResponseEntity<AlertSubscriptionDTO> subscribe(@PathVariable UUID alertId) {
        UUID userId = currentUserProviderPort.getCurrentUserId();
        AlertSubscription subscription = alertSubscriptionUseCase.subscribeToAlert(alertId, userId);
        return ResponseEntity.status(HttpStatus.CREATED).body(toDTO(subscription));
    }

    @DeleteMapping("/{alertId}/subscribe")
    public ResponseEntity<Void> unsubscribe(@PathVariable UUID alertId) {
        UUID userId = currentUserProviderPort.getCurrentUserId();
        alertSubscriptionUseCase.unsubscribeFromAlert(alertId, userId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{alertId}/subscribed")
    public ResponseEntity<SubscribedResponse> isSubscribed(@PathVariable UUID alertId) {
        UUID userId = currentUserProviderPort.getCurrentUserId();
        boolean subscribed = alertSubscriptionUseCase.isUserSubscribed(alertId, userId);
        return ResponseEntity.ok(new SubscribedResponse(subscribed));
    }

    @GetMapping("/subscriptions/me")
    public ResponseEntity<List<AlertSubscriptionDTO>> getMySubscriptions() {
        UUID userId = currentUserProviderPort.getCurrentUserId();
        List<AlertSubscription> subscriptions = alertSubscriptionUseCase.getUserActiveSubscriptions(userId);
        return ResponseEntity.ok(subscriptions.stream().map(this::toDTO).toList());
    }

    private AlertSubscriptionDTO toDTO(AlertSubscription subscription) {
        return new AlertSubscriptionDTO(
                subscription.getId().toString(),
                subscription.getAlertId().toString(),
                subscription.getUserId().toString(),
                subscription.isActive(),
                subscription.getSubscribedAt()
        );
    }
}

