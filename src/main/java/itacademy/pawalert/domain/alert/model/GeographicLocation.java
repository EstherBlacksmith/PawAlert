package itacademy.pawalert.domain.alert.model;
import itacademy.pawalert.domain.alert.exception.InvalidLatitudeException;
import itacademy.pawalert.domain.alert.exception.InvalidLongitudeException;
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

    private static void validateLatitude(double latitude) {
        if (latitude < MIN_LATITUDE || latitude > MAX_LATITUDE) {
            throw new InvalidLatitudeException(String.format("Latitude %.4f is invalid. Must be between -90.0 and 90.0", latitude));

        }
    }
    private static void validateLongitude(double longitude) {
        if (longitude < MIN_LONGITUDE || longitude > MAX_LONGITUDE) {
            throw new InvalidLongitudeException(String.format("Longitude %.4f is invalid. Must be between -180.0 and 180.0", longitude));
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
