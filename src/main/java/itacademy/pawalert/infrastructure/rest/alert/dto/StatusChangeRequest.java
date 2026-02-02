package itacademy.pawalert.infrastructure.rest.alert.dto;

import itacademy.pawalert.domain.alert.model.StatusNames;
import itacademy.pawalert.domain.alert.model.UserId;
import jakarta.validation.constraints.NotNull;

public record StatusChangeRequest(
        @NotNull StatusNames newStatus,
        @NotNull UserId userId
) {
    public StatusNames getNewStatus() {
        return newStatus;
    }

    public String getUserId() {
        return userId.value();
    }
}



