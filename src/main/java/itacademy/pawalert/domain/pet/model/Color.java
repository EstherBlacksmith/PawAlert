package itacademy.pawalert.domain.pet.model;

import jakarta.validation.constraints.NotBlank;

public record Color(@NotBlank(message = "Color can not be empty") String color) {

    public String value() { return this.color; }
    public static Color of(String color) {
        return new Color(color);
    }

}
