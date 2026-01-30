package itacademy.pawalert.infrastructure.rest;

import itacademy.pawalert.application.service.AlertService;
import itacademy.pawalert.infrastructure.persistence.AlertEntity;
import itacademy.pawalert.infrastructure.rest.dto.AlertDTO;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/alerts")
public class AlertController {
    private final AlertService alertService;

    public AlertController(AlertService alertService) {
        this.alertService = alertService;
    }

    @PostMapping
    public ResponseEntity<AlertEntity> createAlert(@RequestBody AlertDTO dto) {
        return
    }

    @GetMapping("/{id}")
    public ResponseEntity<AlertEntity> getAlert(@PathVariable Long id) {
        AlertEntity alert = alertService.findById(id);  // Lanza 404 si no existe
        return ResponseEntity.ok(alert);
    }
}
