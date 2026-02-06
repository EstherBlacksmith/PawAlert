package itacademy.pawalert.infrastructure.rest.pet.controller;

import itacademy.pawalert.domain.alert.model.EventType;
import itacademy.pawalert.domain.pet.model.*;
import itacademy.pawalert.application.port.inbound.*;
import itacademy.pawalert.infrastructure.rest.pet.dto.PetDTO;

import itacademy.pawalert.infrastructure.rest.pet.dto.UpdatePetRequest;
import itacademy.pawalert.infrastructure.rest.pet.mapper.PetMapper;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import itacademy.pawalert.infrastructure.security.UserDetailsAdapter;
import itacademy.pawalert.application.exception.UnauthorizedException;

import java.util.UUID;


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
    public Pet createPet(@Valid @RequestBody PetDTO petDTO) {
        return createPetUseCase.createPet(
                UUID.fromString(petDTO.getUserId()),
                UUID.fromString(petDTO.getPetId()),
                ChipNumber.of(petDTO.getChipNumber()),
                PetName.of(petDTO.getOfficialPetName()),
                PetName.of(petDTO.getWorkingPetName()),
                Species.valueOf( petDTO.getSpecies()),
                Breed.of(petDTO.getBreed()),
                Size.valueOf( petDTO.getSize()),
                Color.of(petDTO.getColor()),
                Gender.valueOf( petDTO.getGender()),
                PetDescription.of(petDTO.getPetDescription()),
                PetImage.of(petDTO.getPetImage())
        );

    }

    @GetMapping("/{id}")
    public ResponseEntity<PetDTO> getPet(@PathVariable String petId) {
        UUID petIdConverted = UUID.fromString(petId);

        Pet pet = getPetUseCase.getPetdById(petIdConverted);

        return ResponseEntity.ok(petMapper.toDTO(pet));
    }

    @PatchMapping("/{petId}")
    public ResponseEntity<PetDTO> updatePet(
            @PathVariable String petId,
            @Valid @RequestBody UpdatePetRequest request) {

        String userId = getCurrentUserId();
        UUID UserIdConverted = UUID.fromString(userId);
        UUID petIdConverted = UUID.fromString(petId);
        Pet updated = updatePetUseCase.updatePet(petIdConverted, UserIdConverted, request);

        return ResponseEntity.ok(petMapper.toDTO(updated));
    }

    private String getCurrentUserId() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if(auth == null || !auth.isAuthenticated()){
            throw new UnauthorizedException("User not authenticated");
        }

        Object principal = auth.getPrincipal();

        if(principal instanceof UserDetailsAdapter){
            return ((UserDetailsAdapter)principal).getUser().getId().toString();
        }
        throw new UnauthorizedException("Invalid authentication principal");
    }

}
