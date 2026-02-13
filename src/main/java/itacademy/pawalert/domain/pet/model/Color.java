package itacademy.pawalert.domain.pet.model;


public record Color(String color) {

    public Color {
        if(color == null  || color.isBlank()) {
            throw new IllegalArgumentException("The color cannot be empty");
        }
    }
    public String value() {
        return this.color;
    }

    public static Color of(String color) {
        return new Color(color);
    }
}
