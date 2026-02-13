package itacademy.pawalert.domain.pet.model;


public record Breed(String breed) {

    public Breed {
        if(breed == null  || breed.isBlank()) {
            throw new IllegalArgumentException("The breed cannot be empty");
        }
    }

    public String value() {
        return this.breed;
    }
    public static Breed of(String breed) {
        return new Breed(breed);
    }

    public Breed() {
        this(null);
    }
}