package itacademy.pawalert.infrastructure.rest.notification.controller;

import itacademy.pawalert.infrastructure.notificationsenders.sse.SseNotificationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@RestController
@RequestMapping("/api/notifications")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Subscriptions", description = "Subscription management endpoints for managing alert subscriptions and notifications")
public class SseNotificationController {

    private final SseNotificationService sseNotificationService;

    @GetMapping(value = "/stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    @Operation(summary = "Suscribirse a notificaciones en tiempo real", description = "Establece una conexión SSE (Server-Sent Events) para recibir notificaciones en tiempo real. Requiere autenticación.")
    @SecurityRequirement(name = "Bearer JWT")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Conexión SSE establecida exitosamente",
                    content = @Content(mediaType = "text/event-stream")),
            @ApiResponse(responseCode = "401", description = "No autorizado - Token JWT faltante o inválido")
    })
    public SseEmitter subscribe() {
        log.info("New SSE subscription request");
        return sseNotificationService.subscribe();
    }

    @GetMapping("/connected-count")
    @Operation(summary = "Obtener número de clientes conectados", description = "Retorna el número de clientes actualmente conectados al servicio de notificaciones SSE. Requiere autenticación.")
    @SecurityRequirement(name = "Bearer JWT")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Número de clientes conectados recuperado exitosamente",
                    content = @Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "401", description = "No autorizado - Token JWT faltante o inválido")
    })
    public int getConnectedCount() {
        return sseNotificationService.getConnectedCount();
    }
}