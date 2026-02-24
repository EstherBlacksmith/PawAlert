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
                .userId(pet.getUserId().toString())
                .petId(pet.getPetId().toString())
                .chipNumber(pet.getChipNumber() != null ? pet.getChipNumber().value() : null)
                .officialPetName(pet.getOfficialPetName() != null ? pet.getOfficialPetName().value() : null)
                .workingPetName(pet.getWorkingPetName() != null ? pet.getWorkingPetName().value() : null)
                .species(pet.getSpecies().name())
                .breed(pet.getBreed() != null ? pet.getBreed().value() : null)
                .size(pet.getSize().name())
                .color(pet.getColor() != null ? pet.getColor().value() : null)
                .gender(pet.getGender().name())
                .petDescription(pet.getPetDescription() != null ? pet.getPetDescription().value() : null)
                .petImage(pet.getPetImage() != null ? pet.getPetImage().value() : null)
                .build();
    }

    public Pet toDomain(PetDTO petDTO) {
        return new Pet(
                UUID.fromString(petDTO.getUserId()),
                UUID.fromString(petDTO.getPetId()),
                ChipNumber.ofNullable(petDTO.getChipNumber()),
                PetOfficialName.of(petDTO.getOfficialPetName()),
                PetWorkingName.ofNullable(petDTO.getWorkingPetName()),
                Species.valueOf(petDTO.getSpecies()),
                Breed.ofNullable(petDTO.getBreed()),
                Size.valueOf(petDTO.getSize()),
                Color.ofNullable(petDTO.getColor()),
                Gender.valueOf(petDTO.getGender()),
                PetDescription.ofNullable(petDTO.getPetDescription()),
                PetImage.ofNullable(petDTO.getPetImage())
        );
    }

    public PetResponse toResponse(Pet pet) {
        return new PetResponse(
                pet.getPetId().toString(),
                pet.getUserId().toString(),
                pet.getChipNumber() != null ? pet.getChipNumber().value() : null,
                pet.getOfficialPetName() != null ? pet.getOfficialPetName().value() : null,
                pet.getWorkingPetName() != null ? pet.getWorkingPetName().value() : null,
                pet.getSpecies().name(),
                pet.getBreed() != null ? pet.getBreed().value() : null,
                pet.getSize().name(),
                pet.getColor() != null ? pet.getColor().value() : null,
                pet.getGender().name(),
                pet.getPetDescription() != null ? pet.getPetDescription().value() : null,
                pet.getPetImage() != null ? pet.getPetImage().value() : null
        );
    }

    public List<PetDTO> toDTOList(List<Pet> pets) {
        return pets.stream()
                .map(this::toDTO)
                .toList();
    }

}
