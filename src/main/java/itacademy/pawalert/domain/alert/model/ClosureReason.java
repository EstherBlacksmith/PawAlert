package itacademy.pawalert.domain.alert.model;

import itacademy.pawalert.domain.pet.model.Gender;

import java.util.Arrays;

public enum ClosureReason {
    FOUNDED, FALSE_ALARM, OTHER_REASON;

    public static ClosureReason fromString(String value) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("Closure reason cannot be null or empty");
        }
        try {
            return ClosureReason.valueOf(value.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException(
                    "Invalid reason: " + value + ". Must be one of: " +
                            Arrays.toString(values())
            );
        }
    }

}
