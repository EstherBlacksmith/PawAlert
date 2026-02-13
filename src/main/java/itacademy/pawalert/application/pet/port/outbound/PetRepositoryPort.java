package itacademy.pawalert.application.pet.port.outbound;

import itacademy.pawalert.domain.pet.model.*;
import itacademy.pawalert.infrastructure.persistence.pet.PetEntity;

import java.util.List;
import java.util.Optional;
import java.util.UUID;


public interface PetRepositoryPort {
    List<Pet> getPetsByOwnerId(UUID userId) ;
    List<Pet> getPetsBySpecies(Species species);
    PetEntity save(Pet pet);

    Optional<Pet> findById(UUID petId);

    void deleteById(UUID petId, UUID userId);

    boolean existsById(UUID petId);


    List<Pet> getPetsByBreed(Breed breed);

    List<Pet> getPetsByWorkingName(PetWorkingName name);

    List<Pet> getPetsByOfficialName(PetOfficialName name);

    Pet getPetByChipNumber(ChipNumber chipNumber);
}
