package itacademy.pawalert.application.port.inbound;

import itacademy.pawalert.domain.pet.model.*;

import java.util.UUID;

public interface CreatePetUseCase {


    Pet createPet(UUID userId, UUID petId, ChipNumber chipNumber, PetName officialPetName, PetName workingPetName,
                  Species species, Breed breed, Size size, Color color, Gender gender, PetDescription petDescription,
                  PetImage petImage);
}
