package itacademy.pawalert.domain.pet.model;

public record PetImage(String petImage) {

    public static PetImage of(String petImage) {
        if (petImage == null || petImage.isBlank()) {
            throw new IllegalArgumentException("The pet image cannot be empty");
        }
        return new PetImage(petImage);
    }

    public static PetImage ofNullable(String petImage) {
        if (petImage == null || petImage.isBlank()) {
            return null;
        }
        return new PetImage(petImage);
    }

    public String value() {
        return this.petImage;
    }
}