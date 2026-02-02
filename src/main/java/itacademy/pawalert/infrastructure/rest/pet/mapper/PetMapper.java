package itacademy.pawalert.infrastructure.rest.pet.mapper;

import itacademy.pawalert.domain.alert.model.UserId;
import itacademy.pawalert.domain.pet.model.*;
import itacademy.pawalert.infrastructure.rest.pet.dto.PetDTO;

import java.util.UUID;

public class PetMapper {

    public PetDTO toDTO(Pet pet) {
        return PetDTO.builder()
                .petId(pet.getPetId().toString())
                .chipNumber(pet.getChipNumber().toString())
                .oficialPetName(pet.getOficialPetName().toString())
                .oficialPetName(pet.getWorkingPetName().toString())
                .species(pet.getSpecies().name())
                .breed(pet.getBreed().toString())
                .size(pet.getSize().name())
                .color(pet.getColor().toString())
                .petDescription(pet.getPetDescription().toString())
                .petImage(pet.getPetImage().toString())
                .build();
    }

    public Pet toDomain(PetDTO petDTO) {

        PetId petId = new PetId(petDTO.getPetId());
        return new Pet(
                petId,
                new ChipNumber(petDTO.getChipNumber()),
                new PetName(petDTO.getOficialPetName()),
                new PetName(petDTO.getWorkingPetName()),
                Species.valueOf(petDTO.getSpecies()),
                new Breed(petDTO.getBreed()),
                Size.valueOf(petDTO.getSize()),
                new Color(petDTO.getColor()),
                new PetDescription(petDTO.getPetDescription()),
                new PetImage(petDTO.getPetImage())
        );
    }
}
