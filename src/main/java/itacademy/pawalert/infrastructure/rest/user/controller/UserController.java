package itacademy.pawalert.infrastructure.rest.user.controller;

import itacademy.pawalert.application.port.inbound.CreateUserUseCase;
import itacademy.pawalert.application.port.inbound.DeleteUserUseCase;
import itacademy.pawalert.application.port.inbound.GetUserUseCase;
import itacademy.pawalert.application.port.inbound.UpdateUserUseCase;
import itacademy.pawalert.domain.user.User;
import itacademy.pawalert.infrastructure.rest.user.dto.CreateUserRequest;
import itacademy.pawalert.infrastructure.rest.user.dto.UpdateUserRequest;
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

    public UserController(CreateUserUseCase createUserUseCase,
                          GetUserUseCase getUserUseCase,
                          UpdateUserUseCase updateUserUseCase,
                          DeleteUserUseCase deleteUserUseCase) {

        this.createUserUseCase = createUserUseCase;
        this.getUserUseCase = getUserUseCase;
        this.updateUserUseCase = updateUserUseCase;
        this.deleteUserUseCase = deleteUserUseCase;
    }

    @PostMapping("/register")
    public ResponseEntity<Map<String, Object>> register(@RequestBody CreateUserRequest request) {
        if (getUserUseCase.existsByUsername(request.getUsername())) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", "Username already exists"));
        }

        if (getUserUseCase.existsByEmail(request.getEmail())) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", "Email already exists"));
        }

        User saved = createUserUseCase.register(
                request.username(),
                request.fullName(),
                request.email(),
                request.phoneNumber(),
                request.password()
        );

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(Map.of(
                        "id", saved.getId().toString(),
                        "username", saved.getUsername(),
                        "email", saved.getEmail()
                ));
    }

    @GetMapping("/{id}")
    public ResponseEntity<User> getUserBuId(@PathVariable String id) {
        User user = getUserUseCase.getById(id);
        return ResponseEntity.ok(user);
    }

    @GetMapping("/username/{username}")
    public ResponseEntity<User> getUserByUsername(@PathVariable String username) {
        User user = getUserUseCase.getByUsername(username);
        return ResponseEntity.ok(user);
    }

    @GetMapping("/email/{email}")
    public ResponseEntity<User> getUserByEmail(@PathVariable String email) {
        User user = getUserUseCase.getByEmail(email);
        return ResponseEntity.ok(user);
    }

    @DeleteMapping("/email/{email}")
    public ResponseEntity<Void> deleteUserByEmail(@PathVariable String email) {
        deleteUserUseCase.deleteByEmail(email);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/email/{email}")
    public ResponseEntity<User> updateUsername(@PathVariable String email, @RequestBody UpdateUserRequest updateUserRequest) {
        User user = updateUserUseCase.updateUsername(email,updateUserRequest.newUsername());
        return ResponseEntity.ok(user);
    }


}
