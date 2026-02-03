package itacademy.pawalert.domain.pet.service;

import itacademy.pawalert.application.exception.UnauthorizedException;
import itacademy.pawalert.domain.pet.exception.PetNotFoundException;
import itacademy.pawalert.domain.pet.model.*;

import itacademy.pawalert.infrastructure.persistence.pet.PetEntity;
import itacademy.pawalert.infrastructure.persistence.pet.PetRepository;


import itacademy.pawalert.infrastructure.rest.pet.dto.UpdatePetRequest;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class PetService {

    private final PetRepository petRepository;

    public PetService(PetRepository petRepository) {
        this.petRepository = petRepository;
    }

    public Pet createPet(String userId, String petId, String chipNumber, String officialPetName, String workingPetName,
                         String species, String breed, String size, String color, String petDescription,
                         String petImage) {
        PetName newOfficialPetName = new PetName(officialPetName);
        PetName newWorkingPetName = new PetName(workingPetName);
        ChipNumber newChipNumber = new ChipNumber(chipNumber);
        Breed newBreed = new Breed(breed);
        Color newColor = new Color(color);
        PetDescription newPetDescription = new PetDescription(petDescription);
        PetImage newPetImage = new PetImage(petImage);
        Species newSpecies = Species.valueOf(species.toUpperCase());
        Size newSize = Size.valueOf(size.toUpperCase());

        Pet pet = Pet.builder()
                .userId( UUID.fromString(userId))
                .petId( UUID.fromString(petId))
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

        PetEntity entity = pet.toEntity();
        PetEntity savedPet = petRepository.save(entity);

        return savedPet.toDomain();

    }

    public Pet findById(String petId) {
        return petRepository.findById(petId)
                .map(PetEntity::toDomain)
                .orElseThrow(() -> new PetNotFoundException("Pet not found: " + petId));
    }

    public Pet updatePet(String petId, String userId, UpdatePetRequest request) {
        Pet existing = findById(petId);

        checkOwnership(existing, userId);

        Pet updatedPet = existing.with(builder -> {
            if (request.chipNumber() != null) {
                builder.chipNumber(new ChipNumber(request.chipNumber()));
            }
            if (request.officialPetName() != null) {
                builder.officialPetName(new PetName(request.officialPetName()));
            }
            if (request.workingPetName() != null) {
                builder.workingPetName(new PetName(request.workingPetName()));
            }
            if (request.species() != null) {
                builder.species(Species.fromString(request.species()));
            }
            if (request.breed() != null) {
                builder.breed(new Breed(request.breed()));
            }
            if (request.size() != null) {
                builder.size(Size.fromString(request.size()));
            }
            if (request.gender() != null) {
                builder.gender(Gender.fromString(request.gender()));
            }
            if (request.color() != null) {
                builder.color(new Color(request.color()));
            }
            if (request.petDescription() != null) {
                builder.petDescription(new PetDescription(request.petDescription()));
            }
        });

        return petRepository.save(updatedPet.toEntity()).toDomain();
    }

    private void checkOwnership(Pet existingPet, String userId) {
        if(!existingPet.getUserId().toString().equals(userId)){
            throw new UnauthorizedException("Only the owner cant modify the pet data");
        }
    }

}

