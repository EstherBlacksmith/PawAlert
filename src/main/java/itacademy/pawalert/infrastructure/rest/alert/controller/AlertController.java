package itacademy.pawalert.infrastructure.rest.alert.controller;

import itacademy.pawalert.application.port.inbound.*;
import itacademy.pawalert.application.service.AlertService;
import itacademy.pawalert.domain.alert.model.Alert;
import itacademy.pawalert.domain.alert.model.Description;
import itacademy.pawalert.domain.alert.model.StatusNames;
import itacademy.pawalert.domain.alert.model.Title;
import itacademy.pawalert.infrastructure.rest.alert.dto.AlertDTO;
import itacademy.pawalert.infrastructure.rest.alert.dto.DescriptionUpdateRequest;
import itacademy.pawalert.infrastructure.rest.alert.dto.StatusChangeRequest;
import itacademy.pawalert.infrastructure.rest.alert.dto.TitleUpdateRequest;
import itacademy.pawalert.infrastructure.rest.alert.mapper.AlertMapper;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/alerts")
public class AlertController {
    private final CreateAlertUseCase createAlertUseCase;
    private final GetAlertUseCase getAlertUseCase;
    private final UpdateAlertStatusUseCase updateAlertStatusUseCase;
    private final UpdateAlertUseCase updateAlertUseCase;
    private final DeleteAlertUseCase deleteAlertUseCase;
    private final AlertMapper alertMapper;

    public AlertController(AlertMapper alertMapper, AlertService alertService, CreateAlertUseCase createAlertUseCase, GetAlertUseCase getAlertUseCase, UpdateAlertStatusUseCase updateAlertStatusUseCase, UpdateAlertUseCase updateAlertUseCase, DeleteAlertUseCase deleteAlertUseCase) {
        this.createAlertUseCase = createAlertUseCase;
        this.getAlertUseCase = getAlertUseCase;
        this.updateAlertStatusUseCase = updateAlertStatusUseCase;
        this.updateAlertUseCase = updateAlertUseCase;
        this.alertMapper = alertMapper;
        this.deleteAlertUseCase = deleteAlertUseCase;
    }

    @PostMapping
    public ResponseEntity<AlertDTO> createAlert(@Valid @RequestBody AlertDTO alertDTO) {
        Alert created = createAlertUseCase.createOpenedAlert(UUID.fromString(alertDTO.getPetId()),
                Title.of(alertDTO.getTitle()),
               Description.of( alertDTO.getDescription()),
                UUID.fromString(alertDTO.getUserId())
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
                                                 @Valid @RequestBody StatusChangeRequest request) {

        Alert updated = updateAlertStatusUseCase.changeStatus(
                UUID.fromString(id),
                request.getNewStatus(),
                UUID.fromString(request.getUserId())
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
        List<Alert> alerts = alertService.searchAlerts(status, petName, species);
        return ResponseEntity.ok(AlertMapper.INSTANCE.toDTOList(alerts));
    }

    @GetMapping("/pet/{petName}")
    public ResponseEntity<List<AlertDTO>> getAlertsByPetName(
            @PathVariable String petName
    ) {
        List<Alert> alerts = alertService.findAlertsByPetName(petName);
        return ResponseEntity.ok(AlertMapper.INSTANCE.toDTOList(alerts));
    }
}
