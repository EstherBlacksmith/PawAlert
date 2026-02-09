package itacademy.pawalert.infrastructure.persistence.pet;

import itacademy.pawalert.application.pet.port.outbound.PetRepositoryPort;
import itacademy.pawalert.domain.pet.model.Pet;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.UUID;

@Component
public class PetRepositoryAdapter implements PetRepositoryPort {

    private final PetRepository petRepository;

    public PetRepositoryAdapter(PetRepository petRepository) {
        this.petRepository = petRepository;
    }
    @Override
    public PetEntity save(PetEntity entity) {
        return petRepository.save(entity);
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
}
