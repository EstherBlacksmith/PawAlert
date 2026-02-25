package itacademy.pawalert.infrastructure.rest.user.controller;

import itacademy.pawalert.application.user.port.inbound.*;
import itacademy.pawalert.domain.user.Role;
import itacademy.pawalert.domain.user.User;
import itacademy.pawalert.domain.user.model.*;
import itacademy.pawalert.infrastructure.rest.user.dto.ChangePasswordRequest;
import itacademy.pawalert.infrastructure.rest.user.dto.RegistrationInput;
import itacademy.pawalert.infrastructure.rest.user.dto.UpdateUserRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@Tag(name = "Users", description = "User management endpoints for authentication, registration, and profile management")
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
    @Operation(summary = "Register new user", description = "Creates a new user account in the system. No authentication required.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "User registered successfully",
                    content = @Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "400", description = "Invalid registration data or email already exists")
    })
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
    @Operation(summary = "Get user by username", description = "Retrieves a specific user by their username.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User retrieved successfully",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = User.class))),
            @ApiResponse(responseCode = "404", description = "User not found")
    })
    public ResponseEntity<User> getUserByUsername(
            @Parameter(description = "Username", required = true)
            @PathVariable String username) {
        logger.debug("getUserByUsername called with: {}", username);
        User user = getUserUseCase.getBySurname(Surname.of(username));
        return ResponseEntity.ok(user);
    }

    @GetMapping("/by-email/{email}")
    @Operation(summary = "Get user by email", description = "Retrieves a specific user by their email address.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User retrieved successfully",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = User.class))),
            @ApiResponse(responseCode = "404", description = "User not found")
    })
    public ResponseEntity<User> getUserByEmail(
            @Parameter(description = "Email address", required = true)
            @PathVariable String email) {
        logger.debug("getUserByEmail called with: {}", email);
        User user = getUserUseCase.getByEmail(Email.of(email));
        return ResponseEntity.ok(user);
    }

    // ========== PARAMETERIZED ROUTES (MUST COME LAST) ==========

    @GetMapping("/{userId}")
    @Operation(summary = "Get user by ID", description = "Retrieves a specific user by their unique identifier.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User retrieved successfully",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = User.class))),
            @ApiResponse(responseCode = "404", description = "User not found")
    })
    public ResponseEntity<User> getUserById(
            @Parameter(description = "User ID (UUID format)", required = true, example = "550e8400-e29b-41d4-a716-446655440000")
            @PathVariable String userId) {
        UUID convertedUserId = UUID.fromString(userId);
        User user = getUserUseCase.getById(convertedUserId);
        return ResponseEntity.ok(user);
    }

    @PutMapping("/{userId}/change-password")
    @Operation(summary = "Change password", description = "Changes the authenticated user's password. Requires authentication.")
    @SecurityRequirement(name = "Bearer JWT")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Password changed successfully",
                    content = @Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "400", description = "Incorrect current password or invalid data"),
            @ApiResponse(responseCode = "401", description = "Unauthorized - JWT token missing or invalid"),
            @ApiResponse(responseCode = "404", description = "User not found")
    })
    public ResponseEntity<Map<String, String>> changePassword(
            @Parameter(description = "User ID (UUID format)", required = true, example = "550e8400-e29b-41d4-a716-446655440000")
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
    @Operation(summary = "Change username", description = "Updates the username. Requires authentication.")
    @SecurityRequirement(name = "Bearer JWT")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Username updated successfully",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = User.class))),
            @ApiResponse(responseCode = "400", description = "Invalid data"),
            @ApiResponse(responseCode = "401", description = "Unauthorized - JWT token missing or invalid"),
            @ApiResponse(responseCode = "404", description = "User not found")
    })
    public ResponseEntity<User> updateUsername(
            @Parameter(description = "User ID (UUID format)", required = true, example = "550e8400-e29b-41d4-a716-446655440000")
            @PathVariable String userId,
            @Valid @RequestBody UpdateUserRequest request) {
        logger.debug("updateUsername called for userId: {}", userId);
        Username username = Username.of(request.newUsername());
        UUID convertedUserId = UUID.fromString(userId);

        User user = updateUserUseCase.updateUsername(convertedUserId, username);
        return ResponseEntity.ok(user);
    }

    @PutMapping("/{userId}/change-surname")
    @Operation(summary = "Change surname", description = "Updates the user's surname. Requires authentication.")
    @SecurityRequirement(name = "Bearer JWT")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Surname updated successfully",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = User.class))),
            @ApiResponse(responseCode = "400", description = "Invalid data"),
            @ApiResponse(responseCode = "401", description = "Unauthorized - JWT token missing or invalid"),
            @ApiResponse(responseCode = "404", description = "User not found")
    })
    public ResponseEntity<User> updateSurname(
            @Parameter(description = "User ID (UUID format)", required = true, example = "550e8400-e29b-41d4-a716-446655440000")
            @PathVariable String userId,
            @Valid @RequestBody UpdateUserRequest updateUserRequest) {
        logger.debug("updateSurname called for userId: {}", userId);
        Surname surname = Surname.of(updateUserRequest.newSurname());
        UUID convertedUserId = UUID.fromString(userId);
        User user = updateUserUseCase.updateSurname(convertedUserId, surname);
        return ResponseEntity.ok(user);
    }

    @PutMapping("/{userId}/change-phonenumber")
    @Operation(summary = "Change phone number", description = "Updates the user's phone number. Requires authentication.")
    @SecurityRequirement(name = "Bearer JWT")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Phone number updated successfully",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = User.class))),
            @ApiResponse(responseCode = "400", description = "Invalid data"),
            @ApiResponse(responseCode = "401", description = "Unauthorized - JWT token missing or invalid"),
            @ApiResponse(responseCode = "404", description = "User not found")
    })
    public ResponseEntity<User> updatePhonenumber(
            @Parameter(description = "User ID (UUID format)", required = true, example = "550e8400-e29b-41d4-a716-446655440000")
            @PathVariable String userId,
            @Valid @RequestBody UpdateUserRequest updateUserRequest) {
        logger.debug("updatePhonenumber called for userId: {}", userId);
        PhoneNumber newPhonenumber = PhoneNumber.of(updateUserRequest.newPhonenumber());
        UUID convertedUserId = UUID.fromString(userId);
        User user = updateUserUseCase.updatePhonenumber(convertedUserId, newPhonenumber);
        return ResponseEntity.ok(user);
    }

    @PutMapping("/{userId}/change-email")
    @Operation(summary = "Change email", description = "Updates the user's email address. Requires authentication.")
    @SecurityRequirement(name = "Bearer JWT")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Email updated successfully",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = User.class))),
            @ApiResponse(responseCode = "400", description = "Invalid data or email already exists"),
            @ApiResponse(responseCode = "401", description = "Unauthorized - JWT token missing or invalid"),
            @ApiResponse(responseCode = "404", description = "User not found")
    })
    public ResponseEntity<User> updateEmail(
            @Parameter(description = "User ID (UUID format)", required = true, example = "550e8400-e29b-41d4-a716-446655440000")
            @PathVariable String userId,
            @Valid @RequestBody UpdateUserRequest updateUserRequest) {
        logger.debug("updateEmail called for userId: {}", userId);
        Email convertedEmail = Email.of(updateUserRequest.newEmail());
        UUID convertedUserId = UUID.fromString(userId);
        User user = updateUserUseCase.updateEmail(convertedUserId, convertedEmail);
        return ResponseEntity.ok(user);
    }


    @PutMapping("/{userId}/email-notifications")
    @Operation(summary = "Update email notification preference", description = "Enables or disables email notifications. Requires authentication.")
    @SecurityRequirement(name = "Bearer JWT")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Notification preference updated successfully",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = User.class))),
            @ApiResponse(responseCode = "401", description = "Unauthorized - JWT token missing or invalid"),
            @ApiResponse(responseCode = "404", description = "User not found")
    })
    public ResponseEntity<User> updateEmailNotificationsEnabled(
            @Parameter(description = "User ID (UUID format)", required = true, example = "550e8400-e29b-41d4-a716-446655440000")
            @PathVariable String userId,
            @Valid @RequestBody UpdateUserRequest updateUserRequest) {
        logger.debug("updateEmailNotificationsEnabled called for userId: {}", userId);
        boolean enabledNotifications = updateUserRequest.emailNotificationsEnabled();
        UUID convertedUserId = UUID.fromString(userId);
        User user = updateUserUseCase.updateEmailNotifications(convertedUserId, enabledNotifications);
        return ResponseEntity.ok(user);
    }


    @PutMapping("/{userId}/telegram-notifications")
    @Operation(summary = "Update Telegram notification preference", description = "Enables or disables Telegram notifications. Requires authentication.")
    @SecurityRequirement(name = "Bearer JWT")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Notification preference updated successfully",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = User.class))),
            @ApiResponse(responseCode = "401", description = "Unauthorized - JWT token missing or invalid"),
            @ApiResponse(responseCode = "404", description = "User not found")
    })
    public ResponseEntity<User> updateTelegramNotificationsEnabled(
            @Parameter(description = "User ID (UUID format)", required = true, example = "550e8400-e29b-41d4-a716-446655440000")
            @PathVariable String userId,
            @Valid @RequestBody UpdateUserRequest updateUserRequest) {
        logger.debug("updateTelegramNotificationsEnabled called for userId: {}", userId);
        boolean enabledNotifications = updateUserRequest.telegramNotificationsEnabled();
        UUID convertedUserId = UUID.fromString(userId);
        User user = updateUserUseCase.updateTelegramNotifications(convertedUserId, enabledNotifications);
        return ResponseEntity.ok(user);
    }


    @PutMapping("/{userId}/telegram-chat-id")
    @Operation(summary = "Update Telegram chat ID", description = "Updates the Telegram chat ID for receiving notifications. Requires authentication.")
    @SecurityRequirement(name = "Bearer JWT")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Telegram chat ID updated successfully",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = User.class))),
            @ApiResponse(responseCode = "400", description = "Invalid data"),
            @ApiResponse(responseCode = "401", description = "Unauthorized - JWT token missing or invalid"),
            @ApiResponse(responseCode = "404", description = "User not found")
    })
    public ResponseEntity<User> updateTelegramChatId(
            @Parameter(description = "User ID (UUID format)", required = true, example = "550e8400-e29b-41d4-a716-446655440000")
            @PathVariable String userId,
            @Valid @RequestBody UpdateUserRequest updateUserRequest) {
        logger.debug("updateTelegramChatId called for userId: {}", userId);
        TelegramChatId telegramChatId = TelegramChatId.of(updateUserRequest.telegramChatId());
        UUID convertedUserId = UUID.fromString(userId);
        User user = updateUserUseCase.updateTelegramChatId(convertedUserId, telegramChatId);
        return ResponseEntity.ok(user);
    }

    @DeleteMapping("/{userId}")
    @Operation(summary = "Delete user", description = "Deletes a user by their unique identifier. Requires authentication.")
    @SecurityRequirement(name = "Bearer JWT")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "User deleted successfully"),
            @ApiResponse(responseCode = "401", description = "Unauthorized - JWT token missing or invalid"),
            @ApiResponse(responseCode = "404", description = "User not found")
    })
    public ResponseEntity<Void> deleteUserById(
            @Parameter(description = "User ID (UUID format)", required = true, example = "550e8400-e29b-41d4-a716-446655440000")
            @PathVariable String userId) {
        logger.debug("deleteUserById called for userId: {}", userId);
        UUID convertedUserId = UUID.fromString(userId);
        deleteUserUseCase.deleteById(convertedUserId);
        return ResponseEntity.noContent().build();
    }

    // ========== ADMIN ENDPOINTS ==========

    @GetMapping("/admin/all")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Get all users (Admin Only)", description = "Retrieves all users in the system. This endpoint requires ADMIN role.")
    @SecurityRequirement(name = "Bearer JWT")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "List of all users retrieved successfully",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = User.class))),
            @ApiResponse(responseCode = "401", description = "Unauthorized - JWT token missing or invalid"),
            @ApiResponse(responseCode = "403", description = "Forbidden - User does not have ADMIN role")
    })
    public ResponseEntity<List<User>> getAllUsers() {
        List<User> users = getUserUseCase.getAllUsers();
        return ResponseEntity.ok(users);
    }

    @DeleteMapping("/admin/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Delete user (Admin Only)", description = "Deletes a user by their identifier. This endpoint requires ADMIN role.")
    @SecurityRequirement(name = "Bearer JWT")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "User deleted successfully"),
            @ApiResponse(responseCode = "401", description = "Unauthorized - JWT token missing or invalid"),
            @ApiResponse(responseCode = "403", description = "Forbidden - User does not have ADMIN role"),
            @ApiResponse(responseCode = "404", description = "User not found")
    })
    public ResponseEntity<Void> deleteUserByAdmin(
            @Parameter(description = "User ID (UUID format)", required = true, example = "550e8400-e29b-41d4-a716-446655440000")
            @PathVariable String id) {
        UUID convertedUserId = UUID.fromString(id);
        deleteUserUseCase.deleteById(convertedUserId);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/admin/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Update user (Admin Only)", description = "Updates user data. This endpoint requires ADMIN role and allows updating multiple fields.")
    @SecurityRequirement(name = "Bearer JWT")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User updated successfully",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = User.class))),
            @ApiResponse(responseCode = "400", description = "Invalid data"),
            @ApiResponse(responseCode = "401", description = "Unauthorized - JWT token missing or invalid"),
            @ApiResponse(responseCode = "403", description = "Forbidden - User does not have ADMIN role"),
            @ApiResponse(responseCode = "404", description = "User not found")
    })
    public ResponseEntity<User> updateUserByAdmin(
            @Parameter(description = "User ID (UUID format)", required = true, example = "550e8400-e29b-41d4-a716-446655440000")
            @PathVariable String id,
            @Valid @RequestBody UpdateUserRequest request) {
        logger.debug("updateUserByAdmin called for userId: {}", id);
        logger.debug("Request payload: newUsername={}, newRole={}, newEmail={}, newSurname={}", 
            request.newUsername(), request.newRole(), request.newEmail(), request.newSurname());
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
