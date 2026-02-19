package itacademy.pawalert.infrastructure.persistence.pet;

import itacademy.pawalert.domain.pet.model.*;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;
import java.util.UUID;

public interface PetRepository  extends
        JpaRepository<PetEntity, String>,
        JpaSpecificationExecutor<PetEntity>{

    List<PetEntity> getPetsByUserId(String userID);
    List<PetEntity> findBySpecies(String species);
    List<PetEntity> findByBreed(String breed);
    List<PetEntity> findByWorkingPetName(String name);
    List<PetEntity> findByOfficialPetName(String name);
    PetEntity findByChipNumber(String chipNumber);
    List<PetEntity> findByUserId(String userId);
}
