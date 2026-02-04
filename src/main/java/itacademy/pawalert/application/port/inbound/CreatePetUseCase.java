package itacademy.pawalert.application.port.inbound;

import itacademy.pawalert.domain.pet.model.Pet;

public interface CreatePetUseCase {


    Pet createPet(String userId, String petId, String chipNumber, String officialPetName, String workingPetName,
                  String species, String breed, String size, String color, String gender, String petDescription,
                  String petImage);
}
