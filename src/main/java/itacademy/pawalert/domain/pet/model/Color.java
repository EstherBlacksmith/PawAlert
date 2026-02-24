package itacademy.pawalert.domain.pet.model;


public record Color(String color) {

    public Color {
    }

    public static Color of(String color) {
        if (color == null || color.isBlank()) {
            throw new IllegalArgumentException("The color cannot be empty");
        }
        return new Color(color.trim());
    }

    public static Color ofNullable(String color) {
        if (color == null || color.isBlank()) {
            return null;
        }
        return new Color(color.trim());
    }

    public String value() {
        return this.color;
    }
}
