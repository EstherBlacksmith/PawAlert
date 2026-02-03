package itacademy.pawalert.infrastructure.rest.pet.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PetDTO {

    private String userId;
    private String petId;
    private String chipNumber;
    private String officialPetName;
    private String workingPetName;
    private String species;
    private String breed;
    private String size;
    private String color;
    private String gender;
    private String petDescription;
    private String petImage;

}

