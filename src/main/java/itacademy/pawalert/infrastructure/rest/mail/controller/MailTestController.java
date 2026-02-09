package itacademy.pawalert.infrastructure.rest.mail.controller;

import itacademy.pawalert.infrastructure.notification.mail.EmailService;
import itacademy.pawalert.infrastructure.rest.mail.dto.SendEmailRequest;
import jakarta.mail.MessagingException;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/mail")
public class MailTestController {

    @Autowired
    private EmailService emailService;

    /**
     * Endpoint de prueba para enviar emails.
     * NOTA: En producción, debes eliminar este endpoint o protegerlo.
     */
    @PostMapping("/send")
    public ResponseEntity<Map<String, String>> sendEmail(@Valid @RequestBody SendEmailRequest request) {
        try {
            // Usamos sendHtmlEmail que es más flexible
            emailService.sendHtmlEmail(request.getTo(), request.getSubject(), request.getContent());
            return ResponseEntity.ok(Map.of(
                "status", "success",
                "message", "Email enviado correctamente a " + request.getTo()
            ));
        } catch (MessagingException e) {
            return ResponseEntity.internalServerError().body(Map.of(
                "status", "error",
                "message", "Error al enviar email: " + e.getMessage()
            ));
        }
    }

    /**
     * Endpoint simple de prueba (hardcoded).
     */
    @PostMapping("/test")
    public ResponseEntity<Map<String, String>> sendTestEmail() {
        try {
            emailService.sendEmail("Contenido de prueba");
            return ResponseEntity.ok(Map.of(
                "status", "success",
                "message", "Email de prueba enviado"
            ));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(Map.of(
                "status", "error",
                "message", "Error: " + e.getMessage()
            ));
        }
    }
}
