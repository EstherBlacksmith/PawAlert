package itacademy.pawalert.infrastructure.rest.mail.controller;

import itacademy.pawalert.infrastructure.notificationsenders.email.EmailServiceImpl;
import itacademy.pawalert.infrastructure.rest.mail.dto.SendEmailRequest;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/mail")
public class MailTestController {

    @Autowired
    private EmailServiceImpl emailService;

    /**
     * Endpoint de prueba para enviar emails.
     * NOTA: En producción, debes eliminar este endpoint o protegerlo.
     */
    @PostMapping("/send")
    public ResponseEntity<Map<String, String>> sendEmail(@Valid @RequestBody SendEmailRequest request) {
        // Usamos sendHtmlEmail que es más flexible
        emailService.sendToUser(request.getTo(), request.getSubject(), request.getContent());
        return ResponseEntity.ok(Map.of(
                "status", "success",
                "message", "Email enviado correctamente a " + request.getTo()
        ));
    }

    /**
     * Endpoint simple de prueba (hardcoded).
     */
    @PostMapping("/test")
    public ResponseEntity<Map<String, String>> sendTestEmail() {
        try {
            emailService.sendToUser("arikhel@gmail.com", "Contenido de prueba", "Contenido de prueba");
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


    @PostMapping("/send-direct")
    public ResponseEntity<Map<String, String>> sendDirectEmail() {
        try {
            emailService.sendToUser(
                    "arikhel@gmail.com",  // ← Tu email para probar
                    "Test PawAlert",
                    "<h1>¡Hola!</h1><p>Este es un email de prueba.</p>"
            );
            return ResponseEntity.ok(Map.of(
                    "status", "success",
                    "message", "Email enviado"
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                    "status", "error",
                    "message", e.getMessage()
            ));
        }
    }

    @GetMapping("/test-simple")
    public ResponseEntity<String> testSimple() {
        return ResponseEntity.ok("El endpoint funciona");
    }
}
