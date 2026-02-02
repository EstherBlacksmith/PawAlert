package itacademy.pawalert.infrastructure.rest.pet.controller;


import itacademy.pawalert.domain.pet.model.*;
import itacademy.pawalert.domain.pet.service.PetService;
import itacademy.pawalert.infrastructure.rest.pet.dto.PetDTO;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/pets")
public class PetController {

    private final PetService petService;

    public PetController(PetService petService) {
        this.petService = petService;
    }

    @PostMapping
    public Pet createPet(@RequestBody PetDTO petDTO) {
        return petService.createPet(petDTO.getPetId(),
                petDTO.getChipNumber(),
                petDTO.getOficialPetName(),
                petDTO.getWorkingPetName(),
                petDTO.getSpecies(),
                petDTO.getBreed(),
                petDTO.getSize(),
                petDTO.getColor(),
                petDTO.getPetDescription(),
                petDTO.getPetImage()
        );

    }

}
