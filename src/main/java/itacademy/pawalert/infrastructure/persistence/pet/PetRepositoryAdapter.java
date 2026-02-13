package itacademy.pawalert.infrastructure.persistence.pet;

import itacademy.pawalert.application.pet.port.outbound.PetRepositoryPort;
import itacademy.pawalert.domain.pet.model.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.UUID;


@Component
public class PetRepositoryAdapter implements PetRepositoryPort {

    private final PetRepository petRepository;

    public PetRepositoryAdapter(PetRepository petRepository) {
        this.petRepository = petRepository;
    }


    @Override
    public PetEntity save(Pet pet) {
        return petRepository.save(pet.toEntity());
    }

    @Override
    public Optional<Pet> findById(UUID petId) {
        return petRepository.findById(petId.toString())
                .map(PetEntity::toDomain);
    }

    @Override
    public void deleteById(UUID petId, UUID userId) {
        petRepository.deleteById(petId.toString());
    }

    @Override
    public boolean existsById(UUID petId) {
        return petRepository.existsById(petId.toString());
    }

    @Override
    public List<Pet> findAll(Specification<Pet> spec, Sort sort) {
        return petRepository.findAll(spec, sort);
    }

    @Override
    public Page<Pet> findAll(Specification<Pet> spec, Pageable pageable) {
        return petRepository.findAll(spec, pageable);
    }

    @Override
    public List<Pet> findAllByUserId(UUID userId) {
        return petRepository.findByUserId(userId).stream()
                .map(PetEntity::toDomain)
                .toList();
    }
}
