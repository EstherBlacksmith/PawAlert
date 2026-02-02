package itacademy.pawalert.infrastructure.rest.pet.mapper;

import itacademy.pawalert.domain.pet.model.*;
import itacademy.pawalert.infrastructure.rest.pet.dto.PetDTO;

public class PetMapper {

    public PetDTO toDTO(Pet pet) {
        return PetDTO.builder()
                .petId(pet.petId().toString())
                .chipNumber(pet.chipNumber().toString())
                .officialPetName(pet.officialPetName().toString())
                .workingPetName(pet.workingPetName().toString())
                .species(pet.species().name())
                .breed(pet.breed().toString())
                .size(pet.size().name())
                .color(pet.color().toString())
                .petDescription(pet.petDescription().toString())
                .petImage(pet.petImage().toString())
                .build();
    }

    public Pet toDomain(PetDTO petDTO) {

        PetId petId = new PetId(petDTO.getPetId());
        return new Pet(
                petId,
                new ChipNumber(petDTO.getChipNumber()),
                new PetName(petDTO.getOfficialPetName()),
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
