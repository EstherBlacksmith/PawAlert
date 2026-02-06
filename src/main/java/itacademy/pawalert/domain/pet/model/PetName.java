package itacademy.pawalert.domain.pet.model;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.NotBlank;

public record PetName(String value) {

    public String value() {
        return this.value;
    }
    public static PetName of(String petName) {
        return new PetName(petName);
    }



    private static final java.util.regex.Pattern NAME_PATTERN =
            java.util.regex.Pattern.compile("^[a-zA-ZÁÉÍÓÚÜÑáéíóúüñÇç'\\-\\s]+$");

    private static final int MIN_LENGTH = 3;
    private static final int MAX_LENGTH = 50;

    public PetName {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("The name cant be empty");
        }

        value = value.trim();

        if (value.length() < MIN_LENGTH) {
            throw new IllegalArgumentException("The name must have al least " + MIN_LENGTH + " characters");
        }
        if (value.length() > MAX_LENGTH) {
            throw new IllegalArgumentException("The name can't exceed " + MAX_LENGTH + " characters");
        }

        if (!NAME_PATTERN.matcher(value).matches()) {
            throw new IllegalArgumentException("El nombre contiene caracteres inválidos");
        }

    }

}
