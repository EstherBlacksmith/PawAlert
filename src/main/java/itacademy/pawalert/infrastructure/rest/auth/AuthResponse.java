package itacademy.pawalert.infrastructure.rest.auth;

public record AuthResponse(
        String token,
        String tokenType,
        Long expiresIn,
        String userId,
        String username,
        String email,
        String role,
        String phonenumber,
        String surname
) {}