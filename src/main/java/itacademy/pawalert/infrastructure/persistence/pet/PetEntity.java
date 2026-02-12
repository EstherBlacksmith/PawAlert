package itacademy.pawalert.infrastructure.persistence.pet;
import itacademy.pawalert.domain.pet.model.*;
import jakarta.persistence.*;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.UUID;


@Getter
@Entity
@Table(name = "pets")
public class PetEntity {
    @Id
    @Column(name = "id")
    private String id;
    @Column(name = "user_id")
    private String userId;
    @Column(name = "pet_chip_number")
    private String chipNumber;
    @Column(name = "pet_offical_name")
    private String officialPetName;
    @Column(name = "pet_working_name")
    private String workingPetName;
    @Column(name = "pet_species")
    private String species;
    @Column(name = "pet_breed")
    private String breed;
    @Column(name = "pet_size")
    private String size;
    @Column(name = "pet_color")
    private String color;
    @Column(name = "pet_gender")
    private String gender;
    @Column(name = "pet_description")
    private String petDescription;
    @Column(name = "pet_image")
    private String petImage;
    @Column(name = "created_at")
    private LocalDateTime createdAt;

    public PetEntity() {
    }

    public PetEntity(String id, String userId, String chipNumber,
                     String officialPetName, String workingPetName, String species,
                     String breed, String size, String color, String gender, String petDescription,
                     String petImage) {
        this.id = id;
        this.userId = userId;
        this.chipNumber = chipNumber;
        this.officialPetName = officialPetName;
        this.workingPetName = workingPetName;
        this.species = species;
        this.breed = breed;
        this.size = size;
        this.color = color;
        this.gender = gender;
        this.petDescription = petDescription;
        this.petImage = petImage;
        this.createdAt = LocalDateTime.now();
    }


    public Pet toDomain() {
        return new Pet(
                UUID.fromString(this.userId),
                UUID.fromString(this.id),
                new ChipNumber (this.chipNumber),
                new PetWorkingName(this.officialPetName),
                new PetWorkingName(this.workingPetName),
                Species.valueOf(this.species),
                new Breed (this.breed),
                Size.valueOf(this.size),
                new Color (this.color),
                Gender.valueOf(this.gender),
                new PetDescription (this.petDescription),
                new PetImage (this.petImage)
        );
    }
}
