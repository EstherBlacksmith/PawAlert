package itacademy.pawalert.domain.alert.model;


import lombok.Getter;

import java.util.Arrays;

@Getter
public enum ClosureReason implements AlertDisplayableEnum {
    FOUNDED("Founded"), FALSE_ALARM("False alarm"), OTHER_REASON("Other reason");

    private final String value;

    ClosureReason(String value) {
        this.value = value;
    }

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

    @Override
    public String getDisplayName() {
        return value;
    }

}
