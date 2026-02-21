package itacademy.pawalert.domain.pet.model;

public record PetImage(String petImage) {
    public PetImage {
    }

    public static PetImage of(String petImage) {
        return new PetImage(petImage);
    }

    public String value() {
        return this.petImage;
    }

}