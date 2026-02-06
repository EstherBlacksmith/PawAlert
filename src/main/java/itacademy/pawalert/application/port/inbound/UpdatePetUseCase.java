package itacademy.pawalert.application.port.inbound;

import itacademy.pawalert.domain.pet.model.Pet;
import itacademy.pawalert.infrastructure.rest.pet.dto.UpdatePetRequest;

import java.util.UUID;

public interface UpdatePetUseCase {
    Pet updatePet(UUID petId, UUID userId, UpdatePetRequest request);
}
