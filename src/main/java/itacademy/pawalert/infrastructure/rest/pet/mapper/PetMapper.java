package itacademy.pawalert.infrastructure.rest.pet.mapper;

import itacademy.pawalert.domain.pet.model.*;
import itacademy.pawalert.infrastructure.rest.pet.dto.PetDTO;

import java.util.UUID;

public class PetMapper {

    public PetDTO toDTO(Pet pet) {
        return PetDTO.builder()
                .petId(pet.getPetId().toString())
                .chipNumber(pet.getChipNumber().value())
                .officialPetName(pet.getOfficialPetName().value())
                .workingPetName(pet.getWorkingPetName().value())
                .species(pet.getSpecies().name())
                .breed(pet.getBreed().value())
                .size(pet.getSize().name())
                .color(pet.getColor().value())
                .gender(pet.getGender().toString())
                .petDescription(pet.getPetDescription().value())
                .petImage(pet.getPetImage().value())
                .build();
    }

    public Pet toDomain(PetDTO petDTO) {
        return new Pet(
                UUID.fromString(petDTO.getUserId()),
                UUID.fromString(petDTO.getPetId()),
                new ChipNumber(petDTO.getChipNumber()),
                new PetName(petDTO.getOfficialPetName()),
                new PetName(petDTO.getWorkingPetName()),
                Species.valueOf(petDTO.getSpecies()),
                new Breed(petDTO.getBreed()),
                Size.valueOf(petDTO.getSize()),
                new Color(petDTO.getColor()),
                Gender.valueOf(petDTO.getGender()),
                new PetDescription(petDTO.getPetDescription()),
                new PetImage(petDTO.getPetImage())
        );
    }
}
