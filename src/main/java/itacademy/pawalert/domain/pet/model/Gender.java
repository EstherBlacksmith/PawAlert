package itacademy.pawalert.domain.pet.model;

import java.util.Arrays;

public enum Gender {
    FEMALE, MALE, UNKNOWN;

    public static Gender fromString(String value) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("Gender cannot be null or empty");
        }
        try {
            return Gender.valueOf(value.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException(
                    "Invalid gender: " + value + ". Must be one of: " +
                            Arrays.toString(values())
            );
        }
    }
}
