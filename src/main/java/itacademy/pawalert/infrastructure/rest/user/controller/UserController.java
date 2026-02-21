package itacademy.pawalert.infrastructure.rest.user.controller;

import itacademy.pawalert.application.user.port.inbound.*;
import itacademy.pawalert.domain.user.Role;
import itacademy.pawalert.domain.user.User;
import itacademy.pawalert.domain.user.model.*;
import itacademy.pawalert.infrastructure.rest.user.dto.ChangePasswordRequest;
import itacademy.pawalert.infrastructure.rest.user.dto.RegistrationInput;
import itacademy.pawalert.infrastructure.rest.user.dto.UpdateUserRequest;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private static final Logger logger = LoggerFactory.getLogger(UserController.class);

    private final CreateUserUseCase createUserUseCase;
    private final GetUserUseCase getUserUseCase;
    private final UpdateUserUseCase updateUserUseCase;
    private final DeleteUserUseCase deleteUserUseCase;
    private final UpdatePasswordUseCase updatePasswordUseCase;

    public UserController(CreateUserUseCase createUserUseCase,
                          GetUserUseCase getUserUseCase,
                          UpdateUserUseCase updateUserUseCase,
                          DeleteUserUseCase deleteUserUseCase, UpdatePasswordUseCase updatePasswordUseCase) {

        this.createUserUseCase = createUserUseCase;
        this.getUserUseCase = getUserUseCase;
        this.updateUserUseCase = updateUserUseCase;
        this.deleteUserUseCase = deleteUserUseCase;
        this.updatePasswordUseCase = updatePasswordUseCase;
    }

    // ========== SPECIFIC ROUTES (MUST COME FIRST) ==========

    @PostMapping("/register")
    public ResponseEntity<Map<String, Object>> register(@Valid @RequestBody RegistrationInput request) {
        logger.debug("Register endpoint called with email: {}", request.email());
        Email convertedEmail = Email.of(request.email());

        if (getUserUseCase.existsByEmail(convertedEmail)) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", "Email already exists"));
        }
        User saved = createUserUseCase.register(request);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(Map.of(
                        "id", saved.id().toString(),
                        "username", saved.username(),
                        "email", saved.email()
                ));
    }

    // Specific routes with "by-" prefix to avoid conflict with parameterized routes
    @GetMapping("/by-username/{username}")
    public ResponseEntity<User> getUserByUsername(@PathVariable String username) {
        logger.debug("getUserByUsername called with: {}", username);
        User user = getUserUseCase.getBySurname(Surname.of(username));
        return ResponseEntity.ok(user);
    }

    @GetMapping("/by-email/{email}")
    public ResponseEntity<User> getUserByEmail(@PathVariable String email) {
        logger.debug("getUserByEmail called with: {}", email);
        User user = getUserUseCase.getByEmail(Email.of(email));
        return ResponseEntity.ok(user);
    }

    // ========== PARAMETERIZED ROUTES (MUST COME LAST) ==========

    @GetMapping("/{userId}")
    public ResponseEntity<User> getUserById(@PathVariable String userId) {
        UUID convertedUserId = UUID.fromString(userId);
        User user = getUserUseCase.getById(convertedUserId);
        return ResponseEntity.ok(user);
    }

    @PutMapping("/{userId}/change-password")
    public ResponseEntity<Map<String, String>> changePassword(
            @PathVariable String userId,
            @Valid @RequestBody ChangePasswordRequest request) {
        logger.debug("changePassword called for userId: {}", userId);
        UUID convertedUserId = UUID.fromString(userId);

        updatePasswordUseCase.changePassword(convertedUserId,
                Password.fromPlainText(request.currentPassword()),
                request.newPassword());

        return ResponseEntity.ok(Map.of("message", "Password changed successfully"));
    }

    @PutMapping("/{userId}/change-username")
    public ResponseEntity<User> updateUsername(
            @PathVariable String userId,
            @Valid @RequestBody UpdateUserRequest request) {
        logger.debug("updateUsername called for userId: {}", userId);
        Username username = Username.of(request.newUsername());
        UUID convertedUserId = UUID.fromString(userId);

        User user = updateUserUseCase.updateUsername(convertedUserId, username);
        return ResponseEntity.ok(user);
    }

    @PutMapping("/{userId}/change-surname")
    public ResponseEntity<User> updateSurname(
            @PathVariable String userId,
            @Valid @RequestBody UpdateUserRequest updateUserRequest) {
        logger.debug("updateSurname called for userId: {}", userId);
        Surname surname = Surname.of(updateUserRequest.newSurname());
        UUID convertedUserId = UUID.fromString(userId);
        User user = updateUserUseCase.updateSurname(convertedUserId, surname);
        return ResponseEntity.ok(user);
    }

    @PutMapping("/{userId}/change-phonenumber")
    public ResponseEntity<User> updatePhonenumber(@PathVariable String userId,
                                                  @Valid @RequestBody UpdateUserRequest updateUserRequest) {
        logger.debug("updatePhonenumber called for userId: {}", userId);
        PhoneNumber newPhonenumber = PhoneNumber.of(updateUserRequest.newPhonenumber());
        UUID convertedUserId = UUID.fromString(userId);
        User user = updateUserUseCase.updatePhonenumber(convertedUserId, newPhonenumber);
        return ResponseEntity.ok(user);
    }

    @PutMapping("/{userId}/change-email")
    public ResponseEntity<User> updateEmail(
            @PathVariable String userId,
            @Valid @RequestBody UpdateUserRequest updateUserRequest) {
        logger.debug("updateEmail called for userId: {}", userId);
        Email convertedEmail = Email.of(updateUserRequest.newEmail());
        UUID convertedUserId = UUID.fromString(userId);
        User user = updateUserUseCase.updateEmail(convertedUserId, convertedEmail);
        return ResponseEntity.ok(user);
    }


    @PutMapping("/{userId}/email-notifications")
    public ResponseEntity<User> updateEmailNotificationsEnabled(
            @PathVariable String userId,
            @Valid @RequestBody UpdateUserRequest updateUserRequest) {
        logger.debug("updateEmailNotificationsEnabled called for userId: {}", userId);
        boolean enabledNotifications = updateUserRequest.emailNotificationsEnabled();
        UUID convertedUserId = UUID.fromString(userId);
        User user = updateUserUseCase.updateEmailNotifications(convertedUserId, enabledNotifications);
        return ResponseEntity.ok(user);
    }


    @PutMapping("/{userId}/telegram-notifications")
    public ResponseEntity<User> updateTelegramNotificationsEnabled(
            @PathVariable String userId,
            @Valid @RequestBody UpdateUserRequest updateUserRequest) {
        logger.debug("updateTelegramNotificationsEnabled called for userId: {}", userId);
        boolean enabledNotifications = updateUserRequest.telegramNotificationsEnabled();
        UUID convertedUserId = UUID.fromString(userId);
        User user = updateUserUseCase.updateTelegramNotifications(convertedUserId, enabledNotifications);
        return ResponseEntity.ok(user);
    }


    @PutMapping("/{userId}/telegram-chat-id")
    public ResponseEntity<User> updateTelegramChatId(
            @PathVariable String userId,
            @Valid @RequestBody UpdateUserRequest updateUserRequest) {
        logger.debug("updateTelegramChatId called for userId: {}", userId);
        TelegramChatId telegramChatId = TelegramChatId.of(updateUserRequest.telegramChatId());
        UUID convertedUserId = UUID.fromString(userId);
        User user = updateUserUseCase.updateTelegramChatId(convertedUserId, telegramChatId);
        return ResponseEntity.ok(user);
    }

    @DeleteMapping("/{userId}")
    public ResponseEntity<Void> deleteUserById(@PathVariable String userId) {
        logger.debug("deleteUserById called for userId: {}", userId);
        UUID convertedUserId = UUID.fromString(userId);
        deleteUserUseCase.deleteById(convertedUserId);
        return ResponseEntity.noContent().build();
    }

    // ========== ADMIN ENDPOINTS ==========

    @GetMapping("/admin/all")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<User>> getAllUsers() {
        List<User> users = getUserUseCase.getAllUsers();
        return ResponseEntity.ok(users);
    }

    @DeleteMapping("/admin/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteUserByAdmin(@PathVariable String id) {
        UUID convertedUserId = UUID.fromString(id);
        deleteUserUseCase.deleteById(convertedUserId);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/admin/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<User> updateUserByAdmin(
            @PathVariable String id,
            @Valid @RequestBody UpdateUserRequest request) {
        UUID convertedUserId = UUID.fromString(id);

        // Update fields if provided
        if (request.newUsername() != null) {
            updateUserUseCase.updateUsername(convertedUserId, Username.of(request.newUsername()));
        }
        if (request.newEmail() != null) {
            updateUserUseCase.updateEmail(convertedUserId, Email.of(request.newEmail()));
        }
        if (request.newSurname() != null) {
            updateUserUseCase.updateSurname(convertedUserId, Surname.of(request.newSurname()));
        }
        if (request.newPhonenumber() != null) {
            updateUserUseCase.updatePhonenumber(convertedUserId, PhoneNumber.of(request.newPhonenumber()));
        }
        if (request.telegramChatId() != null) {
            updateUserUseCase.updateTelegramChatId(convertedUserId, TelegramChatId.of(request.telegramChatId()));
        }
        if (request.emailNotificationsEnabled() != null) {
            updateUserUseCase.updateEmailNotifications(convertedUserId, request.emailNotificationsEnabled());
        }
        if (request.telegramNotificationsEnabled() != null) {
            updateUserUseCase.updateTelegramNotifications(convertedUserId, request.telegramNotificationsEnabled());
        }

        if (request.newRole() != null) {
            Role newRole = Role.valueOf(request.newRole().toUpperCase());
            updateUserUseCase.updateRole(convertedUserId, newRole);
        }

        User updatedUser = getUserUseCase.getById(convertedUserId);
        return ResponseEntity.ok(updatedUser);
    }

}
