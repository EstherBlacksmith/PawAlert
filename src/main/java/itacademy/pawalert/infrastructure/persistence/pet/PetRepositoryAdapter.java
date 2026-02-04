package itacademy.pawalert.infrastructure.persistence.pet;

import itacademy.pawalert.application.port.outbound.PetRepositoryPort;
import itacademy.pawalert.domain.pet.model.Pet;
import org.springframework.stereotype.Component;

import java.util.Optional;

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
    public Optional<Pet> findById(String petId) {
        return petRepository.findById(petId)
                .map(PetEntity::toDomain);
    }
}
