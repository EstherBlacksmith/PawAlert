package itacademy.pawalert.domain.alert.model;


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


    public String getValue() {
        return description;
    }

    public static Description of(String description) {
        return new Description(description);
    }

}
