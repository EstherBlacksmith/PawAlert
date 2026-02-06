package itacademy.pawalert.application.port.outbound;

import itacademy.pawalert.domain.pet.model.Pet;
import itacademy.pawalert.infrastructure.persistence.pet.PetEntity;

import java.util.Optional;
import java.util.UUID;


public interface PetRepositoryPort {
    PetEntity save(PetEntity entity);

    Optional<Pet> findById(UUID petId);

   void deleteById(UUID petId);

    boolean existsById(UUID petId);


}
