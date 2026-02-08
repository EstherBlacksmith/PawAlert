package itacademy.pawalert.application.port.inbound;

import jakarta.transaction.Transactional;

import java.util.UUID;

public interface DeletePetUseCase {
    void deletePetdById(UUID petId, UUID userId);

}

