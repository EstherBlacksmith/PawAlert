package itacademy.pawalert.domain.user.model;

import com.fasterxml.jackson.annotation.JsonValue;

public record Surname(@JsonValue String value) {

    private static final java.util.regex.Pattern SURNAME_PATTERN =
            java.util.regex.Pattern.compile("^[a-zA-ZÁÉÍÓÚÜÑáéíóúüñÇç'\\-\\s]+$");

    public Surname {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("Surname cannot be empty");
        }
        if (value.length() > 200) {
            throw new IllegalArgumentException("Surname cannot exceed 200 characters");
        }

        if (!SURNAME_PATTERN.matcher(value).matches()) {
            throw new IllegalArgumentException("Surname contains invalid characters");
        }
    }

    public boolean isCompound() {
        return value.contains(" ") || value.contains("-") || value.contains("'");
    }

    public static Surname of(String surname) {
        return new Surname(surname);
    }
}