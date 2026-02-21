package itacademy.pawalert.domain.pet.model;

import lombok.Getter;

import java.util.Arrays;

@Getter
public enum Gender implements PetDisplayableEnum {
    FEMALE("Female"), MALE("Male"), UNKNOWN("Unknown");

    private final String value;

    Gender(String value) {
        this.value = value;
    }

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

    @Override
    public String getDisplayName() {
        return value;
    }
}
