package itacademy.pawalert.infrastructure.persistence.pet;

import itacademy.pawalert.application.pet.port.outbound.PetRepositoryPort;
import itacademy.pawalert.domain.pet.model.*;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
public class PetRepositoryAdapter implements PetRepositoryPort {

    private final PetRepository petRepository;

    public PetRepositoryAdapter(PetRepository petRepository) {
        this.petRepository = petRepository;
    }

    @Override
    public List<Pet> getPetsByOwnerId(UUID userId) {
        return petRepository.getPetsByUserId(userId)
                .stream()
                .map(PetEntity::toDomain)
                .collect(Collectors.toList());    }

    @Override
    public List<Pet> getPetsBySpecies(Species species) {
        return petRepository.findBySpecies(species).stream()
                .map(PetEntity::toDomain)
                .collect(Collectors.toList());

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
    public List<Pet> getPetsByBreed(Breed breed) {
        return petRepository.findByBreed(breed);
    }

    @Override
    public List<Pet> getPetsByWorkingName(PetWorkingName name) {
        return petRepository.findByWorkingName(name);
    }

    @Override
    public List<Pet> getPetsByOfficialName(PetOfficialName name) {
        return petRepository.findByOfficialName(name);
    }

    @Override
    public Pet getPetByChipNumber(ChipNumber chipNumber) {
        return petRepository.findByChipNumber(chipNumber);
    }
}
