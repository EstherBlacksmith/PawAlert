package itacademy.pawalert.infrastructure.rest.user.dto;

import itacademy.pawalert.domain.user.model.Password;
import jakarta.validation.constraints.NotBlank;

public record ChangePasswordRequest(
        @NotBlank(message = "Password can't be empty") String currentPassword,
        @NotBlank(message = "Password can't be empty") Password newPassword
) {

}
