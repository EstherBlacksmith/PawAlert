package itacademy.pawalert.infrastructure.rest.alert.controller;

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

@RestController
@RequestMapping("/api/alerts")
public class AlertController {
    private final AlertMapper alertMapper;
    private final AlertService alertService;

    public AlertController(AlertMapper alertMapper, AlertService alertService) {
        this.alertMapper = alertMapper;
        this.alertService = alertService;
    }

    @PostMapping
    public ResponseEntity<AlertDTO> createAlert(@RequestBody AlertDTO alertDTO) {
        Alert created = alertService.createOpenedAlert(alertDTO.getPetId(),
                alertDTO.getTitle(),
                alertDTO.getDescription(),
                alertDTO.getUserId()
        );

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(alertMapper.toDTO(created));
    }

    @GetMapping("/{id}")
    public ResponseEntity<AlertDTO> getAlert(@PathVariable String id) {
        Alert alert = alertService.findById(id);

        return ResponseEntity.ok(alertMapper.toDTO(alert));
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<AlertDTO> changeStatus(@PathVariable String id,
                                                 @RequestBody StatusChangeRequest request) {

        Alert updated = alertService.changeStatus(
                id,
                request.getNewStatus(),
                request.getUserId()
        );

        return ResponseEntity.ok(alertMapper.toDTO(updated));
    }

    @PutMapping("/{alertId}/title")
    public ResponseEntity<AlertDTO> updateTitleDescription(
            @PathVariable String alertId,
            @RequestBody TitleUpdateRequest titleUpdateRequest) {
        Alert updated = alertService.updateTitle(alertId,
                String.valueOf(titleUpdateRequest.userId().value()
                ), String.valueOf(titleUpdateRequest.title().getValue()));

        return ResponseEntity.ok(alertMapper.toDTO(updated));
    }

    @PutMapping("/{alertId}/description")
    public ResponseEntity<AlertDTO> updateTitleDescription(
            @PathVariable String alertId,
            @RequestBody DescriptionUpdateRequest descriptionUpdateRequest) {
        Alert updated = alertService.updateDescription(alertId,
                String.valueOf(descriptionUpdateRequest.userId().value()
                ), String.valueOf(descriptionUpdateRequest.description()));

        return ResponseEntity.ok(alertMapper.toDTO(updated));
    }

}
