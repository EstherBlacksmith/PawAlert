package itacademy.pawalert.domain.pet.model;


public record PetDescription(String description) {

    private static final int MIN_LENGTH = 25;
    private static final int MAX_LENGTH = 255;

    public PetDescription {
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

    public static PetDescription of(String description) {
        return new PetDescription(description);
    }

    public String value() {
        return description;
    }

}