package itacademy.pawalert.infrastructure.rest.auth;

import itacademy.pawalert.domain.user.User;
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
        User user = userRepository.findByUsername(request.username())
                .orElseThrow(() -> new BadCredentialsException("Invalid credentials")).toDomain();

        // Verify the pass
        if (!passwordEncoder.matches(request.password(), user.getPassword())) {
            throw new BadCredentialsException("Invalid credentials");
        }

        // Generar token JWT
        String token = jwtService.generateToken(user.getUsername());

        // Retornar respuesta con token
        return ResponseEntity.ok(new AuthResponse(
                token,
                "Bearer",
                3600L,
                user.getId().toString(),
                user.getUsername(),
                user.getEmail()
        ));
    }
}
