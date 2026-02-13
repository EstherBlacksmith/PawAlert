package itacademy.pawalert.application.pet.port.inbound;

import itacademy.pawalert.domain.pet.model.*;

import java.util.List;
import java.util.UUID;

public interface GetPetUseCase {
      Pet getPetById(UUID petId);


}
