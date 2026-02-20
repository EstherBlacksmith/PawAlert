package itacademy.pawalert.infrastructure.rest.alert.controller;

import itacademy.pawalert.application.alert.port.inbound.*;
import itacademy.pawalert.domain.alert.model.*;
import itacademy.pawalert.infrastructure.rest.alert.dto.*;
import itacademy.pawalert.infrastructure.rest.alert.mapper.AlertMapper;
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
    public ResponseEntity<List<AlertDTO>> getNearbyAlerts(
            @RequestParam Double latitude,
            @RequestParam Double longitude,
            @RequestParam(defaultValue = "10.0") Double radiusKm) {

        logger.debug("Public nearby search: lat={}, lon={}, radius={}km",
                latitude, longitude, radiusKm);

        List<Alert> alerts = searchAlertsUseCase.searchNearby(latitude, longitude, radiusKm);

        return ResponseEntity.ok(alertMapper.toDTOList(alerts));
    }

    @GetMapping("/public/active")
    public ResponseEntity<List<AlertDTO>> getActiveAlerts() {
        List<Alert> alerts = searchAlertsUseCase.search();

        // Filter out closed alerts
        List<Alert> activeAlerts = alerts.stream()
                .filter(a -> a.currentStatus().getStatusName() != StatusNames.CLOSED)
                .toList();

        return ResponseEntity.ok(alertMapper.toDTOList(activeAlerts));
    }

    @PostMapping
    public ResponseEntity<AlertDTO> createAlert(@Valid @RequestBody AlertDTO alertDTO) {
        GeographicLocation location = GeographicLocation.of(   alertDTO.getLatitude(),  alertDTO.getLongitude());

        Alert created = createAlertUseCase.createOpenedAlert(UUID.fromString(alertDTO.getPetId()),
                Title.of(alertDTO.getTitle()),
                Description.of( alertDTO.getDescription()),
                UUID.fromString(alertDTO.getUserId()),
                location
        );

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(alertMapper.toDTO(created));
    }

    @GetMapping("/{id}")
    public ResponseEntity<AlertDTO> getAlert(@PathVariable String id) {
        Alert alert = getAlertUseCase.getAlertById(UUID.fromString(id));

        return ResponseEntity.ok(alertMapper.toDTO(alert));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteAlert(@PathVariable String id) {
        deleteAlertUseCase.deleteAlertById(UUID.fromString(id));
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{id}/close")
    public ResponseEntity<AlertDTO> closeAlert(
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
    public ResponseEntity<AlertDTO> changeStatus(@PathVariable String id,
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
    public ResponseEntity<AlertDTO> updateTitle(
            @PathVariable String alertId,
            @Valid @RequestBody TitleUpdateRequest titleUpdateRequest) {
        Alert updated = updateAlertUseCase.updateTitle(
                UUID.fromString(alertId),
                titleUpdateRequest.title()
        );

        return ResponseEntity.ok(alertMapper.toDTO(updated));
    }

    @PutMapping("/{alertId}/description")
    public ResponseEntity<AlertDTO> updateDescription(
            @PathVariable String alertId,
            @Valid @RequestBody DescriptionUpdateRequest descriptionUpdateRequest) {
        Alert updated = updateAlertUseCase.updateDescription(
                UUID.fromString(alertId),
                descriptionUpdateRequest.description()
        );

        return ResponseEntity.ok(alertMapper.toDTO(updated));
    }


    @GetMapping("/search")
    public ResponseEntity<List<AlertDTO>> searchAlerts(
            @RequestParam(required = false) StatusNames status,
            @RequestParam(required = false) String title,
            @RequestParam(required = false) String petName,
            @RequestParam(required = false) String species,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime createdFrom,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime createdTo,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime updatedFrom,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime updatedTo
    ) {

        List<Alert> alerts = searchAlertsUseCase.search(
                status, title, petName, species,
                createdFrom, createdTo,
                updatedFrom, updatedTo
        );

        return ResponseEntity.ok(alertMapper.toDTOList(alerts));

    }

    @GetMapping
    public ResponseEntity<List<AlertDTO>> getAllAlerts() {
        List<Alert> alerts = searchAlertsUseCase.search();
        return ResponseEntity.ok(alertMapper.toDTOList(alerts));
    }

    @GetMapping("/{id}/events")
    public ResponseEntity<List<AlertEventDTO>> getAlertEvents(@PathVariable String id) {
        List<AlertEvent> events = getAlertUseCase.getAlertHistory(UUID.fromString(id));
        return ResponseEntity.ok(alertMapper.toEventDTOList(events));
    }

    @GetMapping("/pets/{petId}/active")
    public ResponseEntity<AlertDTO> getActiveAlertByPetId(@PathVariable String petId) {
        Optional<Alert> alert = getAlertUseCase.getActiveAlertByPetId(UUID.fromString(petId));
        return alert
                .map(a -> ResponseEntity.ok(alertMapper.toDTO(a)))
                .orElse(ResponseEntity.notFound().build());
    }


    @GetMapping("/admin/all")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<AlertDTO>> getAllAlertsForAdmin() {
        List<Alert> alerts = searchAlertsUseCase.search();
        return ResponseEntity.ok(alertMapper.toDTOList(alerts));
    }
}
