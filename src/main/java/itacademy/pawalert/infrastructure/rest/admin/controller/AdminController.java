package itacademy.pawalert.infrastructure.rest.admin.controller;

import itacademy.pawalert.application.service.NotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/api/admin")
public class AdminController {

    @Autowired
    private NotificationService notificationService;


    @PostMapping("/alerts/{alertId}/notify")
    public ResponseEntity<String> relaunchNotification(@PathVariable UUID alertId) {
        notificationService.relaunchNotification(alertId);
        return ResponseEntity.ok("Notificaciones reenviadas");
    }
}
