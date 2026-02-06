package itacademy.pawalert.infrastructure.rest.user.dto;

public record CreateUserRequest(
        String username,
        String email,
        String password,
        String surname,
        String phoneNumber
) {
}
