package itacademy.pawalert.infrastructure.rest.pet.dto;

public record PetResponse(
        String petId,
        String userId,
        String chipNumber,
        String workingPetName,
        String species,
        String breed,
        String size,
        String color,
        String gender,
        String petDescription,
        String petImage
) {}