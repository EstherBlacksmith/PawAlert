package itacademy.pawalert.infrastructure.rest.auth;

import itacademy.pawalert.domain.user.User;
import itacademy.pawalert.domain.user.UserWithPassword;
import itacademy.pawalert.domain.user.model.Username;
import itacademy.pawalert.infrastructure.persistence.user.UserRepository;
import itacademy.pawalert.infrastructure.security.JWTService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final UserRepository userRepository;
    private final JWTService jwtService;
    private final PasswordEncoder passwordEncoder;


    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@RequestBody LoginRequest request) {
        // Look for a user with the username
        Username convertedUserName = Username.of(request.username());

        UserWithPassword userWithPassword = userRepository.findByUsername(convertedUserName)
                .orElseThrow(() -> new BadCredentialsException("Invalid credentials"))
                .toDomainWithPassword();

        // Verify the pass
        if (!passwordEncoder.matches(request.password(), userWithPassword.getPasswordHash())) {
            throw new BadCredentialsException("Invalid credentials");
        }

        User user = userWithPassword.getUser();

        // Generate token JWT
        String token = jwtService.generateToken(user.getUsername().value());

        // Retornar respuesta con token
        return ResponseEntity.ok(new AuthResponse(
                token,
                "Bearer",
                3600L,
                user.getId().toString(),
                user.getUsername().value(),
                user.getEmail().value()
        ));
    }
}
