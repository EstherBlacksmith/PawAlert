package itacademy.pawalert.infrastructure.rest.integration;

import itacademy.pawalert.application.alert.port.inbound.GetAlertUseCase;
import itacademy.pawalert.application.pet.port.inbound.GetPetUseCase;
import itacademy.pawalert.application.user.port.inbound.GetUserUseCase;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.*;

/**
 * Controller for integration testing without authentication.
 * This should only be used for development/testing purposes.
 */
@RestController
@RequestMapping("/api/test")
public class IntegrationTestController {

    private final GetAlertUseCase getAlertUseCase;
    private final GetPetUseCase getPetUseCase;
    private final GetUserUseCase getUserUseCase;

    public IntegrationTestController(GetAlertUseCase getAlertUseCase,
                                     GetPetUseCase getPetUseCase,
                                     GetUserUseCase getUserUseCase) {
        this.getAlertUseCase = getAlertUseCase;
        this.getPetUseCase = getPetUseCase;
        this.getUserUseCase = getUserUseCase;
    }

    /**
     * Test user registration endpoint
     */
    @PostMapping("/register")
    public ResponseEntity<Map<String, Object>> testRegister(@RequestBody Map<String, String> request) {
        Map<String, Object> response = new HashMap<>();
        response.put("status", "success");
        response.put("message", "User registration endpoint is accessible");
        response.put("receivedData", request);
        response.put("timestamp", LocalDateTime.now().toString());
        return ResponseEntity.ok(response);
    }

    /**
     * Test user login endpoint
     */
    @PostMapping("/login")
    public ResponseEntity<Map<String, Object>> testLogin(@RequestBody Map<String, String> request) {
        Map<String, Object> response = new HashMap<>();
        response.put("status", "success");
        response.put("message", "Login endpoint is accessible");
        response.put("token", "test-jwt-token-for-testing");
        response.put("timestamp", LocalDateTime.now().toString());
        return ResponseEntity.ok(response);
    }

    /**
     * Test get user by ID (simulated - doesn't require real user)
     */
    @GetMapping("/users/{userId}")
    public ResponseEntity<Map<String, Object>> testGetUser(@PathVariable String userId) {
        Map<String, Object> response = new HashMap<>();
        response.put("status", "success");
        response.put("message", "User endpoint is accessible");
        response.put("testUserId", userId);
        response.put("note", "Full functionality requires authentication");
        return ResponseEntity.ok(response);
    }

    /**
     * Test get pet by ID (simulated)
     */
    @GetMapping("/pets/{petId}")
    public ResponseEntity<Map<String, Object>> testGetPet(@PathVariable String petId) {
        Map<String, Object> response = new HashMap<>();
        response.put("status", "success");
        response.put("message", "Pet endpoint is accessible");
        response.put("testPetId", petId);
        response.put("note", "Full functionality requires authentication");
        return ResponseEntity.ok(response);
    }

    /**
     * Test get alert by ID (simulated)
     */
    @GetMapping("/alerts/{alertId}")
    public ResponseEntity<Map<String, Object>> testGetAlert(@PathVariable String alertId) {
        Map<String, Object> response = new HashMap<>();
        response.put("status", "success");
        response.put("message", "Alert endpoint is accessible");
        response.put("testAlertId", alertId);
        response.put("note", "Full functionality requires authentication");
        return ResponseEntity.ok(response);
    }

    /**
     * Get list of all available endpoints
     */
    @GetMapping("/endpoints")
    public ResponseEntity<Map<String, Object>> getAvailableEndpoints() {
        Map<String, Object> response = new HashMap<>();
        
        Map<String, String> authEndpoints = new LinkedHashMap<>();
        authEndpoints.put("POST /api/auth/login", "User login");
        authEndpoints.put("POST /api/users/register", "User registration");
        
        Map<String, String> userEndpoints = new LinkedHashMap<>();
        userEndpoints.put("GET /api/users/{userId}", "Get user by ID (requires auth)");
        userEndpoints.put("PUT /api/users/{userId}/username", "Update username");
        userEndpoints.put("PUT /api/users/{userId}/password", "Change password");
        userEndpoints.put("DELETE /api/users/{userId}", "Delete user");
        
        Map<String, String> petEndpoints = new LinkedHashMap<>();
        petEndpoints.put("POST /api/pets", "Create pet (requires auth)");
        petEndpoints.put("GET /api/pets/{id}", "Get pet by ID (requires auth)");
        petEndpoints.put("PUT /api/pets/{id}", "Update pet (requires auth)");
        petEndpoints.put("DELETE /api/pets/{id}", "Delete pet (requires auth)");
        
        Map<String, String> alertEndpoints = new LinkedHashMap<>();
        alertEndpoints.put("POST /api/alerts", "Create alert (requires auth)");
        alertEndpoints.put("GET /api/alerts", "Get all alerts");
        alertEndpoints.put("GET /api/alerts/{id}", "Get alert by ID");
        alertEndpoints.put("GET /api/alerts/search", "Search alerts");
        alertEndpoints.put("PATCH /api/alerts/{id}/status", "Update alert status");
        alertEndpoints.put("DELETE /api/alerts/{id}", "Delete alert");
        
        Map<String, String> notificationEndpoints = new LinkedHashMap<>();
        notificationEndpoints.put("POST /api/mail/test", "Send test email");
        notificationEndpoints.put("POST /api/telegram/test", "Send test Telegram message");
        notificationEndpoints.put("POST /api/admin/alerts/{id}/notify", "Relaunch notifications");
        
        response.put("auth", authEndpoints);
        response.put("users", userEndpoints);
        response.put("pets", petEndpoints);
        response.put("alerts", alertEndpoints);
        response.put("notifications", notificationEndpoints);
        response.put("note", "Most endpoints require JWT authentication with ADMIN role");
        
        return ResponseEntity.ok(response);
    }

    /**
     * Check API health status
     */
    @GetMapping("/health")
    public ResponseEntity<Map<String, Object>> healthCheck() {
        Map<String, Object> response = new HashMap<>();
        response.put("status", "UP");
        response.put("application", "PawAlert");
        response.put("version", "1.0.0");
        response.put("timestamp", LocalDateTime.now().toString());
        response.put("endpoints", "Use /api/test/endpoints to see all available endpoints");
        return ResponseEntity.ok(response);
    }
}
