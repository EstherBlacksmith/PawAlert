package itacademy.pawalert.application.pet.port.inbound;

import itacademy.pawalert.domain.pet.model.*;
import itacademy.pawalert.infrastructure.rest.pet.dto.CreatePetRequest;

import java.util.UUID;

public interface CreatePetUseCase {
    Pet createPet(CreatePetRequest request);
}
