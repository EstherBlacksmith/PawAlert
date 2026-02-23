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
    @Operation(summary = "Registrar nuevo usuario", description = "Crea una nueva cuenta de usuario en el sistema. No requiere autenticación.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Usuario registrado exitosamente",
                    content = @Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "400", description = "Datos de registro inválidos o email ya existe")
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
    @Operation(summary = "Obtener usuario por nombre de usuario", description = "Recupera un usuario específico por su nombre de usuario.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Usuario recuperado exitosamente",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = User.class))),
            @ApiResponse(responseCode = "404", description = "Usuario no encontrado")
    })
    public ResponseEntity<User> getUserByUsername(
            @Parameter(description = "Nombre de usuario", required = true)
            @PathVariable String username) {
        logger.debug("getUserByUsername called with: {}", username);
        User user = getUserUseCase.getBySurname(Surname.of(username));
        return ResponseEntity.ok(user);
    }

    @GetMapping("/by-email/{email}")
    @Operation(summary = "Obtener usuario por email", description = "Recupera un usuario específico por su dirección de correo electrónico.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Usuario recuperado exitosamente",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = User.class))),
            @ApiResponse(responseCode = "404", description = "Usuario no encontrado")
    })
    public ResponseEntity<User> getUserByEmail(
            @Parameter(description = "Dirección de correo electrónico", required = true)
            @PathVariable String email) {
        logger.debug("getUserByEmail called with: {}", email);
        User user = getUserUseCase.getByEmail(Email.of(email));
        return ResponseEntity.ok(user);
    }

    // ========== PARAMETERIZED ROUTES (MUST COME LAST) ==========

    @GetMapping("/{userId}")
    @Operation(summary = "Obtener usuario por ID", description = "Recupera un usuario específico por su identificador único.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Usuario recuperado exitosamente",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = User.class))),
            @ApiResponse(responseCode = "404", description = "Usuario no encontrado")
    })
    public ResponseEntity<User> getUserById(
            @Parameter(description = "ID del usuario (formato UUID)", required = true, example = "550e8400-e29b-41d4-a716-446655440000")
            @PathVariable String userId) {
        UUID convertedUserId = UUID.fromString(userId);
        User user = getUserUseCase.getById(convertedUserId);
        return ResponseEntity.ok(user);
    }

    @PutMapping("/{userId}/change-password")
    @Operation(summary = "Cambiar contraseña", description = "Cambia la contraseña del usuario autenticado. Requiere autenticación.")
    @SecurityRequirement(name = "Bearer JWT")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Contraseña cambiada exitosamente",
                    content = @Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "400", description = "Contraseña actual incorrecta o datos inválidos"),
            @ApiResponse(responseCode = "401", description = "No autorizado - Token JWT faltante o inválido"),
            @ApiResponse(responseCode = "404", description = "Usuario no encontrado")
    })
    public ResponseEntity<Map<String, String>> changePassword(
            @Parameter(description = "ID del usuario (formato UUID)", required = true, example = "550e8400-e29b-41d4-a716-446655440000")
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
    @Operation(summary = "Cambiar nombre de usuario", description = "Actualiza el nombre de usuario. Requiere autenticación.")
    @SecurityRequirement(name = "Bearer JWT")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Nombre de usuario actualizado exitosamente",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = User.class))),
            @ApiResponse(responseCode = "400", description = "Datos inválidos"),
            @ApiResponse(responseCode = "401", description = "No autorizado - Token JWT faltante o inválido"),
            @ApiResponse(responseCode = "404", description = "Usuario no encontrado")
    })
    public ResponseEntity<User> updateUsername(
            @Parameter(description = "ID del usuario (formato UUID)", required = true, example = "550e8400-e29b-41d4-a716-446655440000")
            @PathVariable String userId,
            @Valid @RequestBody UpdateUserRequest request) {
        logger.debug("updateUsername called for userId: {}", userId);
        Username username = Username.of(request.newUsername());
        UUID convertedUserId = UUID.fromString(userId);

        User user = updateUserUseCase.updateUsername(convertedUserId, username);
        return ResponseEntity.ok(user);
    }

    @PutMapping("/{userId}/change-surname")
    @Operation(summary = "Cambiar apellido", description = "Actualiza el apellido del usuario. Requiere autenticación.")
    @SecurityRequirement(name = "Bearer JWT")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Apellido actualizado exitosamente",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = User.class))),
            @ApiResponse(responseCode = "400", description = "Datos inválidos"),
            @ApiResponse(responseCode = "401", description = "No autorizado - Token JWT faltante o inválido"),
            @ApiResponse(responseCode = "404", description = "Usuario no encontrado")
    })
    public ResponseEntity<User> updateSurname(
            @Parameter(description = "ID del usuario (formato UUID)", required = true, example = "550e8400-e29b-41d4-a716-446655440000")
            @PathVariable String userId,
            @Valid @RequestBody UpdateUserRequest updateUserRequest) {
        logger.debug("updateSurname called for userId: {}", userId);
        Surname surname = Surname.of(updateUserRequest.newSurname());
        UUID convertedUserId = UUID.fromString(userId);
        User user = updateUserUseCase.updateSurname(convertedUserId, surname);
        return ResponseEntity.ok(user);
    }

    @PutMapping("/{userId}/change-phonenumber")
    @Operation(summary = "Cambiar número de teléfono", description = "Actualiza el número de teléfono del usuario. Requiere autenticación.")
    @SecurityRequirement(name = "Bearer JWT")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Número de teléfono actualizado exitosamente",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = User.class))),
            @ApiResponse(responseCode = "400", description = "Datos inválidos"),
            @ApiResponse(responseCode = "401", description = "No autorizado - Token JWT faltante o inválido"),
            @ApiResponse(responseCode = "404", description = "Usuario no encontrado")
    })
    public ResponseEntity<User> updatePhonenumber(
            @Parameter(description = "ID del usuario (formato UUID)", required = true, example = "550e8400-e29b-41d4-a716-446655440000")
            @PathVariable String userId,
            @Valid @RequestBody UpdateUserRequest updateUserRequest) {
        logger.debug("updatePhonenumber called for userId: {}", userId);
        PhoneNumber newPhonenumber = PhoneNumber.of(updateUserRequest.newPhonenumber());
        UUID convertedUserId = UUID.fromString(userId);
        User user = updateUserUseCase.updatePhonenumber(convertedUserId, newPhonenumber);
        return ResponseEntity.ok(user);
    }

    @PutMapping("/{userId}/change-email")
    @Operation(summary = "Cambiar correo electrónico", description = "Actualiza el correo electrónico del usuario. Requiere autenticación.")
    @SecurityRequirement(name = "Bearer JWT")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Correo electrónico actualizado exitosamente",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = User.class))),
            @ApiResponse(responseCode = "400", description = "Datos inválidos o email ya existe"),
            @ApiResponse(responseCode = "401", description = "No autorizado - Token JWT faltante o inválido"),
            @ApiResponse(responseCode = "404", description = "Usuario no encontrado")
    })
    public ResponseEntity<User> updateEmail(
            @Parameter(description = "ID del usuario (formato UUID)", required = true, example = "550e8400-e29b-41d4-a716-446655440000")
            @PathVariable String userId,
            @Valid @RequestBody UpdateUserRequest updateUserRequest) {
        logger.debug("updateEmail called for userId: {}", userId);
        Email convertedEmail = Email.of(updateUserRequest.newEmail());
        UUID convertedUserId = UUID.fromString(userId);
        User user = updateUserUseCase.updateEmail(convertedUserId, convertedEmail);
        return ResponseEntity.ok(user);
    }


    @PutMapping("/{userId}/email-notifications")
    @Operation(summary = "Actualizar preferencia de notificaciones por email", description = "Habilita o deshabilita las notificaciones por correo electrónico. Requiere autenticación.")
    @SecurityRequirement(name = "Bearer JWT")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Preferencia de notificaciones actualizada exitosamente",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = User.class))),
            @ApiResponse(responseCode = "401", description = "No autorizado - Token JWT faltante o inválido"),
            @ApiResponse(responseCode = "404", description = "Usuario no encontrado")
    })
    public ResponseEntity<User> updateEmailNotificationsEnabled(
            @Parameter(description = "ID del usuario (formato UUID)", required = true, example = "550e8400-e29b-41d4-a716-446655440000")
            @PathVariable String userId,
            @Valid @RequestBody UpdateUserRequest updateUserRequest) {
        logger.debug("updateEmailNotificationsEnabled called for userId: {}", userId);
        boolean enabledNotifications = updateUserRequest.emailNotificationsEnabled();
        UUID convertedUserId = UUID.fromString(userId);
        User user = updateUserUseCase.updateEmailNotifications(convertedUserId, enabledNotifications);
        return ResponseEntity.ok(user);
    }


    @PutMapping("/{userId}/telegram-notifications")
    @Operation(summary = "Actualizar preferencia de notificaciones por Telegram", description = "Habilita o deshabilita las notificaciones por Telegram. Requiere autenticación.")
    @SecurityRequirement(name = "Bearer JWT")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Preferencia de notificaciones actualizada exitosamente",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = User.class))),
            @ApiResponse(responseCode = "401", description = "No autorizado - Token JWT faltante o inválido"),
            @ApiResponse(responseCode = "404", description = "Usuario no encontrado")
    })
    public ResponseEntity<User> updateTelegramNotificationsEnabled(
            @Parameter(description = "ID del usuario (formato UUID)", required = true, example = "550e8400-e29b-41d4-a716-446655440000")
            @PathVariable String userId,
            @Valid @RequestBody UpdateUserRequest updateUserRequest) {
        logger.debug("updateTelegramNotificationsEnabled called for userId: {}", userId);
        boolean enabledNotifications = updateUserRequest.telegramNotificationsEnabled();
        UUID convertedUserId = UUID.fromString(userId);
        User user = updateUserUseCase.updateTelegramNotifications(convertedUserId, enabledNotifications);
        return ResponseEntity.ok(user);
    }


    @PutMapping("/{userId}/telegram-chat-id")
    @Operation(summary = "Actualizar ID de chat de Telegram", description = "Actualiza el ID de chat de Telegram para recibir notificaciones. Requiere autenticación.")
    @SecurityRequirement(name = "Bearer JWT")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "ID de chat de Telegram actualizado exitosamente",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = User.class))),
            @ApiResponse(responseCode = "400", description = "Datos inválidos"),
            @ApiResponse(responseCode = "401", description = "No autorizado - Token JWT faltante o inválido"),
            @ApiResponse(responseCode = "404", description = "Usuario no encontrado")
    })
    public ResponseEntity<User> updateTelegramChatId(
            @Parameter(description = "ID del usuario (formato UUID)", required = true, example = "550e8400-e29b-41d4-a716-446655440000")
            @PathVariable String userId,
            @Valid @RequestBody UpdateUserRequest updateUserRequest) {
        logger.debug("updateTelegramChatId called for userId: {}", userId);
        TelegramChatId telegramChatId = TelegramChatId.of(updateUserRequest.telegramChatId());
        UUID convertedUserId = UUID.fromString(userId);
        User user = updateUserUseCase.updateTelegramChatId(convertedUserId, telegramChatId);
        return ResponseEntity.ok(user);
    }

    @DeleteMapping("/{userId}")
    @Operation(summary = "Eliminar usuario", description = "Elimina un usuario por su identificador único. Requiere autenticación.")
    @SecurityRequirement(name = "Bearer JWT")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Usuario eliminado exitosamente"),
            @ApiResponse(responseCode = "401", description = "No autorizado - Token JWT faltante o inválido"),
            @ApiResponse(responseCode = "404", description = "Usuario no encontrado")
    })
    public ResponseEntity<Void> deleteUserById(
            @Parameter(description = "ID del usuario (formato UUID)", required = true, example = "550e8400-e29b-41d4-a716-446655440000")
            @PathVariable String userId) {
        logger.debug("deleteUserById called for userId: {}", userId);
        UUID convertedUserId = UUID.fromString(userId);
        deleteUserUseCase.deleteById(convertedUserId);
        return ResponseEntity.noContent().build();
    }

    // ========== ADMIN ENDPOINTS ==========

    @GetMapping("/admin/all")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Obtener todos los usuarios (Solo Admin)", description = "Recupera todos los usuarios del sistema. Este endpoint requiere rol ADMIN.")
    @SecurityRequirement(name = "Bearer JWT")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista de todos los usuarios recuperada exitosamente",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = User.class))),
            @ApiResponse(responseCode = "401", description = "No autorizado - Token JWT faltante o inválido"),
            @ApiResponse(responseCode = "403", description = "Prohibido - El usuario no tiene rol ADMIN")
    })
    public ResponseEntity<List<User>> getAllUsers() {
        List<User> users = getUserUseCase.getAllUsers();
        return ResponseEntity.ok(users);
    }

    @DeleteMapping("/admin/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Eliminar usuario (Solo Admin)", description = "Elimina un usuario por su identificador. Este endpoint requiere rol ADMIN.")
    @SecurityRequirement(name = "Bearer JWT")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Usuario eliminado exitosamente"),
            @ApiResponse(responseCode = "401", description = "No autorizado - Token JWT faltante o inválido"),
            @ApiResponse(responseCode = "403", description = "Prohibido - El usuario no tiene rol ADMIN"),
            @ApiResponse(responseCode = "404", description = "Usuario no encontrado")
    })
    public ResponseEntity<Void> deleteUserByAdmin(
            @Parameter(description = "ID del usuario (formato UUID)", required = true, example = "550e8400-e29b-41d4-a716-446655440000")
            @PathVariable String id) {
        UUID convertedUserId = UUID.fromString(id);
        deleteUserUseCase.deleteById(convertedUserId);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/admin/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Actualizar usuario (Solo Admin)", description = "Actualiza los datos de un usuario. Este endpoint requiere rol ADMIN y permite actualizar múltiples campos.")
    @SecurityRequirement(name = "Bearer JWT")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Usuario actualizado exitosamente",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = User.class))),
            @ApiResponse(responseCode = "400", description = "Datos inválidos"),
            @ApiResponse(responseCode = "401", description = "No autorizado - Token JWT faltante o inválido"),
            @ApiResponse(responseCode = "403", description = "Prohibido - El usuario no tiene rol ADMIN"),
            @ApiResponse(responseCode = "404", description = "Usuario no encontrado")
    })
    public ResponseEntity<User> updateUserByAdmin(
            @Parameter(description = "ID del usuario (formato UUID)", required = true, example = "550e8400-e29b-41d4-a716-446655440000")
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
