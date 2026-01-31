package itacademy.pawalert.infrastructure.rest.dto;

import itacademy.pawalert.domain.StatusNames;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record StatusChangeRequest(@NotNull StatusNames newStatus,@NotBlank String userId) {
    public StatusNames getNewStatus() {
        return newStatus;
    }

    public String getUserId() {
        return userId;
    }
}




