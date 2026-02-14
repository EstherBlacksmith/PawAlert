package itacademy.pawalert.domain.alert.model;

import lombok.Getter;

import java.util.Arrays;

@Getter
public enum StatusNames implements AlertDisplayableEnum {
    OPENED("Opened"), CLOSED("Closed"), SEEN("Seen"), SAFE("Safe");

    private final String value;

    StatusNames(String value) {
        this.value = value;
    }

    public static StatusNames fromString(String value) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("Status cannot be null or empty");
        }
        try {
            return StatusNames.valueOf(value.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException(
                    "Invalid status: " + value + ". Must be one of: " +
                            Arrays.toString(values())
            );
        }
    }

    @Override
    public String getDisplayName() {
        return value;
    }
}
