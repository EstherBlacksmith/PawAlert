package itacademy.pawalert.infrastructure.rest.alert.controller;

import itacademy.pawalert.application.alert.port.inbound.*;
import itacademy.pawalert.domain.alert.model.*;
import itacademy.pawalert.infrastructure.location.HybridLocationProvider;
import itacademy.pawalert.infrastructure.rest.alert.dto.AlertDTO;
import itacademy.pawalert.infrastructure.rest.alert.dto.DescriptionUpdateRequest;
import itacademy.pawalert.infrastructure.rest.alert.dto.StatusChangeRequest;
import itacademy.pawalert.infrastructure.rest.alert.dto.TitleUpdateRequest;
import itacademy.pawalert.infrastructure.rest.alert.mapper.AlertMapper;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
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
    private final HybridLocationProvider locationProvider;

    public AlertController(AlertMapper alertMapper, CreateAlertUseCase createAlertUseCase, GetAlertUseCase getAlertUseCase,
                           UpdateAlertStatusUseCase updateAlertStatusUseCase, UpdateAlertUseCase updateAlertUseCase,
                           DeleteAlertUseCase deleteAlertUseCase, SearchAlertsUseCase searchAlerts, HybridLocationProvider locationProvider) {
        this.createAlertUseCase = createAlertUseCase;
        this.getAlertUseCase = getAlertUseCase;
        this.updateAlertStatusUseCase = updateAlertStatusUseCase;
        this.updateAlertUseCase = updateAlertUseCase;
        this.alertMapper = alertMapper;
        this.deleteAlertUseCase = deleteAlertUseCase;
        this.searchAlertsUseCase = searchAlerts;
        this.locationProvider = locationProvider;
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

    @PatchMapping("/{id}/status")
    public ResponseEntity<AlertDTO> changeStatus(@PathVariable String id,
                                                 @RequestBody StatusChangeRequest request) {
        // Location is mandatory - use the location from the request directly
        GeographicLocation location = request.getLocation();
        
        if (location == null) {
            throw new IllegalArgumentException("Location is required for status changes");
        }

        logger.debug("Using location for alert {} status change: {}", id, location);

        Alert updated = updateAlertStatusUseCase.changeStatus(
                UUID.fromString(id),
                request.getNewStatus(),
                UUID.fromString(request.getUserId()),
                location
        );

        return ResponseEntity.ok(alertMapper.toDTO(updated));
    }

    @PutMapping("/{alertId}/title")
    public ResponseEntity<AlertDTO> updateTitle(
            @PathVariable String alertId,
            @Valid @RequestBody TitleUpdateRequest titleUpdateRequest) {
        Alert updated = updateAlertUseCase.updateTitle(UUID.fromString(alertId),
                UUID.fromString(titleUpdateRequest.userId().toString()), titleUpdateRequest.title());

        return ResponseEntity.ok(alertMapper.toDTO(updated));
    }

    @PutMapping("/{alertId}/description")
    public ResponseEntity<AlertDTO> updateDescription(
            @PathVariable String alertId,
            @Valid @RequestBody DescriptionUpdateRequest descriptionUpdateRequest) {
        Alert updated = updateAlertUseCase.updateDescription(UUID.fromString(alertId),
                descriptionUpdateRequest.userId(),
                descriptionUpdateRequest.description());

        return ResponseEntity.ok(alertMapper.toDTO(updated));
    }


    @GetMapping("/search")
    public ResponseEntity<List<AlertDTO>> searchAlerts(
            @RequestParam(required = false) StatusNames status,
            @RequestParam(required = false) String petName,
            @RequestParam(required = false) String species
    ) {
        List<Alert> alerts = searchAlertsUseCase.search(status, petName, species);
        return ResponseEntity.ok(alertMapper.toDTOList(alerts));
    }

    @GetMapping
    public ResponseEntity<List<AlertDTO>> getAllAlerts() {
        List<Alert> alerts = searchAlertsUseCase.search(null, null, null);
        return ResponseEntity.ok(alertMapper.toDTOList(alerts));
    }

}
