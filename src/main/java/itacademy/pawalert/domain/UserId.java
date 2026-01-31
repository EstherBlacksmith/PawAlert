package itacademy.pawalert.domain;

import jakarta.validation.constraints.NotBlank;

public record UserId(@NotBlank(message = "UserId cannot be empty")
                     String value ) {

}