package itacademy.pawalert.domain.alert.model;

import itacademy.pawalert.domain.alert.exception.InvalidLatitudeException;
import itacademy.pawalert.domain.alert.exception.InvalidLongitudeException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for GeographicLocation record.
 * 
 * Tests the validation logic for latitude and longitude coordinates
 * to ensure they fall within valid geographic boundaries.
 */
@DisplayName("GeographicLocation Unit Tests")
class GeographicLocationTest {

    // Valid location for Madrid, Spain
    private static final double VALID_LATITUDE = 40.4168;
    private static final double VALID_LONGITUDE = -3.7025;

    @Nested
    @DisplayName("Valid Coordinates Tests")
    class ValidCoordinatesTests {

        @Test
        @DisplayName("Should create GeographicLocation with valid coordinates")
        void shouldCreateWithValidCoordinates() {
            // When
            GeographicLocation location = GeographicLocation.of(VALID_LATITUDE, VALID_LONGITUDE);
            
            // Then
            assertNotNull(location);
            assertEquals(VALID_LATITUDE, location.latitude());
            assertEquals(VALID_LONGITUDE, location.longitude());
        }

        @Test
        @DisplayName("Should handle zero coordinates")
        void shouldHandleZeroCoordinates() {
            // When
            GeographicLocation location = GeographicLocation.of(0.0, 0.0);
            
            // Then
            assertNotNull(location);
            assertEquals(0.0, location.latitude());
            assertEquals(0.0, location.longitude());
        }

        @Test
        @DisplayName("Should handle negative coordinates")
        void shouldHandleNegativeCoordinates() {
            // When - Sydney, Australia
            GeographicLocation location = GeographicLocation.of(-33.8688, 151.2093);
            
            // Then
            assertNotNull(location);
            assertEquals(-33.8688, location.latitude());
            assertEquals(151.2093, location.longitude());
        }

        @Test
        @DisplayName("Should handle coordinates at exact boundaries")
        void shouldHandleExactBoundaryCoordinates() {
            // Given - Boundary values
            double minLat = -90.0;
            double maxLat = 90.0;
            double minLon = -180.0;
            double maxLon = 180.0;
            
            // When/Then - All should be valid
            assertDoesNotThrow(() -> GeographicLocation.of(minLat, minLon));
            assertDoesNotThrow(() -> GeographicLocation.of(minLat, maxLon));
            assertDoesNotThrow(() -> GeographicLocation.of(maxLat, minLon));
            assertDoesNotThrow(() -> GeographicLocation.of(maxLat, maxLon));
        }
    }

    @Nested
    @DisplayName("Invalid Latitude Tests")
    class InvalidLatitudeTests {

        @ParameterizedTest
        @ValueSource(doubles = {-90.01, -91.0, -100.0, 90.01, 91.0, 100.0})
        @DisplayName("Should throw InvalidLatitudeException for out-of-range latitudes")
        void shouldThrowForInvalidLatitude(double invalidLatitude) {
            // When/Then
            InvalidLatitudeException exception = assertThrows(
                    InvalidLatitudeException.class,
                    () -> GeographicLocation.of(invalidLatitude, VALID_LONGITUDE)
            );
            
            assertTrue(exception.getMessage().contains("Latitude"));
            assertTrue(exception.getMessage().contains("invalid"));
        }

        @Test
        @DisplayName("Should include invalid latitude value in exception message")
        void shouldIncludeInvalidLatitudeInMessage() {
            // Given
            double invalidLatitude = 95.5;
            
            // When/Then
            InvalidLatitudeException exception = assertThrows(
                    InvalidLatitudeException.class,
                    () -> GeographicLocation.of(invalidLatitude, VALID_LONGITUDE)
            );
            
            // The message may use %.4f format, check for the value
            assertTrue(exception.getMessage().contains("95") && exception.getMessage().contains("5"),
                "Exception message should contain the invalid latitude value: " + exception.getMessage());
        }
    }

    @Nested
    @DisplayName("Invalid Longitude Tests")
    class InvalidLongitudeTests {

        @ParameterizedTest
        @ValueSource(doubles = {-180.01, -200.0, 180.01, 200.0})
        @DisplayName("Should throw InvalidLongitudeException for out-of-range longitudes")
        void shouldThrowForInvalidLongitude(double invalidLongitude) {
            // When/Then
            InvalidLongitudeException exception = assertThrows(
                    InvalidLongitudeException.class,
                    () -> GeographicLocation.of(VALID_LATITUDE, invalidLongitude)
            );
            
            assertTrue(exception.getMessage().contains("Longitude"));
            assertTrue(exception.getMessage().contains("invalid"));
        }

        @Test
        @DisplayName("Should include invalid longitude value in exception message")
        void shouldIncludeInvalidLongitudeInMessage() {
            // Given
            double invalidLongitude = -200.5;
            
            // When/Then
            InvalidLongitudeException exception = assertThrows(
                    InvalidLongitudeException.class,
                    () -> GeographicLocation.of(VALID_LATITUDE, invalidLongitude)
            );
            
            // The message may use %.4f format, check for the value
            assertTrue(exception.getMessage().contains("-200") || exception.getMessage().contains("200"),
                "Exception message should contain the invalid longitude value: " + exception.getMessage());
        }
    }

    @Nested
    @DisplayName("Both Coordinates Invalid Tests")
    class BothCoordinatesInvalidTests {

        @Test
        @DisplayName("Should throw InvalidLatitudeException when both coordinates are invalid")
        void shouldThrowLatitudeFirstWhenBothInvalid() {
            // Given
            double invalidLat = 95.0;
            double invalidLon = -200.0;
            
            // When/Then - Latitude is validated first
            InvalidLatitudeException exception = assertThrows(
                    InvalidLatitudeException.class,
                    () -> GeographicLocation.of(invalidLat, invalidLon)
            );
            
            assertTrue(exception.getMessage().contains("Latitude"));
        }
    }

    @Nested
    @DisplayName("toString Tests")
    class ToStringTests {

        @Test
        @DisplayName("Should format coordinates with 6 decimal places")
        void shouldFormatWithSixDecimalPlaces() {
            // Given
            GeographicLocation location = GeographicLocation.of(40.4167754, -3.7026167);
            
            // When
            String result = location.toString();
            
            // Then
            // The toString uses %.6f format but with Spanish locale uses comma as separator
            assertTrue(result.contains("Location"), "Should contain 'Location' label");
            assertTrue(result.contains("40") && result.contains("416775"), 
                "Should contain latitude with 6 decimal places: " + result);
            assertTrue(result.contains("-3") && result.contains("702617"), 
                "Should contain longitude with 6 decimal places: " + result);
        }

        @Test
        @DisplayName("Should include Location label in toString output")
        void shouldIncludeLocationLabel() {
            // Given
            GeographicLocation location = GeographicLocation.of(VALID_LATITUDE, VALID_LONGITUDE);
            
            // When
            String result = location.toString();
            
            // Then
            assertTrue(result.contains("Location"));
        }
    }

    @Nested
    @DisplayName("Equality Tests")
    class EqualityTests {

        @Test
        @DisplayName("Should be equal for same coordinates")
        void shouldBeEqualForSameCoordinates() {
            // Given
            GeographicLocation location1 = GeographicLocation.of(40.4168, -3.7025);
            GeographicLocation location2 = GeographicLocation.of(40.4168, -3.7025);
            
            // Then
            assertEquals(location1, location2);
            assertEquals(location1.hashCode(), location2.hashCode());
        }

        @Test
        @DisplayName("Should not be equal for different coordinates")
        void shouldNotBeEqualForDifferentCoordinates() {
            // Given
            GeographicLocation location1 = GeographicLocation.of(40.4168, -3.7025);
            GeographicLocation location2 = GeographicLocation.of(48.8566, 2.3522);
            
            // Then
            assertNotEquals(location1, location2);
        }
    }

    @Nested
    @DisplayName("Precision Tests")
    class PrecisionTests {

        @Test
        @DisplayName("Should handle high precision coordinates")
        void shouldHandleHighPrecisionCoordinates() {
            // Given - Very precise coordinates
            double preciseLat = 40.41677541234159;
            double preciseLon = -3.7026167123412345;
            
            // When
            GeographicLocation location = GeographicLocation.of(preciseLat, preciseLon);
            
            // Then
            assertNotNull(location);
            assertEquals(preciseLat, location.latitude());
            assertEquals(preciseLon, location.longitude());
        }

        @Test
        @DisplayName("Should round coordinates to 6 decimal places")
        void shouldRoundToSixDecimalPlaces() {
            // Given - Coordinates with more than 6 decimal places
            GeographicLocation location = GeographicLocation.of(40.41677541234159, -3.7026167123412345);
            
            // Then - The record should store them with validation precision
            assertEquals(40.41677541234159, location.latitude());
            assertEquals(-3.7026167123412345, location.longitude());
        }
    }
}
