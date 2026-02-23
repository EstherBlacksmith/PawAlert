package itacademy.pawalert.infrastructure.rest.admin.controller;

import itacademy.pawalert.application.notification.service.NotificationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/api/admin")
@Tag(name = "Alerts - Admin", description = "Administrative endpoints for managing all alerts (requires ADMIN role)")
public class AdminController {

    @Autowired
    private NotificationService notificationService;


    @PostMapping("/alerts/{alertId}/notify")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Reenviar notificaciones de alerta", description = "Reenvía las notificaciones de una alerta específica a todos los suscriptores. Este endpoint requiere rol ADMIN.")
    @SecurityRequirement(name = "Bearer JWT")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Notificaciones reenviadas exitosamente",
                    content = @Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "401", description = "No autorizado - Token JWT faltante o inválido"),
            @ApiResponse(responseCode = "403", description = "Prohibido - El usuario no tiene rol ADMIN"),
            @ApiResponse(responseCode = "404", description = "Alerta no encontrada")
    })
    public ResponseEntity<String> relaunchNotification(
            @Parameter(description = "ID de la alerta (formato UUID)", required = true, example = "550e8400-e29b-41d4-a716-446655440000")
            @PathVariable UUID alertId) {
        notificationService.relaunchNotification(alertId);
        return ResponseEntity.ok("Notificaciones reenviadas");
    }
}
