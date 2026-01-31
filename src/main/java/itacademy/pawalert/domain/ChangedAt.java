package itacademy.pawalert.domain;

import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;
import java.util.Objects;

public record ChangedAt(@NotNull(message = "ChangedAt cannot be null") LocalDateTime value) {
       // Factory method opcional
    public static ChangedAt now() {
        return new ChangedAt(LocalDateTime.now());
    }
}