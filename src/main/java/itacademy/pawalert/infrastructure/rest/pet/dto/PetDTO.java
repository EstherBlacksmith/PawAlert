package itacademy.pawalert.infrastructure.rest.pet.dto;

import itacademy.pawalert.domain.pet.model.*;
import lombok.Getter;

import java.util.UUID;

@Getter
public class PetDTO {

    private UUID petId;
    private ChipNumber chipNumber;
    private PetName oficialPetName;
    private PetName workingPetName;
    private Species species;
    private Breed breed;
    private Size size;
    private Color color;
    private PetDescription petDescription;
    private PetImage petImage;
}
