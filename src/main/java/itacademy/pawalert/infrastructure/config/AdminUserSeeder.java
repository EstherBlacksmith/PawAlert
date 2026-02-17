package itacademy.pawalert.infrastructure.config;

import itacademy.pawalert.application.user.port.outbound.UserRepositoryPort;
import itacademy.pawalert.domain.user.Role;
import itacademy.pawalert.domain.user.User;
import itacademy.pawalert.domain.user.model.Email;
import itacademy.pawalert.domain.user.model.PhoneNumber;
import itacademy.pawalert.domain.user.model.Surname;
import itacademy.pawalert.domain.user.model.Username;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.util.UUID;

/**
 * Seeder component that creates an initial admin user from environment variables.
 * 
 * This approach provides:
 * - No hardcoded credentials in code
 * - Different credentials per environment
 * - Production-safe when using secrets management (Docker secrets, Kubernetes secrets, Vault)
 * 
 * Configuration properties:
 * - app.admin.username: Admin username (default: admin)
 * - app.admin.password: Admin password (required for seeding)
 * - app.admin.email: Admin email (default: admin@pawalert.local)
 * - app.admin.surname: Admin surname/display name (default: Administrator)
 * - app.admin.phone: Admin phone number (default: +34600000000)
 * 
 * The seeder only runs when:
 * 1. The property app.admin.auto-create is set to true
 * 2. The admin password is provided (non-empty)
 * 3. No user with the same username already exists
 */
@Component
@Slf4j
@RequiredArgsConstructor
@ConditionalOnProperty(name = "app.admin.auto-create", havingValue = "true")
public class AdminUserSeeder implements CommandLineRunner {

    private final UserRepositoryPort userRepository;

    @Value("${app.admin.username:admin}")
    private String adminUsername;

    @Value("${app.admin.password:}")
    private String adminPassword;

    @Value("${app.admin.email:admin@pawalert.local}")
    private String adminEmail;

    @Value("${app.admin.surname:Administrator}")
    private String adminSurname;

    @Value("${app.admin.phone:+34600000000}")
    private String adminPhone;

    @Override
    public void run(String... args) {
        log.info("Checking if admin user seeding is required...");
        
        // Validate password is provided
        if (adminPassword == null || adminPassword.isBlank()) {
            log.warn("Admin user not created: app.admin.password is not set. " +
                     "Set the password via environment variable to enable admin seeding.");
            return;
        }

        try {
            Username username = Username.of(adminUsername);
            
            // Check if admin already exists
            if (userRepository.findByUsername(username).isPresent()) {
                log.info("Admin user '{}' already exists, skipping creation", adminUsername);
                return;
            }

            // Create admin user
            User adminUser = new User(
                UUID.randomUUID(),
                username,
                Email.of(adminEmail),
                Surname.of(adminSurname),
                PhoneNumber.of(adminPhone),
                Role.ADMIN,
                null // telegramChatId
            );

            // Save with plain password (the repository will hash it)
            userRepository.saveWithPlainPassword(adminUser, adminPassword);
            
            log.info("Admin user '{}' created successfully with email '{}'", adminUsername, adminEmail);
            log.info("IMPORTANT: Please change the default admin password after first login!");
            
        } catch (IllegalArgumentException e) {
            log.error("Failed to create admin user due to invalid configuration: {}", e.getMessage());
        } catch (Exception e) {
            log.error("Unexpected error while creating admin user: {}", e.getMessage(), e);
        }
    }


}
