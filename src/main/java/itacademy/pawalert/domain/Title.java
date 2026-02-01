package itacademy.pawalert.domain;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;

public record Title(@NotBlank(message = "The Title can not be empty")
                     @Min(value = 5, message = "The title must be almost 5 characters")
                     @Max(value = 255, message = "The title must be less than 50 characters") String title) {
    public String getValue() {
        return title;
    }
}