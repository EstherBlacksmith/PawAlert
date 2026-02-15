package itacademy.pawalert.infrastructure.rest.pet.controller;

import itacademy.pawalert.application.pet.port.inbound.*;
import itacademy.pawalert.domain.pet.model.*;
import itacademy.pawalert.domain.pet.specification.PetSpecifications;
import itacademy.pawalert.infrastructure.rest.pet.dto.*;
import itacademy.pawalert.infrastructure.rest.pet.mapper.PetMapper;
import jakarta.validation.Valid;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import itacademy.pawalert.infrastructure.security.UserDetailsAdapter;
import itacademy.pawalert.application.exception.UnauthorizedException;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;


@RestController
@RequestMapping("/api/pets")
public class PetController {

    private final CreatePetUseCase createPetUseCase;
    private final GetPetUseCase getPetUseCase;
    private final UpdatePetUseCase updatePetUseCase;
    private final DeletePetUseCase deletePetUseCase;
    private final ValidateImageUseCase validateImageUseCase;
    private final PetMapper petMapper;

    public PetController(CreatePetUseCase createPetUseCase,
                         GetPetUseCase getPetUseCase,
                         UpdatePetUseCase updatePetUseCase,
                         DeletePetUseCase deletePetUseCase,
                         ValidateImageUseCase validateImageUseCase,
                         PetMapper petMapper) {
        this.createPetUseCase = createPetUseCase;
        this.getPetUseCase = getPetUseCase;
        this.updatePetUseCase = updatePetUseCase;
        this.deletePetUseCase = deletePetUseCase;
        this.validateImageUseCase = validateImageUseCase;
        this.petMapper = petMapper;

    }

    @PostMapping
    public ResponseEntity<PetResponse> createPet(
            @Valid @RequestBody CreatePetRequest request) {

        String userId = getCurrentUserId();
        
        // Create a new request with the authenticated user's ID
        CreatePetRequest requestWithUser = new CreatePetRequest(
                userId,
                request.chipNumber(),
                request.officialPetName(),
                request.workingPetName(),
                request.species(),
                request.breed(),
                request.size(),
                request.color(),
                request.gender(),
                request.petDescription(),
                request.petImage()
        );
        
        Pet pet = createPetUseCase.createPet(requestWithUser);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(petMapper.toResponse(pet));
    }


    @GetMapping("/{id}")
    public ResponseEntity<PetDTO> getPet(@PathVariable("id") String petId) {
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

    @GetMapping("/my-pets")
    public ResponseEntity<List<PetDTO>> getMyPets(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String breed,
            @RequestParam(required = false) String species,
            @RequestParam(required = false) String gender,
            @RequestParam(required = false) String size,
            @RequestParam(defaultValue = "officialPetName") String sortBy,
            @RequestParam(defaultValue = "ASC") String sortDirection
    ) {
        String userId = getCurrentUserId();
        Sort sort = Sort.by(Sort.Direction.fromString(sortDirection), sortBy);

        Specification<Pet> spec = PetSpecifications.byOwner(UUID.fromString(userId));

        if (name != null && !name.isBlank()) {
            spec = spec.and(PetSpecifications.nameContains(name));
        }

        if (breed != null && !breed.isBlank()) {
            spec = spec.and(PetSpecifications.breedContains(breed));
        }

        if (species != null && !species.isBlank()) {
            spec = spec.and(PetSpecifications.speciesEquals(species));
        }

        List<Pet> pets = getPetUseCase.searchPets(spec, sort);

        return ResponseEntity.ok(petMapper.toDTOList(pets));
    }

    @PostMapping("/validate-image")
    public ResponseEntity<ImageValidationResponse> validateImage(
            @RequestParam("file") MultipartFile file) {

        if (file.isEmpty()) {
            return ResponseEntity.badRequest()
                    .body(ImageValidationResponse.invalid("The user doesn't upload any image"));
        }

        ImageValidationResponse response = validateImageUseCase.validateImage(file);

        if (response.valid()) {
            return ResponseEntity.ok(response);
        } else {
            return ResponseEntity.badRequest().body(response);
        }
    }

}
