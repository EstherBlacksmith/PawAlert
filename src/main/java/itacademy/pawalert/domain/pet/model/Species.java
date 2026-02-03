package itacademy.pawalert.domain.pet.model;

import java.util.Arrays;

public enum Species {
    CAT, DOG, BUNNY, FERRET, TURTLE, BIRD;

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
}
