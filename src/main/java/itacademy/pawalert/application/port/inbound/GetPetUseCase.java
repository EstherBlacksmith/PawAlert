package itacademy.pawalert.application.port.inbound;

import itacademy.pawalert.domain.pet.model.Pet;

public interface GetPetUseCase {
    Pet getPetdById(String petId);
}
