package itacademy.pawalert.domain.pet.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import jakarta.validation.constraints.NotBlank;

import java.util.UUID;

public record PetId(@NotBlank(message = "PetId cannot be empty") String petId) {


    @JsonCreator  // Jackson will use this for deserialization
    public PetId {
    }

    // Factory method
    public static PetId fromUUID(UUID uuid) {
        return new PetId(uuid.toString());
    }

    public UUID toUUID() {
        return UUID.fromString(this.petId);
    }
}