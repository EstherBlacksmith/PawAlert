package itacademy.pawalert.application.pet.port.inbound;

import itacademy.pawalert.domain.pet.model.Pet;

import java.util.UUID;

public interface GetPetUseCase {
      Pet getPetdById(UUID petId);
}
