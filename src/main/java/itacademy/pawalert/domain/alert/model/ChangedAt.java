package itacademy.pawalert.domain.alert.model;

import java.time.LocalDateTime;

public record ChangedAt( LocalDateTime value) {
    public ChangedAt {
        if(value == null ) {
            throw new IllegalArgumentException("The UserId cannot be null");
        }
    }

    // Factory method opcional
    public static ChangedAt now() {
        return new ChangedAt(LocalDateTime.now());
    }
}