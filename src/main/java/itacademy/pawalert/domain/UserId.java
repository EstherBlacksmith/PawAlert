package itacademy.pawalert.domain;

import com.fasterxml.jackson.annotation.JsonCreator;
import jakarta.validation.constraints.NotBlank;

import java.util.UUID;

public record UserId(@NotBlank(message ="UserId cannot be empty" ) String value) {

    @JsonCreator  // Jackson will use this for deserialization
    public UserId {
    }

    // Factory method
    public static UserId fromUUID(UUID uuid) {
        return new UserId(uuid.toString());
    }

    public UUID toUUID() {
        return UUID.fromString(this.value);
    }

}