package itacademy.pawalert.infrastructure.rest.pet.dto;

public record CreatePetRequest(
        String userId,
        String chipNumber,
        String officialPetName,
        String workingPetName,
        String species,
        String breed,
        String size,
        String color,
        String gender,
        String petDescription,
        String petImage
) {
}