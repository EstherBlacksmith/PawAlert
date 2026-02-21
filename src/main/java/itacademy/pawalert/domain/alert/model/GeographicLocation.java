package itacademy.pawalert.domain.alert.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import itacademy.pawalert.domain.alert.exception.InvalidLatitudeException;
import itacademy.pawalert.domain.alert.exception.InvalidLongitudeException;

public record GeographicLocation(
        double latitude,
        double longitude
) {

    // Constants for validation
    private static final double MIN_LATITUDE = -90.0;
    private static final double MAX_LATITUDE = 90.0;
    private static final double MIN_LONGITUDE = -180.0;
    private static final double MAX_LONGITUDE = 180.0;
    private static final double EARTH_RADIUS_KM = 6371.0;

    @JsonCreator  // Jackson use this for deserialization
    public GeographicLocation {
        validateLatitude(latitude);
        validateLongitude(longitude);
    }

    private static void validateLatitude(double latitude) {
        if (Double.isNaN(latitude) || latitude < MIN_LATITUDE || latitude > MAX_LATITUDE) {
            throw new InvalidLatitudeException("Invalid latitude: " + latitude);
        }
    }

    private static void validateLongitude(double longitude) {
        if (Double.isNaN(longitude) || longitude < MIN_LONGITUDE || longitude > MAX_LONGITUDE) {
            throw new InvalidLongitudeException("Invalid longitude: " + longitude);
        }
    }

    //Factory method
    public static GeographicLocation of(double latitude, double longitude) {
        return new GeographicLocation(latitude, longitude);
    }

    // Haversine formula
    public double distanceTo(GeographicLocation other) {
        double lat1Rad = Math.toRadians(this.latitude);
        double lat2Rad = Math.toRadians(other.latitude);
        double deltaLat = Math.toRadians(other.latitude - this.latitude);
        double deltaLon = Math.toRadians(other.longitude - this.longitude);

        double a = Math.pow(Math.sin(deltaLat / 2), 2) +
                Math.cos(lat1Rad) * Math.cos(lat2Rad) *
                        Math.pow(Math.sin(deltaLon / 2), 2);

        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        return EARTH_RADIUS_KM * c;
    }

    public boolean isWithinRadius(GeographicLocation center, double radiusKm) {
        return distanceTo(center) <= radiusKm;
    }

    @Override
    public String toString() {
        return String.format("Location(%.6f, %.6f)", latitude, longitude);
    }
}
