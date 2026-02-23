package itacademy.pawalert.domain.pet.model;


public record Color(String color) {

    public Color {
        if (color == null || color.isBlank()) {
            throw new IllegalArgumentException("The color cannot be empty");
        }
    }

    public static Color of(String color) {
        return new Color(color);
    }

    public String value() {
        return this.color;
    }
}
