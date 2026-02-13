package itacademy.pawalert.infrastructure.persistence.pet;

import itacademy.pawalert.domain.pet.model.*;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface PetRepository  extends JpaRepository<PetEntity, String> {
    List<PetEntity> getPetsByUserId(UUID userID);

    List<PetEntity> findBySpecies(Species species);

    List<Pet> findByBreed(Breed breed);

    List<Pet> findByWorkingName(PetWorkingName name);

    List<Pet> findByOfficialName(PetOfficialName name);

    Pet findByChipNumber(ChipNumber chipNumber);
}
