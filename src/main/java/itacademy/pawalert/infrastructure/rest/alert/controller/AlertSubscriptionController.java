package itacademy.pawalert.infrastructure.rest.alert.controller;

import itacademy.pawalert.application.alert.port.inbound.AlertSubscriptionUseCase;

import itacademy.pawalert.application.alert.port.outbound.CurrentUserProviderPort;
import itacademy.pawalert.domain.alert.model.AlertSubscription;
import itacademy.pawalert.infrastructure.rest.alert.dto.AlertSubscriptionDTO;
import itacademy.pawalert.infrastructure.rest.alert.dto.SubscribedResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/api/alerts")
@RequiredArgsConstructor
public class AlertSubscriptionController {
    private final AlertSubscriptionUseCase alertSubscriptionUseCase;
    private final CurrentUserProviderPort currentUserProviderPort;

    @PostMapping("/{alertId}/subscribe")
    public ResponseEntity<AlertSubscriptionDTO> subscribe(@PathVariable UUID alertId) {
        log.info("[SUBSCRIBE] Attempting to subscribe to alert: {}", alertId);
        UUID userId = currentUserProviderPort.getCurrentUserId();
        log.info("[SUBSCRIBE] Current user ID: {}", userId);
        
        if (userId == null) {
            log.error("[SUBSCRIBE] User ID is null - authentication may have failed");
            throw new IllegalStateException("User not authenticated");
        }
        
        AlertSubscription subscription = alertSubscriptionUseCase.subscribeToAlert(alertId, userId);
        log.info("[SUBSCRIBE] Successfully created subscription with ID: {}", subscription.getId());
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
        List<AlertSubscription> subscriptions = alertSubscriptionUseCase.getUserSubscriptions(userId);
        return ResponseEntity.ok(subscriptions.stream().map(this::toDTO).toList());
    }

    private AlertSubscriptionDTO toDTO(AlertSubscription subscription) {
        return new AlertSubscriptionDTO(
                subscription.getId().toString(),
                subscription.getAlertId().toString(),
                subscription.getUserId().toString(),
                subscription.getSubscribedAt()
        );
    }
}

