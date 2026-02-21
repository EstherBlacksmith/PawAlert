package itacademy.pawalert.domain.pet.model;

import lombok.Getter;

import java.util.Arrays;

@Getter
public enum Size implements PetDisplayableEnum {
    TINY("Tiny"), SMALL("Small"), MEDIUM("Medium"), LARGE("Large"), GIANT("Giant");

    private final String value;

    Size(String value) {
        this.value = value;
    }

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

    @Override
    public String getDisplayName() {
        return value;
    }
}
