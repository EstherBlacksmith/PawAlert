package itacademy.pawalert.infrastructure.rest;

import itacademy.pawalert.application.service.AlertService;
import itacademy.pawalert.domain.Alert;
import itacademy.pawalert.infrastructure.rest.dto.AlertDTO;
import itacademy.pawalert.infrastructure.rest.dto.DescriptionUpdateRequest;
import itacademy.pawalert.infrastructure.rest.dto.StatusChangeRequest;
import itacademy.pawalert.infrastructure.rest.dto.TitleUpdateRequest;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/alerts")
public class AlertController {
    private final AlertService alertService;

    public AlertController(AlertService alertService) {
        this.alertService = alertService;
    }

    @PostMapping
    public Alert createAlert(@RequestBody AlertDTO alertDTO) {
        return alertService.createOpenedAlert(alertDTO.getPetId(),
                alertDTO.getTitle(),
                alertDTO.getDescription(),
                alertDTO.getUserId()
        );

    }

    @GetMapping("/{id}")
    public Alert getAlert(@PathVariable String id) {
        return alertService.findById(id);
    }

    @PatchMapping("/{id}/status")
    public Alert changeStatus(@PathVariable String id,
                              @RequestBody StatusChangeRequest request) {
        // Pass the userId making the change
        return alertService.changeStatus(
                id,
                request.getNewStatus(),
                request.getUserId()
        );
    }

    @PutMapping("/{alertId}/title")
    public Alert updateTitleDescription(
            @PathVariable String alertId,
            @RequestBody TitleUpdateRequest titleUpdateRequest
    ) {
        return alertService.updateTitle(alertId,
                String.valueOf(titleUpdateRequest.userId().value()
                ), String.valueOf(titleUpdateRequest.title().getValue()));
    }

    @PutMapping("/{alertId}/description")
    public Alert updateTitleDescription(
            @PathVariable String alertId,
            @RequestBody DescriptionUpdateRequest descriptionUpdateRequest
    ) {
        return alertService.updateDescription(alertId,
                String.valueOf(descriptionUpdateRequest.userId()
                ), String.valueOf(descriptionUpdateRequest.description()));
    }

}
