package itacademy.pawalert.infrastructure.rest.alert.controller;

import itacademy.pawalert.application.alert.port.inbound.*;
import itacademy.pawalert.domain.alert.model.*;
import itacademy.pawalert.infrastructure.rest.alert.dto.*;
import itacademy.pawalert.infrastructure.rest.alert.mapper.AlertMapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/api/alerts")
@Tag(name = "Alerts", description = "Alert management endpoints for creating, retrieving, updating, and deleting pet alerts")
public class AlertController {

    private static final Logger logger = LoggerFactory.getLogger(AlertController.class);

    private final CreateAlertUseCase createAlertUseCase;
    private final GetAlertUseCase getAlertUseCase;
    private final UpdateAlertStatusUseCase updateAlertStatusUseCase;
    private final UpdateAlertUseCase updateAlertUseCase;
    private final DeleteAlertUseCase deleteAlertUseCase;
    private final AlertMapper alertMapper;
    private final SearchAlertsUseCase searchAlertsUseCase;

    public AlertController(AlertMapper alertMapper, CreateAlertUseCase createAlertUseCase, GetAlertUseCase getAlertUseCase,
                           UpdateAlertStatusUseCase updateAlertStatusUseCase, UpdateAlertUseCase updateAlertUseCase,
                           DeleteAlertUseCase deleteAlertUseCase, SearchAlertsUseCase searchAlerts) {
        this.createAlertUseCase = createAlertUseCase;
        this.getAlertUseCase = getAlertUseCase;
        this.updateAlertStatusUseCase = updateAlertStatusUseCase;
        this.updateAlertUseCase = updateAlertUseCase;
        this.alertMapper = alertMapper;
        this.deleteAlertUseCase = deleteAlertUseCase;
        this.searchAlertsUseCase = searchAlerts;
    }

    @GetMapping("/public/nearby")
    @Operation(summary = "Get nearby alerts", description = "Retrieve all active alerts within a specified radius from a geographic location. This is a public endpoint that does not require authentication.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "List of nearby alerts retrieved successfully",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = AlertDTO.class))),
            @ApiResponse(responseCode = "400", description = "Invalid latitude, longitude, or radius parameters")
    })
    @Tag(name = "Alerts - Public")
    public ResponseEntity<List<AlertDTO>> getNearbyAlerts(
            @Parameter(description = "Latitude coordinate of the search center", required = true, example = "40.4168")
            @RequestParam Double latitude,
            @Parameter(description = "Longitude coordinate of the search center", required = true, example = "-3.7038")
            @RequestParam Double longitude,
            @Parameter(description = "Search radius in kilometers", example = "10.0")
            @RequestParam(defaultValue = "10.0") Double radiusKm) {

        logger.debug("Public nearby search: lat={}, lon={}, radius={}km",
                latitude, longitude, radiusKm);

        List<Alert> alerts = searchAlertsUseCase.searchNearby(latitude, longitude, radiusKm);

        return ResponseEntity.ok(alertMapper.toDTOList(alerts));
    }

    @GetMapping("/public/active")
    @Operation(summary = "Get all active alerts", description = "Retrieve all active alerts in the system. Closed alerts are excluded. This is a public endpoint that does not require authentication.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "List of active alerts retrieved successfully",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = AlertDTO.class)))
    })
    @Tag(name = "Alerts - Public")
    public ResponseEntity<List<AlertDTO>> getActiveAlerts() {
        List<Alert> alerts = searchAlertsUseCase.search();

        // Filter out closed alerts
        List<Alert> activeAlerts = alerts.stream()
                .filter(a -> a.currentStatus().getStatusName() != StatusNames.CLOSED)
                .toList();

        return ResponseEntity.ok(alertMapper.toDTOList(activeAlerts));
    }

    @PostMapping
    @Operation(summary = "Create a new alert", description = "Create a new alert for a missing or lost pet. Requires authentication with a valid JWT token.")
    @SecurityRequirement(name = "Bearer JWT")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Alert created successfully",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = AlertDTO.class))),
            @ApiResponse(responseCode = "400", description = "Invalid alert data provided"),
            @ApiResponse(responseCode = "401", description = "Unauthorized - JWT token missing or invalid"),
            @ApiResponse(responseCode = "404", description = "Pet or user not found")
    })
    public ResponseEntity<AlertDTO> createAlert(@Valid @RequestBody AlertDTO alertDTO) {
        logger.info("[API-CONTROLLER] Received alert creation request: petId={}, userId={}, title={}",
                alertDTO.getPetId(), alertDTO.getUserId(), alertDTO.getTitle());

        GeographicLocation location = GeographicLocation.of(alertDTO.getLatitude(), alertDTO.getLongitude());

        Alert created = createAlertUseCase.createOpenedAlert(UUID.fromString(alertDTO.getPetId()),
                Title.of(alertDTO.getTitle()),
                Description.of(alertDTO.getDescription()),
                UUID.fromString(alertDTO.getUserId()),
                location
        );

        logger.info("[API-CONTROLLER] Alert creation completed: alertId={}", created.getId());

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(alertMapper.toDTO(created));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get alert by ID", description = "Retrieve a specific alert by its unique identifier.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Alert retrieved successfully",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = AlertDTO.class))),
            @ApiResponse(responseCode = "404", description = "Alert not found")
    })
    public ResponseEntity<AlertDTO> getAlert(
            @Parameter(description = "Alert ID (UUID format)", required = true, example = "550e8400-e29b-41d4-a716-446655440000")
            @PathVariable String id) {
        Alert alert = getAlertUseCase.getAlertById(UUID.fromString(id));

        return ResponseEntity.ok(alertMapper.toDTO(alert));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete an alert", description = "Delete an alert by its unique identifier. Requires authentication.")
    @SecurityRequirement(name = "Bearer JWT")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Alert deleted successfully"),
            @ApiResponse(responseCode = "401", description = "Unauthorized - JWT token missing or invalid"),
            @ApiResponse(responseCode = "404", description = "Alert not found")
    })
    public ResponseEntity<Void> deleteAlert(
            @Parameter(description = "Alert ID (UUID format)", required = true, example = "550e8400-e29b-41d4-a716-446655440000")
            @PathVariable String id) {
        deleteAlertUseCase.deleteAlertById(UUID.fromString(id));
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{id}/close")
    @Operation(summary = "Close an alert", description = "Mark an alert as closed with a closure reason. Requires authentication.")
    @SecurityRequirement(name = "Bearer JWT")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Alert closed successfully",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = AlertDTO.class))),
            @ApiResponse(responseCode = "400", description = "Invalid request data"),
            @ApiResponse(responseCode = "401", description = "Unauthorized - JWT token missing or invalid"),
            @ApiResponse(responseCode = "404", description = "Alert not found")
    })
    public ResponseEntity<AlertDTO> closeAlert(
            @Parameter(description = "Alert ID (UUID format)", required = true, example = "550e8400-e29b-41d4-a716-446655440000")
            @PathVariable String id,
            @Valid @RequestBody CloseAlertRequest request) {

        GeographicLocation location = request.getLocation();

        logger.debug("Closing alert {} with reason: {}", id, request.getClosureReason());

        Alert updated = updateAlertStatusUseCase.markAsClosed(
                UUID.fromString(id),
                UUID.fromString(request.getUserId()),
                location,
                request.getClosureReason()
        );

        return ResponseEntity.ok(alertMapper.toDTO(updated));
    }

    @PatchMapping("/{id}/status")
    @Operation(summary = "Change alert status", description = "Change the status of an alert. Use the /close endpoint to close an alert. Requires authentication.")
    @SecurityRequirement(name = "Bearer JWT")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Alert status changed successfully",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = AlertDTO.class))),
            @ApiResponse(responseCode = "400", description = "Invalid status or request data"),
            @ApiResponse(responseCode = "401", description = "Unauthorized - JWT token missing or invalid"),
            @ApiResponse(responseCode = "404", description = "Alert not found")
    })
    public ResponseEntity<AlertDTO> changeStatus(
            @Parameter(description = "Alert ID (UUID format)", required = true, example = "550e8400-e29b-41d4-a716-446655440000")
            @PathVariable String id,
            @Valid @RequestBody StatusChangeRequest request) {
        // Location is mandatory - use the location from the request directly
        GeographicLocation location = request.getLocation();

        // Prevent closing through this endpoint
        if (request.getNewStatus() == StatusNames.CLOSED) {
            throw new IllegalArgumentException(
                    "Use POST /api/alerts/{id}/close to close an alert with a closure reason");
        }

        logger.debug("Changing alert {} status to: {}", id, request.getNewStatus());

        Alert updated = updateAlertStatusUseCase.changeStatus(
                UUID.fromString(id),
                request.getNewStatus(),
                UUID.fromString(request.userId()),
                location,
                null  // No closure reason for non-close transitions
        );

        return ResponseEntity.ok(alertMapper.toDTO(updated));
    }

    @PutMapping("/{alertId}/title")
    @Operation(summary = "Update alert title", description = "Update the title of an existing alert. Requires authentication.")
    @SecurityRequirement(name = "Bearer JWT")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Alert title updated successfully",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = AlertDTO.class))),
            @ApiResponse(responseCode = "400", description = "Invalid title data"),
            @ApiResponse(responseCode = "401", description = "Unauthorized - JWT token missing or invalid"),
            @ApiResponse(responseCode = "404", description = "Alert not found")
    })
    public ResponseEntity<AlertDTO> updateTitle(
            @Parameter(description = "Alert ID (UUID format)", required = true, example = "550e8400-e29b-41d4-a716-446655440000")
            @PathVariable String alertId,
            @Valid @RequestBody TitleUpdateRequest titleUpdateRequest) {
        Alert updated = updateAlertUseCase.updateTitle(
                UUID.fromString(alertId),
                titleUpdateRequest.title()
        );

        return ResponseEntity.ok(alertMapper.toDTO(updated));
    }

    @PutMapping("/{alertId}/description")
    @Operation(summary = "Update alert description", description = "Update the description of an existing alert. Requires authentication.")
    @SecurityRequirement(name = "Bearer JWT")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Alert description updated successfully",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = AlertDTO.class))),
            @ApiResponse(responseCode = "400", description = "Invalid description data"),
            @ApiResponse(responseCode = "401", description = "Unauthorized - JWT token missing or invalid"),
            @ApiResponse(responseCode = "404", description = "Alert not found")
    })
    public ResponseEntity<AlertDTO> updateDescription(
            @Parameter(description = "Alert ID (UUID format)", required = true, example = "550e8400-e29b-41d4-a716-446655440000")
            @PathVariable String alertId,
            @Valid @RequestBody DescriptionUpdateRequest descriptionUpdateRequest) {
        Alert updated = updateAlertUseCase.updateDescription(
                UUID.fromString(alertId),
                descriptionUpdateRequest.description()
        );

        return ResponseEntity.ok(alertMapper.toDTO(updated));
    }


    @GetMapping("/search")
    @Operation(summary = "Search alerts with filters", description = "Search for alerts using various filter criteria. All parameters are optional.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Search results retrieved successfully",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = AlertDTO.class)))
    })
    public ResponseEntity<List<AlertDTO>> searchAlerts(
            @Parameter(description = "Filter by alert status")
            @RequestParam(required = false) StatusNames status,
            @Parameter(description = "Filter by alert title (partial match)")
            @RequestParam(required = false) String title,
            @Parameter(description = "Filter by pet name")
            @RequestParam(required = false) String petName,
            @Parameter(description = "Filter by pet species")
            @RequestParam(required = false) String species,
            @Parameter(description = "Filter by pet breed")
            @RequestParam(required = false) String breed,
            @Parameter(description = "Filter alerts created from this date (ISO 8601 format)")
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime createdFrom,
            @Parameter(description = "Filter alerts created until this date (ISO 8601 format)")
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime createdTo,
            @Parameter(description = "Filter alerts updated from this date (ISO 8601 format)")
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime updatedFrom,
            @Parameter(description = "Filter alerts updated until this date (ISO 8601 format)")
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime updatedTo,
            @Parameter(description = "Filter by user ID (UUID format)")
            @RequestParam(required = false) String userId
    ) {

        UUID userIdUUID = null;
        if (userId != null && !userId.isEmpty()) {
            try {
                userIdUUID = UUID.fromString(userId);
            } catch (IllegalArgumentException e) {
                logger.warn("Invalid userId format: {}", userId);
            }
        }

        List<Alert> alerts = searchAlertsUseCase.search(
                status, title, petName, species, breed,
                createdFrom, createdTo,
                updatedFrom, updatedTo,
                userIdUUID
        );

        return ResponseEntity.ok(alertMapper.toDTOList(alerts));

    }

    @GetMapping
    @Operation(summary = "Get all alerts", description = "Retrieve all alerts in the system without any filters.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "All alerts retrieved successfully",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = AlertDTO.class)))
    })
    public ResponseEntity<List<AlertDTO>> getAllAlerts() {
        List<Alert> alerts = searchAlertsUseCase.search();
        return ResponseEntity.ok(alertMapper.toDTOList(alerts));
    }

    @GetMapping("/{id}/events")
    @Operation(summary = "Get alert event history", description = "Retrieve the complete event history for a specific alert, including all status changes and updates.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Alert events retrieved successfully",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = AlertEventDTO.class))),
            @ApiResponse(responseCode = "404", description = "Alert not found")
    })
    public ResponseEntity<List<AlertEventDTO>> getAlertEvents(
            @Parameter(description = "Alert ID (UUID format)", required = true, example = "550e8400-e29b-41d4-a716-446655440000")
            @PathVariable String id) {
        List<AlertEvent> events = getAlertUseCase.getAlertHistory(UUID.fromString(id));
        return ResponseEntity.ok(alertMapper.toEventDTOList(events));
    }

    @GetMapping("/pets/{petId}/active")
    @Operation(summary = "Get active alert for a pet", description = "Retrieve the currently active alert for a specific pet, if one exists.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Active alert retrieved successfully",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = AlertDTO.class))),
            @ApiResponse(responseCode = "404", description = "No active alert found for this pet")
    })
    public ResponseEntity<AlertDTO> getActiveAlertByPetId(
            @Parameter(description = "Pet ID (UUID format)", required = true, example = "550e8400-e29b-41d4-a716-446655440000")
            @PathVariable String petId) {
        Optional<Alert> alert = getAlertUseCase.getActiveAlertByPetId(UUID.fromString(petId));
        return alert
                .map(a -> ResponseEntity.ok(alertMapper.toDTO(a)))
                .orElse(ResponseEntity.notFound().build());
    }


    @GetMapping("/admin/all")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Get all alerts (Admin only)", description = "Retrieve all alerts in the system. This endpoint requires ADMIN role.")
    @SecurityRequirement(name = "Bearer JWT")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "All alerts retrieved successfully",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = AlertDTO.class))),
            @ApiResponse(responseCode = "401", description = "Unauthorized - JWT token missing or invalid"),
            @ApiResponse(responseCode = "403", description = "Forbidden - User does not have ADMIN role")
    })
    @Tag(name = "Alerts - Admin")
    public ResponseEntity<List<AlertDTO>> getAllAlertsForAdmin() {
        List<Alert> alerts = searchAlertsUseCase.search();
        return ResponseEntity.ok(alertMapper.toDTOList(alerts));
    }
}
