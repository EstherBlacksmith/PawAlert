package itacademy.pawalert.infrastructure.rest.user.controller;

import itacademy.pawalert.application.port.inbound.*;
import itacademy.pawalert.domain.user.User;
import itacademy.pawalert.domain.user.model.*;
import itacademy.pawalert.infrastructure.rest.user.dto.RegistrationInput;
import itacademy.pawalert.infrastructure.rest.user.dto.ChangePasswordRequest;
import itacademy.pawalert.infrastructure.rest.user.dto.UpdateUserRequest;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/users")
public class UserController {

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

    @PostMapping("/register")
    public ResponseEntity<Map<String, Object>> register(@Valid @RequestBody RegistrationInput request) {
        Email convertedEmail = Email.of(request.email());
        Surname converteedSurname = Surname.of(request.surname());
        if (getUserUseCase.existsBySurname(converteedSurname)) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", "Username already exists"));
        }

        if (getUserUseCase.existsByEmail(convertedEmail)) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", "Email already exists"));
        }
        User saved = createUserUseCase.register(request);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(Map.of(
                        "id", saved.getId().toString(),
                        "username", saved.getUsername(),
                        "email", saved.getEmail()
                ));
    }

    @PutMapping("/password")
    public ResponseEntity<Map<String, String>> changePassword(
            @RequestParam String email,
            @Valid @RequestBody ChangePasswordRequest request) {
        Email convertedEmail = Email.of(email);

        updatePasswordUseCase.changePassword(convertedEmail,
                Password.fromPlainText(request.currentPassword()),
                request.newPassword());

        return ResponseEntity.ok(Map.of("message", "Password changed successfully"));
    }

    @GetMapping("/{userId}")
    public ResponseEntity<User> getUserById(@PathVariable String userId) {
        UUID convertedUserId = UUID.fromString(userId);

        User user = getUserUseCase.getById(convertedUserId);
        return ResponseEntity.ok(user);
    }

    @GetMapping("/username/{username}")
    public ResponseEntity<User> getUserBySurname(@PathVariable String surname) {
        User user = getUserUseCase.getBySurname(Surname.of(surname));
        return ResponseEntity.ok(user);
    }

    @GetMapping("/email/{email}")
    public ResponseEntity<User> getUserByEmail(@PathVariable String email) {
        User user = getUserUseCase.getByEmail(Email.of(email));
        return ResponseEntity.ok(user);
    }

    @DeleteMapping("/{userId}")
    public ResponseEntity<Void> deleteUserByEmail(@PathVariable String email) {
        deleteUserUseCase.deleteByEmail(Email.of(email));
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{userId}/username")
    public ResponseEntity<User> updateUsername(
            @PathVariable String userId,
            @Valid @RequestBody UpdateUserRequest request) {

        Username username = Username.of(request.newUsername());
        UUID convertedUserId = UUID.fromString(userId);

        User user = updateUserUseCase.updateUsername(convertedUserId, username);
        return ResponseEntity.ok(user);
    }

    @PutMapping("/{userId}/surname")
    public ResponseEntity<User> updateSurname(
            @PathVariable String userId,
            @Valid @RequestBody UpdateUserRequest updateUserRequest) {
        Surname surname = Surname.of(updateUserRequest.newSurname());
        UUID convertedUserId = UUID.fromString(userId);
        User user = updateUserUseCase.updateSurname(convertedUserId,surname);
        return ResponseEntity.ok(user);
    }

    @PutMapping("/{userId}/phonenumber")
    public ResponseEntity<User> updatePhonenumber(@PathVariable String userId,
                                                  @Valid @RequestBody UpdateUserRequest updateUserRequest) {
        PhoneNumber newPhonenumber = PhoneNumber.of(updateUserRequest.newPhonenumber());
        UUID convertedUserId = UUID.fromString(userId);
        User user = updateUserUseCase.updatePhonenumber(convertedUserId,newPhonenumber);
        return ResponseEntity.ok(user);
    }

    @PutMapping("/{userId}/email")
    public ResponseEntity<User> updateEmail(
            @PathVariable String userId,
            @Valid @RequestBody UpdateUserRequest updateUserRequest) {
        Email convertedEmail = Email.of(updateUserRequest.newEmail());
        UUID convertedUserId = UUID.fromString(userId);
        User user = updateUserUseCase.updateEmail(convertedUserId, convertedEmail);
        return ResponseEntity.ok(user);
    }

}
