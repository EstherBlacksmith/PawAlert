package itacademy.pawalert.application.pet.service;


import itacademy.pawalert.application.exception.UnauthorizedException;
import itacademy.pawalert.application.pet.port.inbound.CreatePetUseCase;
import itacademy.pawalert.application.pet.port.inbound.DeletePetUseCase;
import itacademy.pawalert.application.pet.port.inbound.GetPetUseCase;
import itacademy.pawalert.application.pet.port.inbound.UpdatePetUseCase;
import itacademy.pawalert.application.pet.port.outbound.PetRepositoryPort;
import itacademy.pawalert.application.user.port.outbound.UserRepositoryPort;
import itacademy.pawalert.domain.pet.exception.PetNotFoundException;
import itacademy.pawalert.domain.pet.model.*;

import itacademy.pawalert.domain.user.Role;
import itacademy.pawalert.infrastructure.rest.pet.dto.CreatePetRequest;
import itacademy.pawalert.infrastructure.rest.pet.dto.UpdatePetRequest;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class PetService implements
        CreatePetUseCase,
        DeletePetUseCase,
        GetPetUseCase,
        UpdatePetUseCase
{

    private final PetRepositoryPort petRepositoryPort;
    private final UserRepositoryPort userRepositoryPort;


    public PetService(PetRepositoryPort petRepositoryPort, UserRepositoryPort userRepositoryPort) {
        this.petRepositoryPort = petRepositoryPort;
        this.userRepositoryPort = userRepositoryPort;
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

            return petRepositoryPort.save(pet).toDomain();
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
                builder.chipNumber(new ChipNumber(request.chipNumber()));
            }
            if (request.officialPetName() != null) {
                builder.officialPetName(new PetOfficialName(request.officialPetName()));
            }
            if (request.workingPetName() != null) {
                builder.workingPetName(new PetWorkingName(request.workingPetName()));
            }
            if (request.species() != null) {
                builder.species(Species.fromString(request.species()));
            }
            if (request.breed() != null) {
                builder.breed(new Breed(request.breed()));
            }
            if (request.size() != null) {
                builder.size(Size.fromString(request.size()));
            }
            if (request.gender() != null) {
                builder.gender(Gender.fromString(request.gender()));
            }
            if (request.color() != null) {
                builder.color(new Color(request.color()));
            }
            if (request.petDescription() != null) {
                builder.petDescription(new PetDescription(request.petDescription()));
            }
        });

        return petRepositoryPort.save(updatedPet).toDomain();
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
    public Pet getPetdById(UUID petId) {
        return petRepositoryPort.findById(petId).orElseThrow(()->new PetNotFoundException("Pet not found"));
    }
}

