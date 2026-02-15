package itacademy.pawalert.domain.alert.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import java.util.UUID;

public record UserId(String value) {
    
    public UserId {
        if(value == null || value.isBlank()) {
            throw new IllegalArgumentException("The UserId cannot be empty");
        }
    }

    @JsonCreator
    public static UserId of(String value) {
        return new UserId(value);
    }

    @JsonValue
    public String getValue() {
        return value;
    }

    public static UserId fromUUID(UUID uuid) {
        return new UserId(uuid.toString());
    }

    public UUID toUUID() {
        return UUID.fromString(this.value);
    }
}