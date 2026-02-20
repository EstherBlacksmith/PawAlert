package itacademy.pawalert.application.alert.model;

import itacademy.pawalert.domain.alert.model.StatusNames;
import java.time.LocalDateTime;

public record AlertSearchCriteria(
        StatusNames status,
        String title,
        String petName,
        String species,
        LocalDateTime createdFrom,
        LocalDateTime createdTo,
        LocalDateTime updatedFrom,
        LocalDateTime updatedTo
) {
    public static AlertSearchCriteria empty() {
        return new AlertSearchCriteria(null, null,null, null, null, null, null, null);
    }
}
