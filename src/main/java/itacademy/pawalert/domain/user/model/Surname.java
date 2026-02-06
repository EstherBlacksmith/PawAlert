package itacademy.pawalert.domain.user.model;

public record Surname(String value) {

    public Surname {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("Surname cannot be blank");
        }
        if (value.length() > 200) {
            throw new IllegalArgumentException("Surname cannot exceed 200 characters");
        }
    }

    public static Surname of(String surname) {
        return new Surname(surname);
    }
}