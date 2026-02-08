package itacademy.pawalert.infrastructure.rest.alert.dto;

import itacademy.pawalert.domain.alert.model.GeographicLocation;
import itacademy.pawalert.domain.alert.model.StatusNames;
import itacademy.pawalert.domain.alert.model.UserId;
import jakarta.validation.constraints.NotNull;

public record StatusChangeRequest(
        @NotNull(message = "New status is required") StatusNames newStatus,
        @NotNull(message = "User ID is required") UserId userId,
        @NotNull(message = "Latitude is required for location tracking") Double latitude,
        @NotNull(message = "Longitude is required for location tracking") Double longitude
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



