package itacademy.pawalert.application.pet.port.inbound;

import itacademy.pawalert.domain.pet.model.Pet;
import itacademy.pawalert.infrastructure.rest.pet.dto.CreatePetRequest;

public interface CreatePetUseCase {
    Pet createPet(CreatePetRequest request);
}
