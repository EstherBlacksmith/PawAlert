package itacademy.pawalert.infrastructure.persistence.pet;
import itacademy.pawalert.domain.alert.model.UserId;
import itacademy.pawalert.domain.pet.model.*;
import jakarta.persistence.*;
import java.time.LocalDateTime;



@Entity
@Table(name = "pets")
public class PetEntity {
    @OneToMany(mappedBy = "alert_id", cascade = CascadeType.ALL)
    @Id
    private String id;
    @Column(name = "user_id")
    private String userId;
    @Column(name = "pet_id")
    private String petId;
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
    @Column(name = "pet_description")
    private String petDescription;
    @Column(name = "pet_image")
    private String petImage;
    @Column(name = "created_at")
    private LocalDateTime createdAt;

    public PetEntity() {
    }

    public Pet toDomain() {
        return new Pet(
                new UserId(this.userId),
                new PetId (this.petId),
                new ChipNumber (this.chipNumber),
                new PetName(this.officialPetName),
                new PetName (this.workingPetName),
                Species.valueOf(this.species),
                new Breed (this.breed),
                Size.valueOf(this.size),
                new  Color (this.color),
                new PetDescription (this.petDescription),
                new PetImage (this.petImage)
        );
    }
}
