package itacademy.pawalert.domain.pet.model;

import java.util.Arrays;

public enum Size {
    TINY, SMALL, MEDIUM, LARGE, GIANT;

    public static Size fromString(String value) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("Size cannot be null or empty");
        }
        try {
            return Size.valueOf(value.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException(
                    "Invalid size: " + value + ". Must be one of: " +
                            Arrays.toString(values())
            );
        }
    }
}
