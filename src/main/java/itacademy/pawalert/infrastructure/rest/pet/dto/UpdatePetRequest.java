package itacademy.pawalert.infrastructure.rest.pet.dto;


import jakarta.validation.constraints.Size;

public record UpdatePetRequest(
        @Size(max = 100, message = "Name max 100 characters")
        String officialPetName,
        @Size(max = 100, message = "Working name max 100 characters")
        String workingPetName,
        @Size(max = 500, message = "Description max 500 characters")
        String petDescription,
        @Size(max = 100, message = "Species max 100 characters")
        String species,
        @Size(max = 100, message = "Breed max 100 characters")
        String breed,
        @Size(max = 100, message = "Size max 100 characters")
        String size,
        @Size(max = 100, message = "Color max 100 characters")
        String color,
        @Size(max = 100, message = "Gender max 100 characters")
        String gender,
        @Size(max = 100, message = "Gender max 100 characters")
        String chipNumber,
        String petImage
) {

    public boolean hasOfficialPetName() {
        return officialPetName != null;
    }

    public boolean hasWorkingPetName() {
        return workingPetName != null;
    }
    public boolean hasPetDescription() {
        return petDescription != null;
    }

    public boolean hasSpecies() {
        return species != null;
    }

    public boolean hasBreed() {
        return breed != null;
    }

    public boolean hasSize() {
        return size != null;
    }

    public boolean hasColor() {
        return color != null;
    }

    public boolean hasGender() {
        return gender != null;
    }

    public boolean hasChipNumber() {
        return chipNumber != null;
    }

    public boolean hasPetImage() {
        return petImage != null;
    }

}