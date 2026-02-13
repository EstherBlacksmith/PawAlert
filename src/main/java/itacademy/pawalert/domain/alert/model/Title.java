package itacademy.pawalert.domain.alert.model;

public record Title(String title) {
    private static final int MIN_LENGTH = 5;
    private static final int MAX_LENGTH = 255;

    public Title {
        if (title == null || title.isBlank()) {
            throw new IllegalArgumentException("The Title cannot be empty");
        }
        if (title.length() < MIN_LENGTH) {
            throw new IllegalArgumentException("The title must be at least " + MIN_LENGTH + " characters");
        }
        if (title.length() > MAX_LENGTH) {
            throw new IllegalArgumentException("The title must be less than " + MAX_LENGTH + " characters");
        }
        title = title.trim();
    }

    public String getValue() {
        return title;
    }

    public static Title of(String title) {
        return new Title(title);
    }
}