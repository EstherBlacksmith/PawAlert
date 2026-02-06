package itacademy.pawalert.domain.pet.model;

import itacademy.pawalert.domain.alert.model.Title;

public record PetImage(String petImage) {
    public PetImage {
    }

    public String value() { return this.petImage; }
    public static PetImage of(String petImage) {
        return new PetImage(petImage);
    }

}