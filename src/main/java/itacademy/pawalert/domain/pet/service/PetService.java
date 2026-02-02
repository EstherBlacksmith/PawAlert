package itacademy.pawalert.domain.pet.service;

import itacademy.pawalert.domain.pet.model.*;
import org.springframework.stereotype.Service;

@Service
public class PetService {
    public Pet createPet(String petId, String chipNumber, String oficialPetName, String workingPetName,
                         String species, String breed, String size, String color, String petDescription,
                         String petImage) {

        PetId newPetId = new PetId(petId);
        PetName newOficialPetName = new PetName(oficialPetName);
        PetName newWorkingPetName = new PetName(workingPetName);
        ChipNumber newChipNumber = new ChipNumber(chipNumber);
        Breed newBreed = new Breed(breed);
        Color newColor = new Color(color);
        PetDescription newPetDescription = new PetDescription(petDescription);
        PetImage newPetImage = new PetImage(petImage);

        Species newSpecies = Species.valueOf(species.toUpperCase());
        Size newSize = Size.valueOf(size.toUpperCase());


        return new Pet(newPetId, newChipNumber, newOficialPetName, newWorkingPetName, newSpecies, newBreed, newSize, newColor, newPetDescription, newPetImage);
    }
}
