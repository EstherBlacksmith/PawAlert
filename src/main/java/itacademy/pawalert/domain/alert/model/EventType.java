package itacademy.pawalert.domain.alert.model;

import lombok.Getter;

import java.util.Arrays;

@Getter
public enum EventType implements AlertDisplayableEnum{
    STATUS_CHANGED("Status changed"),
    TITLE_CHANGED("Title changed"),
    DESCRIPTION_CHANGED("Description changed");

    private final String value;

    EventType(String value) {
        this.value = value;
    }

    public static EventType fromString(String value) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("EventType cannot be null or empty");
        }
        try {
            return EventType.valueOf(value.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException(
                    "Invalid event type: " + value + ". Must be one of: " +
                            Arrays.toString(values())
            );
        }
    }

    @Override
    public String getDisplayName() {
        return value;
    }
}