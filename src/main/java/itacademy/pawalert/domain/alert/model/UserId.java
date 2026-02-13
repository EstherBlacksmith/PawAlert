package itacademy.pawalert.domain.alert.model;


import java.util.UUID;

public record UserId( String value) {
    public UserId {
        if(value == null || value.isBlank()) {
            throw new IllegalArgumentException("The UserId cannot be empty");
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