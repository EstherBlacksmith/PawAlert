package itacademy.pawalert.domain.pet.model;

import itacademy.pawalert.domain.alert.model.UserId;

import itacademy.pawalert.infrastructure.persistence.pet.PetEntity;
import itacademy.pawalert.infrastructure.rest.pet.dto.UpdatePetRequest;
import lombok.Getter;

import java.util.UUID;
import java.util.function.Consumer;

@Getter
public class Pet {
    private final UUID userId;
    private final UUID petId;
    private final ChipNumber chipNumber;
    private final PetName officialPetName;
    private final PetName workingPetName;
    private final Species species;
    private final Breed breed;
    private final Size size;
    private final Color color;
    private final Gender gender;
    private final PetDescription petDescription;
    private final PetImage petImage;

    public Pet(UUID userId, UUID petId, ChipNumber chipNumber, PetName officialPetName, PetName workingPetName,
               Species species, Breed breed, Size size, Color color, Gender gender, PetDescription petDescription, PetImage petImage) {
        this.userId = userId;
        this.petId = petId;
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
    }


    private Pet(PetBuilder builder) {
        this.userId = builder.userId;
        this.petId = builder.petId;
        this.chipNumber = builder.chipNumber;
        this.officialPetName = builder.officialPetName;
        this.workingPetName = builder.workingPetName;
        this.species = builder.species;
        this.breed = builder.breed;
        this.size = builder.size;
        this.color = builder.color;
        this.petDescription = builder.petDescription;
        this.petImage = builder.petImage;
        this.gender = builder.gender;
    }

    public static PetBuilder builder() {
        return new PetBuilder();
    }


    public Pet with(Consumer<PetBuilder> updater) {
        PetBuilder newBuilder = new PetBuilder();

        newBuilder.userId = this.userId;           // Inmutable
        newBuilder.petId = this.petId;             // Inmutable
        newBuilder.chipNumber = this.chipNumber;
        newBuilder.officialPetName = this.officialPetName;
        newBuilder.workingPetName = this.workingPetName;
        newBuilder.species = this.species;
        newBuilder.breed = this.breed;
        newBuilder.size = this.size;
        newBuilder.gender = this.gender;
        newBuilder.color = this.color;
        newBuilder.petDescription = this.petDescription;
        newBuilder.petImage = this.petImage;

        updater.accept(newBuilder);

        return newBuilder.build();
    }

    public PetEntity toEntity() {
        return new PetEntity(
                this.userId.toString(),
                this.petId.toString(),
                this.chipNumber.value(),
                this.officialPetName.value(),
                this.workingPetName.value(),
                this.species.toString(),
                this.breed.value(),
                this.size.toString(),
                this.color.value(),
                this.gender.toString(),
                this.petDescription.value(),
                this.petImage.value()
        );
    }

    public static class PetBuilder {
        private UUID userId;
        private UUID petId;
        private ChipNumber chipNumber;
        private PetName officialPetName;
        private PetName workingPetName;
        private Species species;
        private Breed breed;
        private Size size;
        private Color color;
        private PetDescription petDescription;
        private PetImage petImage;
        private Gender gender;

        public PetBuilder userId(UUID userId) {
            this.userId = userId;
            return this;
        }

        public PetBuilder petId(UUID petId) {
            this.petId = petId;
            return this;
        }

        public PetBuilder chipNumber(ChipNumber chipNumber) {
            this.chipNumber = chipNumber;
            return this;
        }

        public PetBuilder officialPetName(PetName officialPetName) {
            this.officialPetName = officialPetName;
            return this;
        }

        public PetBuilder workingPetName(PetName workingPetName) {
            this.workingPetName = workingPetName;
            return this;
        }

        public PetBuilder species(Species species) {
            this.species = species;
            return this;
        }

        public PetBuilder breed(Breed breed) {
            this.breed = breed;
            return this;
        }

        public PetBuilder size(Size size) {
            this.size = size;
            return this;
        }

        public PetBuilder color(Color color) {
            this.color = color;
            return this;
        }

        public PetBuilder petDescription(PetDescription petDescription) {
            this.petDescription = petDescription;
            return this;
        }

        public PetBuilder petImage(PetImage petImage) {
            this.petImage = petImage;
            return this;
        }

        public PetBuilder gender(Gender gender) {
            this.gender = gender;
            return this;
        }

        public Pet build() {
            return new Pet(this);
        }

    }

    public Pet apply(UpdatePetRequest request) {
        return new Pet.PetBuilder()
                .userId(this.userId)
                .petId(this.petId)
                .chipNumber(request.hasChipNumber() ? new ChipNumber(request.chipNumber()) : this.chipNumber)
                .officialPetName(request.hasOfficialPetName() ? new PetName(request.officialPetName()) : this.officialPetName)
                .workingPetName(request.hasWorkingPetName() ? new PetName(request.workingPetName()) : this.workingPetName)
                .species(request.hasSpecies() ? Species.fromString(request.species()) : this.species)
                .breed(request.hasBreed() ? new Breed(request.breed()) : this.breed)
                .size(request.hasSize() ? Size.fromString(request.size()) : this.size)
                .gender(request.hasGender() ? Gender.fromString(request.gender()) : this.gender)
                .color(request.hasColor() ? new Color(request.color()) : this.color)
                .petDescription(request.hasPetDescription() ? new PetDescription(request.petDescription()) : this.petDescription)
                .petImage(request.hasPetImage() ? new PetImage(request.petImage()) :this.petImage)
                .build();
    }
}

