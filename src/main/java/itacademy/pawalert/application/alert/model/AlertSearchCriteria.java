package itacademy.pawalert.application.alert.model;

import itacademy.pawalert.domain.alert.model.StatusNames;

import java.time.LocalDateTime;
import java.util.UUID;

public record AlertSearchCriteria(
        StatusNames status,
        String title,
        String petName,
        String species,
        String breed,
        LocalDateTime createdFrom,
        LocalDateTime createdTo,
        LocalDateTime updatedFrom,
        LocalDateTime updatedTo,
        UUID userId
) {
    public static AlertSearchCriteria empty() {
        return new AlertSearchCriteria(null, null, null, null, null, null, null, null, null, null);
    }
}
