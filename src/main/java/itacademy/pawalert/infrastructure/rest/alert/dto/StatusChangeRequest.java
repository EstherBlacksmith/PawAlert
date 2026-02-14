package itacademy.pawalert.infrastructure.rest.alert.dto;

import itacademy.pawalert.domain.alert.model.GeographicLocation;
import itacademy.pawalert.domain.alert.model.StatusNames;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record StatusChangeRequest(
        @NotNull(message = "New status is required") StatusNames newStatus,
        @NotNull(message = "User ID is required") UUID userId,
        Double latitude,
        Double longitude
) {
    public StatusNames getNewStatus() {
        return newStatus;
    }

    public String getUserId() {
        return userId.toString();
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

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }
}



