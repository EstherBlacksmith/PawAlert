package itacademy.pawalert.infrastructure.rest.auth;

import itacademy.pawalert.domain.user.User;
import itacademy.pawalert.domain.user.UserWithPassword;
import itacademy.pawalert.infrastructure.persistence.user.UserRepository;
import itacademy.pawalert.infrastructure.security.JWTService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Tag(name = "Users", description = "User management endpoints for authentication, registration, and profile management")
public class AuthController {

    private final UserRepository userRepository;
    private final JWTService jwtService;
    private final PasswordEncoder passwordEncoder;


    @PostMapping("/login")
    @Operation(summary = "Log in", description = "Authenticates a user with their credentials (email and password) and returns a JWT token. No prior authentication required.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Authentication successful, JWT token generated",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = AuthResponse.class))),
            @ApiResponse(responseCode = "400", description = "Invalid credentials or missing data"),
            @ApiResponse(responseCode = "401", description = "Incorrect email or password")
    })
    public ResponseEntity<AuthResponse> login(@RequestBody LoginRequest request) {
        // Look for a user with the email
        UserWithPassword userWithPassword = userRepository.findByEmail((request.email()))
                .orElseThrow(() -> new BadCredentialsException("Invalid credentials"))
                .toDomainWithPassword();

        // Verify the pass
        if (!passwordEncoder.matches(request.password(), userWithPassword.passwordHash())) {
            throw new BadCredentialsException("Invalid credentials");
        }

        User user = userWithPassword.user();

        // Generate token JWT
        String token = jwtService.generateToken(user.username().value());

        // Retornar respuesta con token
        return ResponseEntity.ok(new AuthResponse(
                token,
                "Bearer",
                3600L,
                user.id().toString(),
                user.username().value(),
                user.email().value(),
                user.role().toString(),
                user.phoneNumber().value(),
                user.surname().value()
        ));
    }
}
