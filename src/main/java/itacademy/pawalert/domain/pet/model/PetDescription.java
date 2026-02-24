package itacademy.pawalert.domain.pet.model;


public record PetDescription(String description) {

    private static final int MIN_LENGTH = 25;
    private static final int MAX_LENGTH = 255;

    public static PetDescription of(String description) {
        if (description == null || description.isBlank()) {
            throw new IllegalArgumentException("The description cannot be empty");
        }
        String trimmed = description.trim();
        if (trimmed.length() < MIN_LENGTH) {
            throw new IllegalArgumentException("The description must be at least " + MIN_LENGTH + " characters");
        }
        if (trimmed.length() > MAX_LENGTH) {
            throw new IllegalArgumentException("The description must be less than " + MAX_LENGTH + " characters");
        }
        return new PetDescription(trimmed);
    }

    public static PetDescription ofNullable(String description) {
        if (description == null || description.isBlank()) {
            return null;
        }
        String trimmed = description.trim();
        if (trimmed.length() < MIN_LENGTH) {
            throw new IllegalArgumentException("The description must be at least " + MIN_LENGTH + " characters");
        }
        if (trimmed.length() > MAX_LENGTH) {
            throw new IllegalArgumentException("The description must be less than " + MAX_LENGTH + " characters");
        }
        return new PetDescription(trimmed);
    }

    public String value() {
        return description;
    }
}