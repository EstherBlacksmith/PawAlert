package itacademy.pawalert.domain.pet.model;

import lombok.Getter;
import java.util.UUID;
import java.util.function.Consumer;

@Getter
public class Pet {

    private final PetId petId;
    private final ChipNumber chipNumber;
    private final PetName oficialPetName;
    private final PetName workingPetName;
    private final Species species;
    private final Breed breed;
    private final Size size;
    private final Color color;
    private final PetDescription petDescription;
    private final PetImage petImage;

    public Pet(PetId petId, ChipNumber chipNumber, PetName oficialPetName, PetName workingPetName, Species species,
               Breed breed, Size size, Color color, PetDescription petDescription, PetImage petImage) {
        this.petId = petId;
        this.chipNumber = chipNumber;
        this.oficialPetName = oficialPetName;
        this.workingPetName = workingPetName;
        this.species = species;
        this.breed = breed;
        this.size = size;
        this.color = color;
        this.petDescription = petDescription;
        this.petImage = petImage;
    }

    public Pet with (Consumer<PetBuilder> builderConsumer){
        PetBuilder petBuilder = new PetBuilder(this);
        builderConsumer.accept(petBuilder);
        return petBuilder.build();
    }

    private static class PetBuilder {
        private final PetId petId;
        private final ChipNumber chipNumber;
        private PetName oficialPetName;
        private PetName workingPetName;
        private Species species;
        private Breed breed;
        private Size size;
        private Color color;
        private PetDescription petDescription;
        private PetImage petImage;

        public PetBuilder(Pet originalPet){
            this.petId = originalPet.petId;
            this.chipNumber = originalPet.chipNumber;
            this.oficialPetName = originalPet.oficialPetName;
            this.workingPetName = originalPet.workingPetName;
            this.species = originalPet.species;
            this.breed = originalPet.breed;
            this.size = originalPet.size;
            this.color = originalPet.color;
            this.petDescription = originalPet.petDescription;
            this.petImage = originalPet.petImage;
        }

        public PetBuilder officialPetName(PetName oficialPetName) {
            this.oficialPetName = oficialPetName;
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

        public Pet build(){
            return new Pet( petId, chipNumber, oficialPetName, workingPetName,
                    species, breed, size, color, petDescription, petImage);
        }

    }

}

