package itacademy.pawalert.domain;

import com.fasterxml.jackson.annotation.JsonCreator;

import java.util.Objects;
import java.util.UUID;

public record UserId(String value) {


    @JsonCreator  // Jackson will use this for deserialization
    public UserId {
        Objects.requireNonNull(value, "UserId cannot be null");
        if (value.isBlank()) {
            throw new IllegalArgumentException("UserId cannot be empty");
        }
    }

    // Factory method
    public static UserId fromUUID(UUID uuid) {
        return new UserId(uuid.toString());
    }

    public UUID toUUID() {
        return UUID.fromString(this.value);
    }

}