package itacademy.pawalert.domain.pet.model;

import jakarta.validation.constraints.NotBlank;

public record Color(@NotBlank(message = "Color can not be empty") String color) {
}
