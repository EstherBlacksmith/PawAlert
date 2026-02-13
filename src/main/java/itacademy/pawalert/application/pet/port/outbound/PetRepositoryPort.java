package itacademy.pawalert.application.pet.port.outbound;

import itacademy.pawalert.domain.pet.model.*;
import itacademy.pawalert.infrastructure.persistence.pet.PetEntity;

import java.util.List;
import java.util.Optional;
import java.util.UUID;


public interface PetRepositoryPort {
    PetEntity save(Pet pet);

    Optional<Pet> findById(UUID petId);

    void deleteById(UUID petId, UUID userId);

    boolean existsById(UUID petId);


}
