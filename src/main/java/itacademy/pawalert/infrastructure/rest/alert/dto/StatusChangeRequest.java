package itacademy.pawalert.infrastructure.rest.alert.dto;

import itacademy.pawalert.domain.alert.model.GeographicLocation;
import itacademy.pawalert.domain.alert.model.StatusNames;
import itacademy.pawalert.domain.alert.model.UserId;
import jakarta.validation.constraints.NotNull;

public record StatusChangeRequest(
        @NotNull StatusNames newStatus,
        @NotNull UserId userId,
        @NotNull Double latitude,
        @NotNull Double longitude
) {
    public StatusNames getNewStatus() {
        return newStatus;
    }

    public String getUserId() {
        return userId.value();
    }

    public GeographicLocation getLocation() {
        return GeographicLocation.of(latitude, longitude);
    }
}



