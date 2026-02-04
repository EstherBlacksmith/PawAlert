package itacademy.pawalert.application.port.inbound;

import itacademy.pawalert.domain.pet.model.Pet;
import itacademy.pawalert.infrastructure.rest.pet.dto.UpdatePetRequest;

public interface UpdatePetUseCase {
    Pet updatePet(String petId, String userId, UpdatePetRequest request);
}
