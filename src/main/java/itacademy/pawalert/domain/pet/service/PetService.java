package itacademy.pawalert.domain.pet.service;

import itacademy.pawalert.domain.alert.model.UserId;
import itacademy.pawalert.domain.pet.model.*;

import java.util.UUID;

public class PetService {
    public Pet createPet(String petId, String chipNumber, String oficialPetName, String workingPetName,
                         String species, String breed, String size, String color, String petDescription,
                         String petImage) {

        PetId newPetId = new PetId(petId);
        PetName newOficialPetName = new PetName(oficialPetName);
        PetName newWorkingPetName = new PetName(workingPetName);
        PetName newChipNumber = new PetName(chipNumber);
        Breed newBreed = new Breed(breed);
        Color newColor = new Color(color);
        PetDescription newPetDescription = new PetDescription(petDescription);
        PetImage newPetImage = new PetImage(petImage);

        Species newSpecies = new Species(species);
        Size newSize = new Size(size);



        Pet pet = new Pet(newPetId,newChipNumber,newOficialPetName,newWorkingPetName,species,newBreed,Size,newColor,newPetDescription,newPetImage);
    }
}
