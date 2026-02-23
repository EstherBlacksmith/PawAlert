package itacademy.pawalert.infrastructure.persistence.pet;

import itacademy.pawalert.application.pet.port.outbound.PetRepositoryPort;
import itacademy.pawalert.domain.pet.model.Pet;
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
    public Pet save(Pet pet) {
        PetEntity savedEntity = petRepository.save(pet.toEntity());
        return savedEntity.toDomain();
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
        Specification<PetEntity> entitySpec = (Specification<PetEntity>) (Specification<?>) spec;
        return petRepository.findAll(entitySpec, sort).stream()
                .map(PetEntity::toDomain)
                .toList();
    }

    @Override
    public Page<Pet> findAll(Specification<Pet> spec, Pageable pageable) {

        Specification<PetEntity> entitySpec = (Specification<PetEntity>) (Specification<?>) spec;
        return petRepository.findAll(entitySpec, pageable)
                .map(PetEntity::toDomain);
    }

    @Override
    public List<Pet> findAllByUserId(UUID userId) {
        return petRepository.findByUserId(userId.toString()).stream()
                .map(PetEntity::toDomain)
                .toList();
    }

    @Override
    public List<Pet> findAll() {
        return petRepository.findAll().stream().
                map(PetEntity::toDomain)
                .toList();
    }
}
