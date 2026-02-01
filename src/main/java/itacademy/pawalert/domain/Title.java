package itacademy.pawalert.domain;

import com.fasterxml.jackson.annotation.JsonCreator;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;

import java.util.Objects;

public record Title(@NotBlank(message = "The Title can not be empty")
                     @Min(value = 5, message = "The title must be almost 5 characters")
                     @Max(value = 255, message = "The title must be less than 50 characters") String title) {


    @JsonCreator  // Jackson will use this for deserialization
    public Title {
    }


    public String getValue() {
        return title;
    }
}