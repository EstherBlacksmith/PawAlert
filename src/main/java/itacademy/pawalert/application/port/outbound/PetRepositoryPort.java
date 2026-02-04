package itacademy.pawalert.application.port.outbound;

import itacademy.pawalert.domain.pet.model.Pet;
import itacademy.pawalert.infrastructure.persistence.pet.PetEntity;

import java.util.Optional;


public interface PetRepositoryPort {
    PetEntity save(PetEntity entity);

    Optional<Pet> findById(String petId);
}
