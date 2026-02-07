package itacademy.pawalert.domain.alert.model;

import itacademy.pawalert.domain.alert.exception.InvalidLatitudeException;
import itacademy.pawalert.domain.alert.exception.InvalidLongitudeException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("GeographicLocation Tests")
class GeographicLocationTest {

    @Nested
    @DisplayName("Valid Coordinates Tests")
    class ValidCoordinatesTests {

        @Test
        @DisplayName("Should create location with valid latitude and longitude")
        void shouldCreateLocationWithValidCoordinates() {
            GeographicLocation location = GeographicLocation.of(40.4168, -3.7025);
            assertEquals(40.4168, location.latitude());
            assertEquals(-3.7025, location.longitude());
        }

        @Test
        @DisplayName("Should create location at equator")
        void shouldCreateLocationAtEquator() {
            GeographicLocation location = GeographicLocation.of(0.0, 0.0);
            assertNotNull(location);
        }

        @Test
        @DisplayName("Should create location at poles")
        void shouldCreateLocationAtPoles() {
            GeographicLocation northPole = GeographicLocation.of(90.0, 0.0);
            GeographicLocation southPole = GeographicLocation.of(-90.0, 0.0);
            assertNotNull(northPole);
            assertNotNull(southPole);
        }
    }

    @Nested
    @DisplayName("Invalid Latitude Tests")
    class InvalidLatitudeTests {

        @Test
        @DisplayName("Should throw exception for latitude above 90")
        void shouldThrowExceptionForLatitudeAbove90() {
            assertThrows(InvalidLatitudeException.class,
                    () -> GeographicLocation.of(91.0, 0.0));
        }

        @Test
        @DisplayName("Should throw exception for latitude below -90")
        void shouldThrowExceptionForLatitudeBelowMinus90() {
            assertThrows(InvalidLatitudeException.class,
                    () -> GeographicLocation.of(-91.0, 0.0));
        }
    }

    @Nested
    @DisplayName("Invalid Longitude Tests")
    class InvalidLongitudeTests {

        @Test
        @DisplayName("Should throw exception for longitude above 180")
        void shouldThrowExceptionForLongitudeAbove180() {
            assertThrows(InvalidLongitudeException.class,
                    () -> GeographicLocation.of(0.0, 181.0));
        }

        @Test
        @DisplayName("Should throw exception for longitude below -180")
        void shouldThrowExceptionForLongitudeBelowMinus180() {
            assertThrows(InvalidLongitudeException.class,
                    () -> GeographicLocation.of(0.0, -181.0));
        }
    }
}
