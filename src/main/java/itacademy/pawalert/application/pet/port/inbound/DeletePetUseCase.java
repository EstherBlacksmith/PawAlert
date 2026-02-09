package itacademy.pawalert.application.pet.port.inbound;

import java.util.UUID;

public interface DeletePetUseCase {
    void deletePetdById(UUID petId, UUID userId);

}

