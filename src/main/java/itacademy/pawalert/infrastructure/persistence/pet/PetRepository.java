package itacademy.pawalert.infrastructure.persistence.pet;

import itacademy.pawalert.domain.pet.model.*;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;
import java.util.UUID;

public interface PetRepository  extends
        JpaRepository<PetEntity, String>,
        JpaSpecificationExecutor<PetEntity>{

    List<PetEntity> getPetsByUserId(UUID userID);
    List<PetEntity> findBySpecies(Species species);
    List<PetEntity> findByBreed(Breed breed);
    List<PetEntity> findByWorkingPetName(PetWorkingName name);
    List<PetEntity> findByOfficialPetName(PetOfficialName name);
    PetEntity findByChipNumber(ChipNumber chipNumber);
    List<PetEntity> findByUserId(UUID userId);
}
