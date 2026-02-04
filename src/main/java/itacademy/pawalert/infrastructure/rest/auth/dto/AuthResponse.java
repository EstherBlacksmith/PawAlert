package itacademy.pawalert.infrastructure.rest.auth.dto;

public record AuthResponse(
        String token,
        String tokenType,
        Long expiresIn,
        String userId,
        String username,
        String email
) {}