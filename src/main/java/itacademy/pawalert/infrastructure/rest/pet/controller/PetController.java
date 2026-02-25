package itacademy.pawalert.infrastructure.rest.pet.controller;

import itacademy.pawalert.application.exception.UnauthorizedException;
import itacademy.pawalert.application.pet.port.inbound.*;
import itacademy.pawalert.domain.pet.model.Pet;
import itacademy.pawalert.domain.pet.specification.PetSpecifications;
import itacademy.pawalert.infrastructure.rest.pet.dto.*;
import itacademy.pawalert.infrastructure.rest.pet.mapper.PetMapper;
import itacademy.pawalert.infrastructure.security.UserDetailsAdapter;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;


@RestController
@RequestMapping("/api/pets")
@Tag(name = "Pets", description = "Pet management endpoints for creating, retrieving, and updating pet information")
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
    @Operation(summary = "Create a new pet", description = "Creates a new pet for the authenticated user. Requires authentication with a valid JWT token.")
    @SecurityRequirement(name = "Bearer JWT")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Pet created successfully",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = PetResponse.class))),
            @ApiResponse(responseCode = "400", description = "Invalid pet data"),
            @ApiResponse(responseCode = "401", description = "Unauthorized - JWT token missing or invalid")
    })
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
    @Operation(summary = "Get pet by ID", description = "Retrieves a specific pet by its unique identifier.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Pet retrieved successfully",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = PetDTO.class))),
            @ApiResponse(responseCode = "404", description = "Pet not found")
    })
    public ResponseEntity<PetDTO> getPet(
            @Parameter(description = "Pet ID (UUID format)", required = true, example = "550e8400-e29b-41d4-a716-446655440000")
            @PathVariable("id") String petId) {
        UUID petIdConverted = UUID.fromString(petId);

        Pet pet = getPetUseCase.getPetById(petIdConverted);

        return ResponseEntity.ok(petMapper.toDTO(pet));
    }

    @PatchMapping("/{petId}")
    @Operation(summary = "Update pet", description = "Updates information for an existing pet. Requires authentication.")
    @SecurityRequirement(name = "Bearer JWT")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Pet updated successfully",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = PetDTO.class))),
            @ApiResponse(responseCode = "400", description = "Invalid update data"),
            @ApiResponse(responseCode = "401", description = "Unauthorized - JWT token missing or invalid"),
            @ApiResponse(responseCode = "404", description = "Pet not found")
    })
    public ResponseEntity<PetDTO> updatePet(
            @Parameter(description = "Pet ID (UUID format)", required = true, example = "550e8400-e29b-41d4-a716-446655440000")
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
        if (auth == null || !auth.isAuthenticated()) {
            throw new UnauthorizedException("User not authenticated");
        }

        Object principal = auth.getPrincipal();

        if (principal instanceof UserDetailsAdapter) {
            return ((UserDetailsAdapter) principal).getUser().id().toString();
        }
        throw new UnauthorizedException("Invalid authentication principal");
    }

    @DeleteMapping("/{petId}")
    @Operation(summary = "Delete pet", description = "Deletes a pet by its unique identifier. Requires authentication.")
    @SecurityRequirement(name = "Bearer JWT")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Pet deleted successfully"),
            @ApiResponse(responseCode = "401", description = "Unauthorized - JWT token missing or invalid"),
            @ApiResponse(responseCode = "404", description = "Pet not found")
    })
    public ResponseEntity<Void> deletePet(
            @Parameter(description = "Pet ID (UUID format)", required = true, example = "550e8400-e29b-41d4-a716-446655440000")
            @PathVariable String petId) {
        String userId = getCurrentUserId();  // Gets the authenticated user
        deletePetUseCase.deletePetdById(
                UUID.fromString(petId),
                UUID.fromString(userId)
        );
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/public/all")
    @Operation(summary = "Get all public pets",
            description = "Retrieves all pets in the system. Public endpoint accessible without authentication.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "List of pets retrieved successfully",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = PetDTO.class)))
    })
    public ResponseEntity<List<PetDTO>> getAllPublicPets() {
        List<Pet> pets = getPetUseCase.getAllPets();
        return ResponseEntity.ok(petMapper.toDTOList(pets));
    }

    @GetMapping("/all")
    @Operation(summary = "Get all pets", description = "Retrieves all pets in the system. Requires authentication.")
    @SecurityRequirement(name = "Bearer JWT")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "List of pets retrieved successfully",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = PetDTO.class))),
            @ApiResponse(responseCode = "401", description = "Unauthorized - JWT token missing or invalid")
    })
    public ResponseEntity<List<PetDTO>> getAllPetsAuthenticated() {
        // Verify user is authenticated
        getCurrentUserId();
        List<Pet> pets = getPetUseCase.getAllPets();
        return ResponseEntity.ok(petMapper.toDTOList(pets));
    }

    @GetMapping("/my-pets")
    @Operation(summary = "Get my pets", description = "Retrieves all pets for the authenticated user with filtering and sorting options. Requires authentication.")
    @SecurityRequirement(name = "Bearer JWT")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "List of pets retrieved successfully",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = PetDTO.class))),
            @ApiResponse(responseCode = "401", description = "Unauthorized - JWT token missing or invalid")
    })
    public ResponseEntity<List<PetDTO>> getMyPets(
            @Parameter(description = "Filter by pet name")
            @RequestParam(required = false) String name,
            @Parameter(description = "Filter by breed")
            @RequestParam(required = false) String breed,
            @Parameter(description = "Filter by species")
            @RequestParam(required = false) String species,
            @Parameter(description = "Filter by gender")
            @RequestParam(required = false) String gender,
            @Parameter(description = "Filter by size")
            @RequestParam(required = false) String size,
            @Parameter(description = "Field to sort by (default: officialPetName)")
            @RequestParam(defaultValue = "officialPetName") String sortBy,
            @Parameter(description = "Sort direction (ASC or DESC)")
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
    @Operation(summary = "Validate pet image", description = "Validates a pet image to ensure it contains a valid pet. Requires authentication.")
    @SecurityRequirement(name = "Bearer JWT")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Image validated successfully",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ImageValidationResponse.class))),
            @ApiResponse(responseCode = "400", description = "Invalid image or no image provided"),
            @ApiResponse(responseCode = "401", description = "Unauthorized - JWT token missing or invalid")
    })
    public ResponseEntity<ImageValidationResponse> validateImage(
            @Parameter(description = "Image file to validate", required = true)
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
// ========== ADMIN ENDPOINTS ==========

    @GetMapping("/admin/all")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Get all pets (Admin Only)", description = "Retrieves all pets in the system. This endpoint requires ADMIN role.")
    @SecurityRequirement(name = "Bearer JWT")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "List of all pets retrieved successfully",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = PetDTO.class))),
            @ApiResponse(responseCode = "401", description = "Unauthorized - JWT token missing or invalid"),
            @ApiResponse(responseCode = "403", description = "Forbidden - User does not have ADMIN role")
    })
    public ResponseEntity<List<PetDTO>> getAllPets() {
        List<Pet> pets = getPetUseCase.getAllPets();
        return ResponseEntity.ok(petMapper.toDTOList(pets));
    }

    @DeleteMapping("/admin/{petId}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Delete pet (Admin Only)", description = "Deletes a pet by its identifier. This endpoint requires ADMIN role.")
    @SecurityRequirement(name = "Bearer JWT")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Pet deleted successfully"),
            @ApiResponse(responseCode = "401", description = "Unauthorized - JWT token missing or invalid"),
            @ApiResponse(responseCode = "403", description = "Forbidden - User does not have ADMIN role"),
            @ApiResponse(responseCode = "404", description = "Pet not found")
    })
    public ResponseEntity<Void> deletePetByAdmin(
            @Parameter(description = "Pet ID (UUID format)", required = true, example = "550e8400-e29b-41d4-a716-446655440000")
            @PathVariable String petId) {
        UUID petIdConverted = UUID.fromString(petId);
        // Pass null for userId - admin can delete any pet
        deletePetUseCase.deletePetdById(petIdConverted, null);
        return ResponseEntity.noContent().build();
    }
}
