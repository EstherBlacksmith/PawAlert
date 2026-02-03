package itacademy.pawalert.domain.pet.model;

public record PetImage(String petImage) {
    public String value() { return this.petImage; }
}