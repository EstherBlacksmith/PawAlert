package itacademy.pawalert.application.pet.port.outbound;

import itacademy.pawalert.domain.pet.model.*;
import itacademy.pawalert.infrastructure.persistence.pet.PetEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;

import java.util.List;
import java.util.Optional;
import java.util.UUID;


public interface PetRepositoryPort {
    PetEntity save(Pet pet);
    Optional<Pet> findById(UUID petId);
    void deleteById(UUID petId, UUID userId);
    boolean existsById(UUID petId);
    List<Pet> findAll(Specification<Pet> spec, Sort sort);
    Page<Pet> findAll(Specification<Pet> spec, Pageable pageable);
    List<Pet> findAllByUserId(UUID userId);
}
