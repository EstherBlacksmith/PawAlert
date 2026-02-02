package itacademy.pawalert.domain.pet.model;

import java.util.function.Consumer;

public record Pet(PetId petId, ChipNumber chipNumber, PetName oficialPetName, PetName workingPetName, Species species,
                  Breed breed, Size size, Color color, PetDescription petDescription, PetImage petImage) {

    public Pet with(Consumer<PetBuilder> builderConsumer) {
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

        public PetBuilder(Pet originalPet) {
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

        public Pet build() {
            return new Pet(petId, chipNumber, oficialPetName, workingPetName,
                    species, breed, size, color, petDescription, petImage);
        }

    }

}

