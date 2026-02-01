package itacademy.pawalert.domain.alert.model;


import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;

public record Description(@NotBlank(message = "Description can not be empty")
                          @Min(value = 25, message = "The description must be almost 25 characters")
                          @Max(value = 500, message = "The description must be less than 500 characters") String description) {
    public String getValue() {
        return description;
    }
}
