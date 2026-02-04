package itacademy.pawalert.infrastructure.rest.user.controller;

import itacademy.pawalert.application.service.UserService;
import itacademy.pawalert.domain.user.User;
import itacademy.pawalert.infrastructure.persistence.user.UserRepositoryAdapter;
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

    private final UserService userService;
    private final UserRepositoryAdapter userRepositoryAdapter;

    public UserController(UserService userService, UserRepositoryAdapter userRepositoryAdapter) {
        this.userService = userService;
        this.userRepositoryAdapter = userRepositoryAdapter;
    }

    @PostMapping("/register")
    public ResponseEntity<Map<String, Object>> register(@RequestBody CreateUserRequest request) {
        if (userService.existsByUsername(request.getUsername())) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", "Username already exists"));
        }

        if (userService.existsByEmail(request.getEmail())) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", "Email already exists"));
        }

        User user = new User(
                UUID.randomUUID(),
                request.username(),
                request.email(),
                request.fullName(),
                request.phoneNumber()
        );

        User saved = userRepositoryAdapter.saveWithPlainPassword(user, request.password());

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(Map.of(
                        "id", saved.getId().toString(),
                        "username", saved.getUsername(),
                        "email", saved.getEmail()
                ));
    }

    @GetMapping("/{id}")
    public ResponseEntity<User> getUser(@PathVariable String id) {
        User user = userService.findById(id);
        return ResponseEntity.ok(user);
    }

    @GetMapping("/username/{username}")
    public ResponseEntity<User> getUserByUsername(@PathVariable String username) {
        User user = userService.findByUsername(username);
        return ResponseEntity.ok(user);
    }

    @GetMapping("/email/{email}")
    public ResponseEntity<User> getUserByEmail(@PathVariable String email) {
        User user = userService.findByEmail(email);
        return ResponseEntity.ok(user);
    }

    @DeleteMapping("/email/{email}")
    public ResponseEntity<User> deleteUserByEmail(@PathVariable String email) {
        User user = userService.deleteByEmail(email);
        return ResponseEntity.ok(user);
    }

  /*  @PatchMapping("/email/{email}")
    public ResponseEntity<User> updateUsername(@PathVariable String email, @RequestBody UpdateUserRequest updateUserRequest) {
        User user = userService.updateUsername(email);
        return ResponseEntity.ok(user);
    }

*/
}
