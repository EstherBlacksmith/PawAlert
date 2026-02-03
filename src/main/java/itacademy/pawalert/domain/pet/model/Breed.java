package itacademy.pawalert.domain.pet.model;

import jakarta.validation.constraints.NotBlank;

public record Breed(@NotBlank(message = "Breed can not be empty") String breed) {

    public String value() {
        return this.breed;
    }
}