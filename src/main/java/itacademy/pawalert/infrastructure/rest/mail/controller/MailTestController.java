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
     * Test endpoint for sending emails.
     * NOTE: In production, you should remove this endpoint or protect it.
     */
    @PostMapping("/send")
    public ResponseEntity<Map<String, String>> sendEmail(@Valid @RequestBody SendEmailRequest request) {
        // We use sendHtmlEmail which is more flexible
        emailService.sendToUser(request.getTo(), request.getSubject(), request.getContent());
        return ResponseEntity.ok(Map.of(
                "status", "success",
                "message", "Email sent successfully to " + request.getTo()
        ));
    }

    /**
     * Simple test endpoint (hardcoded).
     */
    @PostMapping("/test")
    public ResponseEntity<Map<String, String>> sendTestEmail() {
        try {
            emailService.sendToUser("arikhel@gmail.com", "Test content", "Test content");
            return ResponseEntity.ok(Map.of(
                    "status", "success",
                    "message", "Test email sent"
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
                    "arikhel@gmail.com",  // <- Your email for testing
                    "Test PawAlert",
                    "<h1>Hello!</h1><p>This is a test email.</p>"
            );
            return ResponseEntity.ok(Map.of(
                    "status", "success",
                    "message", "Email sent"
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
        return ResponseEntity.ok("The endpoint is working");
    }
}
