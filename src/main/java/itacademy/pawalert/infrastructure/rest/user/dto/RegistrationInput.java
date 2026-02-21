package itacademy.pawalert.infrastructure.rest.user.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record RegistrationInput(
        @NotBlank(message = "Username can't be empty")
        @Size(min = 3, max = 50, message = "Username must be 3-50 characters")
        String username,

        @NotBlank(message = "Email can't be empty")
        @Email(message = "Invalid email format")
        String email,

        @NotBlank(message = "Password can't be empty")
        @Size(min = 8, message = "Password must be at least 8 characters")
        String password,

        @NotBlank(message = "Surname can't be empty")
        @Size(max = 200, message = "Surname cannot exceed 200 characters")
        String surname,

        @NotBlank(message = "Phone number can't be empty")
        @Pattern(regexp = "^\\+?[0-9]{9,15}$", message = "Invalid phone number format")
        String phoneNumber
) {
}
