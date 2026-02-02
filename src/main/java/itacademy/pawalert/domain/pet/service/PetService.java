package itacademy.pawalert.domain.pet.service;

import itacademy.pawalert.application.exception.UnauthorizedException;
import itacademy.pawalert.domain.alert.model.UserId;
import itacademy.pawalert.domain.pet.exception.PetNotFoundException;
import itacademy.pawalert.domain.pet.model.*;

import itacademy.pawalert.infrastructure.persistence.pet.PetEntity;
import itacademy.pawalert.infrastructure.persistence.pet.PetRepository;

import org.springframework.stereotype.Service;

@Service
public class PetService {

    PetRepository petRepository;

    public PetService(PetRepository petRepository) {
        this.petRepository = petRepository;
    }

    public Pet createPet(String userId, String petId, String chipNumber, String officialPetName, String workingPetName,
                         String species, String breed, String size, String color, String petDescription,
                         String petImage) {
        UserId newUserId = new UserId(userId);
        PetId newPetId = new PetId(petId);
        PetName newOfficialPetName = new PetName(officialPetName);
        PetName newWorkingPetName = new PetName(workingPetName);
        ChipNumber newChipNumber = new ChipNumber(chipNumber);
        Breed newBreed = new Breed(breed);
        Color newColor = new Color(color);
        PetDescription newPetDescription = new PetDescription(petDescription);
        PetImage newPetImage = new PetImage(petImage);
        Species newSpecies = Species.valueOf(species.toUpperCase());
        Size newSize = Size.valueOf(size.toUpperCase());

        return Pet.builder()
                .userId(newUserId)
                .petId(newPetId)
                .chipNumber(newChipNumber)
                .officialPetName(newOfficialPetName)
                .workingPetName(newWorkingPetName)
                .species(newSpecies)
                .breed( newBreed)
                .size(newSize)
                .color(newColor)
                .petDescription(newPetDescription)
                .petImage(newPetImage)
                .build();
    }

    public Pet findById(String petId) {
        return petRepository.findById(petId)
                .map(PetEntity::toDomain)
                .orElseThrow(() -> new PetNotFoundException("Pet not found: " + petId));
    }

    public Pet updatePetDescription(String petId, String userId, String petDescription) {
        Pet pet = findById(petId);

        if (!pet.getUserId().value().equals(userId)) {
            throw new UnauthorizedException("Just authorized users can modify this pet description");
        }
        Pet updatedPet = pet.with(builder -> {
            builder.petDescription(new PetDescription(petDescription));
        });

        return petRepository.save(updatedPet.toEntity()).toDomain();
    }

}
