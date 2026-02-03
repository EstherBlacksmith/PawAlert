package itacademy.pawalert.infrastructure.rest.pet.controller;



import itacademy.pawalert.domain.pet.model.Pet;
import itacademy.pawalert.domain.pet.service.PetService;
import itacademy.pawalert.infrastructure.rest.pet.dto.PetDTO;

import itacademy.pawalert.infrastructure.rest.pet.dto.UpdatePetRequest;
import itacademy.pawalert.infrastructure.rest.pet.mapper.PetMapper;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/pets")
public class PetController {

    private final PetMapper petMapper;
    private final PetService petService;

    public PetController(PetMapper petMapper, PetService petService) {
        this.petMapper = petMapper;
        this.petService = petService;
    }

    @PostMapping
    public Pet createPet(@RequestBody PetDTO petDTO) {
        return petService.createPet(
                petDTO.getUserId(),
                petDTO.getPetId(),
                petDTO.getChipNumber(),
                petDTO.getOfficialPetName(),
                petDTO.getWorkingPetName(),
                petDTO.getSpecies(),
                petDTO.getBreed(),
                petDTO.getSize(),
                petDTO.getColor(),
                petDTO.getPetDescription(),
                petDTO.getPetImage()
        );

    }

    @GetMapping("/{id}")
    public ResponseEntity<PetDTO> getPet(@PathVariable String id) {
        Pet pet = petService.findById(id);

        return ResponseEntity.ok(petMapper.toDTO(pet));
    }
    @PatchMapping("/{petId}")
    public ResponseEntity<PetDTO> updatePet(
            @PathVariable String petId,
            @Valid @RequestBody UpdatePetRequest request) {

        String userId = getCurrentUserId();
        Pet updated = petService.updatePet(petId, userId, request);

        return ResponseEntity.ok(petMapper.toDTO(updated));
    }

    private String getCurrentUserId() {
        //TODO
        return "";
    }
}
