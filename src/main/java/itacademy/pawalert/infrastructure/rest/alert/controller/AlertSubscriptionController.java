package itacademy.pawalert.infrastructure.rest.alert.controller;

import itacademy.pawalert.application.alert.port.inbound.AlertSubscriptionUseCase;
import itacademy.pawalert.application.alert.port.outbound.CurrentUserProviderPort;
import itacademy.pawalert.domain.alert.model.AlertSubscription;
import itacademy.pawalert.infrastructure.rest.alert.dto.AlertSubscriptionDTO;
import itacademy.pawalert.infrastructure.rest.alert.dto.SubscribedResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@Tag(name = "Subscriptions", description = "Subscription management endpoints for managing alert subscriptions and notifications")
public class AlertSubscriptionController {
    private final AlertSubscriptionUseCase alertSubscriptionUseCase;
    private final CurrentUserProviderPort currentUserProviderPort;

    @PostMapping("/{alertId}/subscribe")
    @Operation(summary = "Subscribe to an alert", description = "Subscribes the authenticated user to a specific alert to receive notifications. Requires authentication.")
    @SecurityRequirement(name = "Bearer JWT")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Subscription created successfully",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = AlertSubscriptionDTO.class))),
            @ApiResponse(responseCode = "401", description = "Unauthorized - JWT token missing or invalid"),
            @ApiResponse(responseCode = "404", description = "Alert not found")
    })
    public ResponseEntity<AlertSubscriptionDTO> subscribe(
            @Parameter(description = "Alert ID (UUID format)", required = true, example = "550e8400-e29b-41d4-a716-446655440000")
            @PathVariable UUID alertId) {
        log.info("[SUBSCRIBE] Attempting to subscribe to alert: {}", alertId);
        UUID userId = currentUserProviderPort.getCurrentUserId();
        log.info("[SUBSCRIBE] Current user ID: {}", userId);

        if (userId == null) {
            log.error("[SUBSCRIBE] User ID is null - authentication may have failed");
            throw new IllegalStateException("User not authenticated");
        }

        AlertSubscription subscription = alertSubscriptionUseCase.subscribeToAlert(alertId, userId);
        log.info("[SUBSCRIBE] Successfully created subscription with ID: {}", subscription.id());
        return ResponseEntity.status(HttpStatus.CREATED).body(toDTO(subscription));
    }

    @DeleteMapping("/{alertId}/subscribe")
    @Operation(summary = "Unsubscribe from an alert", description = "Unsubscribes the authenticated user from a specific alert. Requires authentication.")
    @SecurityRequirement(name = "Bearer JWT")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Unsubscription completed successfully"),
            @ApiResponse(responseCode = "401", description = "Unauthorized - JWT token missing or invalid"),
            @ApiResponse(responseCode = "404", description = "Alert not found")
    })
    public ResponseEntity<Void> unsubscribe(
            @Parameter(description = "Alert ID (UUID format)", required = true, example = "550e8400-e29b-41d4-a716-446655440000")
            @PathVariable UUID alertId) {
        UUID userId = currentUserProviderPort.getCurrentUserId();
        alertSubscriptionUseCase.unsubscribeFromAlert(alertId, userId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{alertId}/subscribed")
    @Operation(summary = "Check alert subscription", description = "Checks if the authenticated user is subscribed to a specific alert. Requires authentication.")
    @SecurityRequirement(name = "Bearer JWT")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Subscription status retrieved successfully",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = SubscribedResponse.class))),
            @ApiResponse(responseCode = "401", description = "Unauthorized - JWT token missing or invalid"),
            @ApiResponse(responseCode = "404", description = "Alert not found")
    })
    public ResponseEntity<SubscribedResponse> isSubscribed(
            @Parameter(description = "Alert ID (UUID format)", required = true, example = "550e8400-e29b-41d4-a716-446655440000")
            @PathVariable UUID alertId) {
        UUID userId = currentUserProviderPort.getCurrentUserId();
        boolean subscribed = alertSubscriptionUseCase.isUserSubscribed(alertId, userId);
        return ResponseEntity.ok(new SubscribedResponse(subscribed));
    }

    @GetMapping("/subscriptions/me")
    @Operation(summary = "Get my subscriptions", description = "Retrieves all subscriptions for the authenticated user. Requires authentication.")
    @SecurityRequirement(name = "Bearer JWT")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Subscription list retrieved successfully",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = AlertSubscriptionDTO.class))),
            @ApiResponse(responseCode = "401", description = "Unauthorized - JWT token missing or invalid")
    })
    public ResponseEntity<List<AlertSubscriptionDTO>> getMySubscriptions() {
        UUID userId = currentUserProviderPort.getCurrentUserId();
        List<AlertSubscription> subscriptions = alertSubscriptionUseCase.getUserSubscriptions(userId);
        return ResponseEntity.ok(subscriptions.stream().map(this::toDTO).toList());
    }

    private AlertSubscriptionDTO toDTO(AlertSubscription subscription) {
        return new AlertSubscriptionDTO(
                subscription.id().toString(),
                subscription.alertId().toString(),
                subscription.userId().toString(),
                subscription.subscribedAt()
        );
    }
}

