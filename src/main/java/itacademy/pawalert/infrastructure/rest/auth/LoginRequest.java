package itacademy.pawalert.infrastructure.rest.auth;

public record LoginRequest(
        String email,
        String password
) {
}