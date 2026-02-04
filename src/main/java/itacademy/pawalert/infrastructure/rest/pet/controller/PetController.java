package itacademy.pawalert.infrastructure.rest.pet.controller;

import itacademy.pawalert.domain.pet.model.Pet;
import itacademy.pawalert.application.port.inbound.*;
import itacademy.pawalert.infrastructure.rest.pet.dto.PetDTO;

import itacademy.pawalert.infrastructure.rest.pet.dto.UpdatePetRequest;
import itacademy.pawalert.infrastructure.rest.pet.mapper.PetMapper;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/pets")
public class PetController {

    private final CreatePetUseCase createPetUseCase;
    private final GetPetUseCase getPetUseCase;
    private final UpdatePetUseCase updatePetUseCase;
    private final PetMapper petMapper;

    public PetController(CreatePetUseCase createPetUseCase,
                         GetPetUseCase getPetUseCase,
                         UpdatePetUseCase updatePetUseCase,
                         PetMapper petMapper) {
        this.createPetUseCase = createPetUseCase;
        this.getPetUseCase = getPetUseCase;
        this.updatePetUseCase = updatePetUseCase;
        this.petMapper = petMapper;

    }

    @PostMapping
    public Pet createPet(@RequestBody PetDTO petDTO) {
        return createPetUseCase.createPet(
                petDTO.getUserId(),
                petDTO.getPetId(),
                petDTO.getChipNumber(),
                petDTO.getOfficialPetName(),
                petDTO.getWorkingPetName(),
                petDTO.getSpecies(),
                petDTO.getBreed(),
                petDTO.getSize(),
                petDTO.getColor(),
                petDTO.getGender(),
                petDTO.getPetDescription(),
                petDTO.getPetImage()
        );

    }

    @GetMapping("/{id}")
    public ResponseEntity<PetDTO> getPet(@PathVariable String id) {
        Pet pet = getPetUseCase.getPetdById(id);

        return ResponseEntity.ok(petMapper.toDTO(pet));
    }

    @PatchMapping("/{petId}")
    public ResponseEntity<PetDTO> updatePet(
            @PathVariable String petId,
            @Valid @RequestBody UpdatePetRequest request) {

        String userId = getCurrentUserId();
        Pet updated = updatePetUseCase.updatePet(petId, userId, request);

        return ResponseEntity.ok(petMapper.toDTO(updated));
    }

    private String getCurrentUserId() {
        //TODO
        return "";
    }
}
