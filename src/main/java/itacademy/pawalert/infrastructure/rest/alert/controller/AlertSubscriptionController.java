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
    @Operation(summary = "Suscribirse a una alerta", description = "Suscribe al usuario autenticado a una alerta específica para recibir notificaciones. Requiere autenticación.")
    @SecurityRequirement(name = "Bearer JWT")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Suscripción creada exitosamente",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = AlertSubscriptionDTO.class))),
            @ApiResponse(responseCode = "401", description = "No autorizado - Token JWT faltante o inválido"),
            @ApiResponse(responseCode = "404", description = "Alerta no encontrada")
    })
    public ResponseEntity<AlertSubscriptionDTO> subscribe(
            @Parameter(description = "ID de la alerta (formato UUID)", required = true, example = "550e8400-e29b-41d4-a716-446655440000")
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
    @Operation(summary = "Desuscribirse de una alerta", description = "Desuscribe al usuario autenticado de una alerta específica. Requiere autenticación.")
    @SecurityRequirement(name = "Bearer JWT")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Desuscripción completada exitosamente"),
            @ApiResponse(responseCode = "401", description = "No autorizado - Token JWT faltante o inválido"),
            @ApiResponse(responseCode = "404", description = "Alerta no encontrada")
    })
    public ResponseEntity<Void> unsubscribe(
            @Parameter(description = "ID de la alerta (formato UUID)", required = true, example = "550e8400-e29b-41d4-a716-446655440000")
            @PathVariable UUID alertId) {
        UUID userId = currentUserProviderPort.getCurrentUserId();
        alertSubscriptionUseCase.unsubscribeFromAlert(alertId, userId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{alertId}/subscribed")
    @Operation(summary = "Verificar suscripción a alerta", description = "Verifica si el usuario autenticado está suscrito a una alerta específica. Requiere autenticación.")
    @SecurityRequirement(name = "Bearer JWT")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Estado de suscripción recuperado exitosamente",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = SubscribedResponse.class))),
            @ApiResponse(responseCode = "401", description = "No autorizado - Token JWT faltante o inválido"),
            @ApiResponse(responseCode = "404", description = "Alerta no encontrada")
    })
    public ResponseEntity<SubscribedResponse> isSubscribed(
            @Parameter(description = "ID de la alerta (formato UUID)", required = true, example = "550e8400-e29b-41d4-a716-446655440000")
            @PathVariable UUID alertId) {
        UUID userId = currentUserProviderPort.getCurrentUserId();
        boolean subscribed = alertSubscriptionUseCase.isUserSubscribed(alertId, userId);
        return ResponseEntity.ok(new SubscribedResponse(subscribed));
    }

    @GetMapping("/subscriptions/me")
    @Operation(summary = "Obtener mis suscripciones", description = "Recupera todas las suscripciones del usuario autenticado. Requiere autenticación.")
    @SecurityRequirement(name = "Bearer JWT")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista de suscripciones recuperada exitosamente",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = AlertSubscriptionDTO.class))),
            @ApiResponse(responseCode = "401", description = "No autorizado - Token JWT faltante o inválido")
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

