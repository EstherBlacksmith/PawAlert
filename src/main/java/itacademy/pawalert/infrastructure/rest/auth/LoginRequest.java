package itacademy.pawalert.infrastructure.rest.auth;

public record LoginRequest(
        String username,
        String password
) {}