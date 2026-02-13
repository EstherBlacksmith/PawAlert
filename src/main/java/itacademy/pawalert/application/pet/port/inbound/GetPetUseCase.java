package itacademy.pawalert.application.pet.port.inbound;

import itacademy.pawalert.domain.pet.model.*;

import java.util.List;
import java.util.UUID;

public interface GetPetUseCase {
      Pet getPetById(UUID petId);
      List<Pet> getPetsByUserId(UUID userId);
      List<Pet> getPetsBySpecies(Species species);
      List<Pet> getPetsByBreed(Breed breed);
      List<Pet> getPetsByWorkingName(PetWorkingName name);
      List<Pet> getPetsByOfficialName(PetOfficialName name);
      Pet getPetByChipNumber(ChipNumber chipNumber);

}
