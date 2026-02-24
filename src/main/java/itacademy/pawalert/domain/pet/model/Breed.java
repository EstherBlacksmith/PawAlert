package itacademy.pawalert.domain.pet.model;


public record Breed(String breed) {

    public Breed {
    }

    public static Breed of(String breed) {
        if (breed == null || breed.isBlank()) {
            throw new IllegalArgumentException("The breed cannot be empty");
        }
        return new Breed(breed.trim());
    }

    public static Breed ofNullable(String breed) {
        if (breed == null || breed.isBlank()) {
            return null;
        }
        return new Breed(breed.trim());
    }

    public String value() {
        return this.breed;
    }
}