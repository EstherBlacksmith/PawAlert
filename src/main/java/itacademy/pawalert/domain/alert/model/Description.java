package itacademy.pawalert.domain.alert.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;


public record Description( String description) {

    private static final int MIN_LENGTH = 25;
    private static final int MAX_LENGTH = 500;

    public Description {
        if (description == null || description.isBlank()) {
            throw new IllegalArgumentException("The description cannot be empty");
        }
        if (description.length() < MIN_LENGTH) {
            throw new IllegalArgumentException("The description must be at least " + MIN_LENGTH + " characters");
        }
        if (description.length() > MAX_LENGTH) {
            throw new IllegalArgumentException("The description must be less than " + MAX_LENGTH + " characters");
        }
        description = description.trim();
    }


    @JsonValue
    public String getValue() {
        return description;
    }

    @JsonCreator
    public static Description of(String description) {
        return new Description(description);
    }

}
