package itacademy.pawalert.domain.alert.model;

import com.fasterxml.jackson.annotation.JsonCreator;

public record GeographicLocation(
        double latitude,
        double longitude
) {

    // Constants for validation
    private static final double MIN_LATITUDE = -90.0;
    private static final double MAX_LATITUDE = 90.0;
    private static final double MIN_LONGITUDE = -180.0;
    private static final double MAX_LONGITUDE = 180.0;


    @JsonCreator  // Jackson use this for deserialization
    public GeographicLocation {
        validateLatitude(latitude);
        validateLongitude(longitude);
    }

    private static void validateLatitude(double lat) {
        if (lat < MIN_LATITUDE || lat > MAX_LATITUDE) {
            throw new IllegalArgumentException(
                    String.format("Latitude must be between %.1f and %.1f, but was %.4f",
                            MIN_LATITUDE, MAX_LATITUDE, lat)
            );
        }
    }

    private static void validateLongitude(double lon) {
        if (lon < MIN_LONGITUDE || lon > MAX_LONGITUDE) {
            throw new IllegalArgumentException(
                    String.format("Longitude must be between %.1f and %.1f, but was %.4f",
                            MIN_LONGITUDE, MAX_LONGITUDE, lon)
            );
        }
    }

    //Factory method
    public static GeographicLocation of(double latitude, double longitude) {
        return new GeographicLocation(latitude, longitude);
    }

    @Override
    public String toString() {
        return String.format("Location(%.6f, %.6f)", latitude, longitude);
    }
}
