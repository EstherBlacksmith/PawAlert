package itacademy.pawalert.domain.pet.model;

import itacademy.pawalert.domain.alert.model.UserId;

import itacademy.pawalert.infrastructure.persistence.pet.PetEntity;
import lombok.Getter;
import java.util.function.Consumer;

@Getter
public class Pet {
    private final UserId userId;
    private final PetId petId;
    private final ChipNumber chipNumber;
    private final PetName officialPetName;
    private final PetName workingPetName;
    private final Species species;
    private final Breed breed;
    private final Size size;
    private final Color color;
    private final PetDescription petDescription;
    private final PetImage petImage;

    public Pet(UserId userId, PetId petId, ChipNumber chipNumber, PetName officialPetName, PetName workingPetName,
               Species species, Breed breed, Size size, Color color, PetDescription petDescription, PetImage petImage) {
        this.userId = userId;
        this.petId = petId;
        this.chipNumber = chipNumber;
        this.officialPetName = officialPetName;
        this.workingPetName = workingPetName;
        this.species = species;
        this.breed = breed;
        this.size = size;
        this.color = color;
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
        newBuilder.color = this.color;
        newBuilder.petDescription = this.petDescription;
        newBuilder.petImage = this.petImage;

        updater.accept(newBuilder);

        return newBuilder.build();
    }


    //TODO y arreglar el pet mapper y el petbuilder
    public PetEntity toEntity() {
        return new PetEntity(userId, petId, chipNumber);
    }




    private static class PetBuilder {
        private UserId userId;
        private PetId petId;
        private ChipNumber chipNumber;
        private PetName officialPetName;
        private PetName workingPetName;
        private Species species;
        private Breed breed;
        private Size size;
        private Color color;
        private PetDescription petDescription;
        private PetImage petImage;

        public PetBuilder userId(UserId userId) {
            this.userId = userId;
            return this;
        }

        public PetBuilder petId(PetId petId) {
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
        public Pet build() {
            return new Pet(this);
        }
    }
}

