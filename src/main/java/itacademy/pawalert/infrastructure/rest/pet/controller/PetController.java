package itacademy.pawalert.infrastructure.rest.pet.controller;

import itacademy.pawalert.application.pet.port.inbound.CreatePetUseCase;
import itacademy.pawalert.application.pet.port.inbound.DeletePetUseCase;
import itacademy.pawalert.application.pet.port.inbound.GetPetUseCase;
import itacademy.pawalert.application.pet.port.inbound.UpdatePetUseCase;
import itacademy.pawalert.domain.pet.model.*;
import itacademy.pawalert.infrastructure.rest.pet.dto.CreatePetRequest;
import itacademy.pawalert.infrastructure.rest.pet.dto.PetDTO;

import itacademy.pawalert.infrastructure.rest.pet.dto.PetResponse;
import itacademy.pawalert.infrastructure.rest.pet.dto.UpdatePetRequest;
import itacademy.pawalert.infrastructure.rest.pet.mapper.PetMapper;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
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
    private final DeletePetUseCase deletePetUseCase;

    private final PetMapper petMapper;

    public PetController(CreatePetUseCase createPetUseCase,
                         GetPetUseCase getPetUseCase,
                         UpdatePetUseCase updatePetUseCase, DeletePetUseCase deletePetUseCase,
                         PetMapper petMapper) {
        this.createPetUseCase = createPetUseCase;
        this.getPetUseCase = getPetUseCase;
        this.updatePetUseCase = updatePetUseCase;
        this.deletePetUseCase = deletePetUseCase;
        this.petMapper = petMapper;

    }

    @PostMapping
    public ResponseEntity<PetResponse> createPet(
            @Valid @RequestBody CreatePetRequest request) {

        Pet pet = createPetUseCase.createPet(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(petMapper.toResponse(pet));
    }


    @GetMapping("/{id}")
    public ResponseEntity<PetDTO> getPet(@PathVariable String petId) {
        UUID petIdConverted = UUID.fromString(petId);

        Pet pet = getPetUseCase.getPetById(petIdConverted);

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

    @DeleteMapping("/{petId}")
    public ResponseEntity<Void> deletePet(@PathVariable String petId) {
        String userId = getCurrentUserId();  // Obtiene el usuario autenticado
        deletePetUseCase.deletePetdById(
                UUID.fromString(petId),
                UUID.fromString(userId)
        );
        return ResponseEntity.noContent().build();
    }
}
