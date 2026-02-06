package itacademy.pawalert.domain.pet.model;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.NotBlank;

public record PetName(@NotBlank(message = "The name cant be empty")
                      @Max(value = 150, message = "The name can't exceed 150 characters") String petName) {

    public String value() {
        return this.petName;
    }
    public static PetName of(String petName) {
        return new PetName(petName);
    }

}
