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
    @Operation(summary = "Crear una nueva mascota", description = "Crea una nueva mascota para el usuario autenticado. Requiere autenticación con un token JWT válido.")
    @SecurityRequirement(name = "Bearer JWT")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Mascota creada exitosamente",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = PetResponse.class))),
            @ApiResponse(responseCode = "400", description = "Datos de mascota inválidos"),
            @ApiResponse(responseCode = "401", description = "No autorizado - Token JWT faltante o inválido")
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
    @Operation(summary = "Obtener mascota por ID", description = "Recupera una mascota específica por su identificador único.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Mascota recuperada exitosamente",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = PetDTO.class))),
            @ApiResponse(responseCode = "404", description = "Mascota no encontrada")
    })
    public ResponseEntity<PetDTO> getPet(
            @Parameter(description = "ID de la mascota (formato UUID)", required = true, example = "550e8400-e29b-41d4-a716-446655440000")
            @PathVariable("id") String petId) {
        UUID petIdConverted = UUID.fromString(petId);

        Pet pet = getPetUseCase.getPetById(petIdConverted);

        return ResponseEntity.ok(petMapper.toDTO(pet));
    }

    @PatchMapping("/{petId}")
    @Operation(summary = "Actualizar mascota", description = "Actualiza la información de una mascota existente. Requiere autenticación.")
    @SecurityRequirement(name = "Bearer JWT")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Mascota actualizada exitosamente",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = PetDTO.class))),
            @ApiResponse(responseCode = "400", description = "Datos de actualización inválidos"),
            @ApiResponse(responseCode = "401", description = "No autorizado - Token JWT faltante o inválido"),
            @ApiResponse(responseCode = "404", description = "Mascota no encontrada")
    })
    public ResponseEntity<PetDTO> updatePet(
            @Parameter(description = "ID de la mascota (formato UUID)", required = true, example = "550e8400-e29b-41d4-a716-446655440000")
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
    @Operation(summary = "Eliminar mascota", description = "Elimina una mascota por su identificador único. Requiere autenticación.")
    @SecurityRequirement(name = "Bearer JWT")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Mascota eliminada exitosamente"),
            @ApiResponse(responseCode = "401", description = "No autorizado - Token JWT faltante o inválido"),
            @ApiResponse(responseCode = "404", description = "Mascota no encontrada")
    })
    public ResponseEntity<Void> deletePet(
            @Parameter(description = "ID de la mascota (formato UUID)", required = true, example = "550e8400-e29b-41d4-a716-446655440000")
            @PathVariable String petId) {
        String userId = getCurrentUserId();  // Obtiene el usuario autenticado
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
    @Operation(summary = "Obtener mis mascotas", description = "Recupera todas las mascotas del usuario autenticado con opciones de filtrado y ordenamiento. Requiere autenticación.")
    @SecurityRequirement(name = "Bearer JWT")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista de mascotas recuperada exitosamente",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = PetDTO.class))),
            @ApiResponse(responseCode = "401", description = "No autorizado - Token JWT faltante o inválido")
    })
    public ResponseEntity<List<PetDTO>> getMyPets(
            @Parameter(description = "Filtrar por nombre de mascota")
            @RequestParam(required = false) String name,
            @Parameter(description = "Filtrar por raza")
            @RequestParam(required = false) String breed,
            @Parameter(description = "Filtrar por especie")
            @RequestParam(required = false) String species,
            @Parameter(description = "Filtrar por género")
            @RequestParam(required = false) String gender,
            @Parameter(description = "Filtrar por tamaño")
            @RequestParam(required = false) String size,
            @Parameter(description = "Campo para ordenar (por defecto: officialPetName)")
            @RequestParam(defaultValue = "officialPetName") String sortBy,
            @Parameter(description = "Dirección de ordenamiento (ASC o DESC)")
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
    @Operation(summary = "Validar imagen de mascota", description = "Valida una imagen de mascota para asegurar que contiene una mascota válida. Requiere autenticación.")
    @SecurityRequirement(name = "Bearer JWT")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Imagen validada exitosamente",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ImageValidationResponse.class))),
            @ApiResponse(responseCode = "400", description = "Imagen inválida o no proporcionada"),
            @ApiResponse(responseCode = "401", description = "No autorizado - Token JWT faltante o inválido")
    })
    public ResponseEntity<ImageValidationResponse> validateImage(
            @Parameter(description = "Archivo de imagen a validar", required = true)
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
    @Operation(summary = "Obtener todas las mascotas (Solo Admin)", description = "Recupera todas las mascotas del sistema. Este endpoint requiere rol ADMIN.")
    @SecurityRequirement(name = "Bearer JWT")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista de todas las mascotas recuperada exitosamente",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = PetDTO.class))),
            @ApiResponse(responseCode = "401", description = "No autorizado - Token JWT faltante o inválido"),
            @ApiResponse(responseCode = "403", description = "Prohibido - El usuario no tiene rol ADMIN")
    })
    public ResponseEntity<List<PetDTO>> getAllPets() {
        List<Pet> pets = getPetUseCase.getAllPets();
        return ResponseEntity.ok(petMapper.toDTOList(pets));
    }

    @DeleteMapping("/admin/{petId}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Eliminar mascota (Solo Admin)", description = "Elimina una mascota por su identificador. Este endpoint requiere rol ADMIN.")
    @SecurityRequirement(name = "Bearer JWT")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Mascota eliminada exitosamente"),
            @ApiResponse(responseCode = "401", description = "No autorizado - Token JWT faltante o inválido"),
            @ApiResponse(responseCode = "403", description = "Prohibido - El usuario no tiene rol ADMIN"),
            @ApiResponse(responseCode = "404", description = "Mascota no encontrada")
    })
    public ResponseEntity<Void> deletePetByAdmin(
            @Parameter(description = "ID de la mascota (formato UUID)", required = true, example = "550e8400-e29b-41d4-a716-446655440000")
            @PathVariable String petId) {
        UUID petIdConverted = UUID.fromString(petId);
        // Pass null for userId - admin can delete any pet
        deletePetUseCase.deletePetdById(petIdConverted, null);
        return ResponseEntity.noContent().build();
    }
}
