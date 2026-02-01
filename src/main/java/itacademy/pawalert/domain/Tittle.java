package itacademy.pawalert.domain;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;

public record Tittle(@NotBlank(message = "The Tittle can not be empty")
                     @Min(value = 5, message = "The tittle must be almost 5 characters")
                     @Max(value = 255, message = "The tittle must be less than 50 characters") String tittle) {
    public String getValue() {
        return tittle;
    }
}