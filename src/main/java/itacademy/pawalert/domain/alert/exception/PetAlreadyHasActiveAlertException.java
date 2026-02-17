package itacademy.pawalert.domain.alert.exception;

public class PetAlreadyHasActiveAlertException extends RuntimeException {

    public PetAlreadyHasActiveAlertException(String petId) {
        super("Pet " + petId + " already has an active alert. " +
                "A pet can only have one open alert.");
    }

    public static PetAlreadyHasActiveAlertException forPet(String petId) {
        return new PetAlreadyHasActiveAlertException(petId);
    }
}