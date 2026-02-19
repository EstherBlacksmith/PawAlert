package itacademy.pawalert.infrastructure.rest.alert.dto;

import itacademy.pawalert.domain.alert.model.GeographicLocation;
import itacademy.pawalert.domain.alert.model.StatusNames;
import jakarta.validation.constraints.NotNull;

public record StatusChangeRequest(
        @NotNull(message = "New status is required") StatusNames newStatus,
        @NotNull(message = "User ID is required") String  userId,
        @NotNull(message = "Latitude is required") Double latitude,
        @NotNull(message = "Longitude is required") Double longitude
) {
    public StatusNames getNewStatus() {
        return newStatus;
    }

    public String getUserId() {
        return userId;
    }

    public GeographicLocation getLocation() {
        if (latitude != null && longitude != null) {
            return GeographicLocation.of(latitude, longitude);
        }
        return null; //When there is not GPS
    }

    public boolean hasGpsLocation() {
        return latitude != null && longitude != null;
    }
}



