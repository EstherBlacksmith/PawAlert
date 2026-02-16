package itacademy.pawalert.infrastructure.rest.alert.dto;

import itacademy.pawalert.domain.alert.model.ClosureReason;
import itacademy.pawalert.domain.alert.model.GeographicLocation;
import itacademy.pawalert.domain.alert.model.UserId;
import jakarta.validation.constraints.NotNull;

public record CloseAlertRequest(
        @NotNull(message = "User ID is required") UserId userId,
        @NotNull(message = "Latitude is required") Double latitude,
        @NotNull(message = "Longitude is required") Double longitude,
        @NotNull(message = "Closure reason is required") ClosureReason closureReason
) {
    public String getUserId() {
        return userId.value();
    }

    public GeographicLocation getLocation() {
        if (latitude != null && longitude != null) {
            return GeographicLocation.of(latitude, longitude);
        }
        return null;
    }

    public ClosureReason getClosureReason() {
        return closureReason;
    }
}
