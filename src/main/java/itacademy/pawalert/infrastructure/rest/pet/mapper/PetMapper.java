package itacademy.pawalert.infrastructure.rest.pet.mapper;

import itacademy.pawalert.domain.pet.model.*;
import itacademy.pawalert.infrastructure.rest.pet.dto.PetDTO;
import itacademy.pawalert.infrastructure.rest.pet.dto.PetResponse;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

@Component
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
                ChipNumber.of(petDTO.getChipNumber()),
                PetOfficialName.of(petDTO.getOfficialPetName()),
                PetWorkingName.of(petDTO.getWorkingPetName()),
                Species.valueOf(petDTO.getSpecies()),
                Breed.of(petDTO.getBreed()),
                Size.valueOf(petDTO.getSize()),
                Color.of(petDTO.getColor()),
                Gender.valueOf(petDTO.getGender()),
                PetDescription.of(petDTO.getPetDescription()),
                PetImage.of(petDTO.getPetImage())
        );
    }

    public PetResponse toResponse(Pet pet) {
        return new PetResponse(
                pet.getPetId().toString(),
                pet.getUserId().toString(),
                pet.getChipNumber().value(),
                pet.getWorkingPetName().value() ,
                pet.getSpecies().name(),
                pet.getBreed().value(),
                pet.getSize().name(),
                pet.getColor().color(),
                pet.getGender().name(),
                pet.getPetDescription().description(),
                pet.getPetImage().petImage()
        );
    }

    public List<PetDTO> toDTOList(List<Pet> pets) {
        return pets.stream()
                .map(this::toDTO)
                .toList();
    }

}
