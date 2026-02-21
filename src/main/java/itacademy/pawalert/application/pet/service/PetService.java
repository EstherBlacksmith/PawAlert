package itacademy.pawalert.application.pet.service;
import itacademy.pawalert.application.pet.port.inbound.*;
import itacademy.pawalert.domain.image.model.PetAnalysisResult;
import itacademy.pawalert.domain.image.port.inbound.PetImageAnalyzer;
import itacademy.pawalert.infrastructure.rest.pet.dto.ImageValidationResponse;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;

import itacademy.pawalert.application.exception.UnauthorizedException;
import itacademy.pawalert.application.pet.port.outbound.PetRepositoryPort;
import itacademy.pawalert.application.user.port.outbound.UserRepositoryPort;
import itacademy.pawalert.domain.pet.exception.PetNotFoundException;
import itacademy.pawalert.domain.pet.model.*;

import itacademy.pawalert.domain.user.Role;
import itacademy.pawalert.infrastructure.rest.pet.dto.CreatePetRequest;
import itacademy.pawalert.infrastructure.rest.pet.dto.UpdatePetRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import itacademy.pawalert.application.pet.port.inbound.ValidateImageUseCase;
import itacademy.pawalert.domain.image.model.PetAnalysisResult;
import itacademy.pawalert.domain.image.port.inbound.PetImageAnalyzer;
import itacademy.pawalert.infrastructure.rest.pet.dto.ImageValidationResponse;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.util.List;
import java.util.UUID;

@Service
public class PetService implements
        CreatePetUseCase,
        DeletePetUseCase,
        GetPetUseCase,
        UpdatePetUseCase,
        ValidateImageUseCase
{

    private final PetRepositoryPort petRepositoryPort;
    private final UserRepositoryPort userRepositoryPort;
    private final PetImageAnalyzer petImageAnalyzer;


    public PetService(PetRepositoryPort petRepositoryPort,
                      UserRepositoryPort userRepositoryPort,
                      PetImageAnalyzer petImageAnalyzer) {
        this.petRepositoryPort = petRepositoryPort;
        this.userRepositoryPort = userRepositoryPort;
        this.petImageAnalyzer = petImageAnalyzer;
    }


        @Override
        public Pet createPet(CreatePetRequest request) {
            UUID petId = UUID.randomUUID();

            Pet pet = Pet.builder()
                    .petId(petId)
                    .userId(UUID.fromString(request.userId()))
                    .chipNumber(new ChipNumber(request.chipNumber()))
                    .officialPetName(PetOfficialName.of(request.officialPetName()))
                    .workingPetName(PetWorkingName.of(request.workingPetName()))
                    .species(Species.valueOf(request.species()))
                    .breed(Breed.of(request.breed()))
                    .size(Size.valueOf(request.size()))
                    .color(Color.of(request.color()))
                    .gender(Gender.valueOf(request.gender()))
                    .petDescription(PetDescription.of(request.petDescription()))
                    .petImage(PetImage.of(request.petImage()))
                    .build();

            return petRepositoryPort.save(pet);
        }



    @Override
    public void deletePetdById(UUID petId,UUID userId) {
        Pet  pet = petRepositoryPort.findById(petId).orElseThrow(()->new PetNotFoundException("Pet not found"));

        Role userRole = getUserRole(userId);

        checkOwnership(pet, userId,userRole);

        petRepositoryPort.deleteById(petId,userId);
    }

    @Override
    public Pet updatePet(UUID petId, UUID userId, UpdatePetRequest request) {
        Pet existing = petRepositoryPort.findById(petId).orElseThrow(()->new PetNotFoundException("Pet not found"));

        Role userRole = getUserRole(userId);

        checkOwnership(existing, userId,userRole);

        Pet updatedPet = existing.with(builder -> {
            if (request.chipNumber() != null) {
                builder.chipNumber(ChipNumber.of(request.chipNumber()));
            }
            if (request.officialPetName() != null) {
                builder.officialPetName(PetOfficialName.of(request.officialPetName()));
            }
            if (request.workingPetName() != null) {
                builder.workingPetName(PetWorkingName.of(request.workingPetName()));
            }
            if (request.species() != null && !request.species().isBlank()) {
                builder.species(Species.fromString(request.species()));
            }
            if (request.breed() != null) {
                builder.breed(Breed.of(request.breed()));
            }
            if (request.size() != null && !request.size().isBlank()) {
                builder.size(Size.fromString(request.size()));
            }
            if (request.gender() != null && !request.gender().isBlank()) {
                builder.gender(Gender.fromString(request.gender()));
            }
            if (request.color() != null) {
                builder.color(Color.of(request.color()));
            }
            if (request.petDescription() != null) {
                builder.petDescription(PetDescription.of(request.petDescription()));
            }
            if (request.petImage() != null) {
                builder.petImage(PetImage.of(request.petImage()));
            }
        });

        return petRepositoryPort.save(updatedPet);
    }

    private void checkOwnership(Pet existingPet, UUID userId, Role userRole) {
        boolean isOwner = existingPet.getUserId().equals(userId);
        boolean isAdmin = userRole == Role.ADMIN;

        if (!isOwner && !isAdmin) {
            throw new UnauthorizedException("Only the owner or admin can modify the pet data");
        }
    }

    private Role getUserRole(UUID userId) {
        return  userRepositoryPort.getUserRol(userId);
    }

    @Override
    public Pet getPetById(UUID petId) {
        return petRepositoryPort.findById(petId).orElseThrow(()->new PetNotFoundException("Pet not found"));
    }

    @Override
    public List<Pet> getAllPetsByUserId(UUID userId) {
        return petRepositoryPort.findAllByUserId(userId);
    }

    @Override
    public List<Pet> searchPets(Specification<Pet> spec, Sort sort) {
        return petRepositoryPort.findAll(spec, sort);
    }

    @Override
    public Page<Pet> searchPets(Specification<Pet> spec, Pageable pageable) {
        return petRepositoryPort.findAll(spec, pageable);
    }

    @Override
    public List<Pet> getAllPets() {
        return petRepositoryPort.findAll();
    }

    @Override
    public ImageValidationResponse validateImage(MultipartFile file) {
        try {
            // Analyze the image
            PetAnalysisResult result = petImageAnalyzer.analyze(file.getBytes());

            // Validate the image
            if (!result.isValidPet()) {
                return ImageValidationResponse.invalid(result.validationMessage());
            }

            if (!result.isSafeForWork()) {
                return ImageValidationResponse.invalid("The image is inappropriate");
            }
            // Show the possible values for the fields
            return ImageValidationResponse.success(
                    result.species(),
                    result.speciesConfidence(),
                    result.breed(),
                    result.breedConfidence(),
                    result.possibleBreeds(),
                    result.dominantColor(),
                    result.dominantColorHex(),
                    result.visualLabels()
            );

        } catch (IOException e) {
            return ImageValidationResponse.invalid("Error processing the image: " + e.getMessage());
        }
    }

}

