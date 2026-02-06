package itacademy.pawalert.domain.pet.model;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;

public record PetDescription(@NotBlank(message = "Description can not be empty")
                             @Min(value = 25, message = "The description must be almost 25 characters")
                             @Max(value = 255, message = "The description must be less than 255 characters") String description) {
    public String value() {
        return description;
    }
    public static PetDescription of(String description) {
        return new PetDescription(description);
    }

}