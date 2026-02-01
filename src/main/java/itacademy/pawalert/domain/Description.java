package itacademy.pawalert.domain;


import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;

public record Description(@NotBlank(message = "Description can not be empty")
                          @Min(value = 5, message = "The description must be almost 5 characters")
                          @Max(value = 255, message = "The description must be less than 255 characters") String description) {
    public String getValue() {
        return description;
    }
}
