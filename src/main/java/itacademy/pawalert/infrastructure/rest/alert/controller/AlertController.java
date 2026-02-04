package itacademy.pawalert.infrastructure.rest.alert.controller;

import itacademy.pawalert.application.port.inbound.*;
import itacademy.pawalert.application.service.AlertService;
import itacademy.pawalert.domain.alert.model.Alert;
import itacademy.pawalert.infrastructure.rest.alert.dto.AlertDTO;
import itacademy.pawalert.infrastructure.rest.alert.dto.DescriptionUpdateRequest;
import itacademy.pawalert.infrastructure.rest.alert.dto.StatusChangeRequest;
import itacademy.pawalert.infrastructure.rest.alert.dto.TitleUpdateRequest;
import itacademy.pawalert.infrastructure.rest.alert.mapper.AlertMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

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
    public ResponseEntity<AlertDTO> createAlert(@RequestBody AlertDTO alertDTO) {
        Alert created = createAlertUseCase.createOpenedAlert(alertDTO.getPetId(),
                alertDTO.getTitle(),
                alertDTO.getDescription(),
                alertDTO.getUserId()
        );

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(alertMapper.toDTO(created));
    }

    @GetMapping("/{id}")
    public ResponseEntity<AlertDTO> getAlert(@PathVariable String id) {
        Alert alert = getAlertUseCase.getAlertById(id);

        return ResponseEntity.ok(alertMapper.toDTO(alert));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteAlert(@PathVariable String id) {
         deleteAlertUseCase.deleteAlertById(id);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<AlertDTO> changeStatus(@PathVariable String id,
                                                 @RequestBody StatusChangeRequest request) {

        Alert updated = updateAlertStatusUseCase.changeStatus(
                id,
                request.getNewStatus(),
                request.getUserId()
        );

        return ResponseEntity.ok(alertMapper.toDTO(updated));
    }

    @PutMapping("/{alertId}/title")
    public ResponseEntity<AlertDTO> updateTitle(
            @PathVariable String alertId,
            @RequestBody TitleUpdateRequest titleUpdateRequest) {
        Alert updated = updateAlertUseCase.updateTitle(alertId,
                String.valueOf(titleUpdateRequest.userId().value()
                ), String.valueOf(titleUpdateRequest.title().getValue()));

        return ResponseEntity.ok(alertMapper.toDTO(updated));
    }

    @PutMapping("/{alertId}/description")
    public ResponseEntity<AlertDTO> updateDescription(
            @PathVariable String alertId,
            @RequestBody DescriptionUpdateRequest descriptionUpdateRequest) {
        Alert updated = updateAlertUseCase.updateDescription(alertId,
                String.valueOf(descriptionUpdateRequest.userId().value()
                ), String.valueOf(descriptionUpdateRequest.description()));

        return ResponseEntity.ok(alertMapper.toDTO(updated));
    }

}
