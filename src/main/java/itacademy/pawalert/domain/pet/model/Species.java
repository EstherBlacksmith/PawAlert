package itacademy.pawalert.domain.pet.model;

import lombok.Getter;

import java.util.Arrays;

@Getter
public enum Species implements PetDisplayableEnum {
    CAT("Cat"), DOG("Dog"), BUNNY("Bunny"), FERRET("Ferret"), TURTLE("Turtle"), BIRD("Bird");

    private final String value;

    Species(String value) {
        this.value = value;
    }

    public static Species fromString(String value) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("Species cannot be null or empty");
        }
        try {
            return Species.valueOf(value.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException(
                    "Invalid species: " + value + ". Must be one of: " +
                            Arrays.toString(values())
            );
        }
    }

    @Override
    public String getDisplayName() {
        return value;
    }
}
