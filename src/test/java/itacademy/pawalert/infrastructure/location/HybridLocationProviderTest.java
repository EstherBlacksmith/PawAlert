package itacademy.pawalert.infrastructure.location;

import itacademy.pawalert.domain.alert.exception.LocationException;
import itacademy.pawalert.domain.alert.model.GeographicLocation;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("HybridLocationProvider Unit Tests")
class HybridLocationProviderTest {

    @Mock
    private IpLocationService ipLocationService;

    @InjectMocks
    private HybridLocationProvider hybridLocationProvider;

    private static final GeographicLocation GPS_LOCATION = GeographicLocation.of(40.4168, -3.7025);  // Madrid
    private static final GeographicLocation IP_LOCATION = GeographicLocation.of(48.8566, 2.3522);   // Paris

    @Nested
    @DisplayName("getCurrentLocation Tests")
    class GetCurrentLocationTests {

        @Test
        @DisplayName("Should return cached GPS location when available")
        void shouldReturnCachedGpsLocationWhenAvailable() {
            // Given
            hybridLocationProvider.setGpsLocation(GPS_LOCATION);
            
            // When
            GeographicLocation result = hybridLocationProvider.getCurrentLocation();
            
            // Then
            assertNotNull(result);
            assertEquals(GPS_LOCATION.latitude(), result.latitude());
            assertEquals(GPS_LOCATION.longitude(), result.longitude());
            verifyNoInteractions(ipLocationService);
        }

        @Test
        @DisplayName("Should fallback to IP geolocation when GPS location is not cached")
        void shouldFallbackToIpGeolocationWhenGpsNotCached() {
            // Given - getCurrentLocation() usa getLocationFromIp()
            when(ipLocationService.getLocationFromIp()).thenReturn(IP_LOCATION);
            
            // When
            GeographicLocation result = hybridLocationProvider.getCurrentLocation();
            
            // Then
            assertNotNull(result);
            assertEquals(IP_LOCATION.latitude(), result.latitude());
            assertEquals(IP_LOCATION.longitude(), result.longitude());
            verify(ipLocationService).getLocationFromIp();
        }

        @Test
        @DisplayName("Should throw LocationException when IP is unavailable")
        void shouldThrowExceptionWhenIpUnavailable() {
            // Given - getLocationFromIp() lanza excepción
            when(ipLocationService.getLocationFromIp())
                    .thenThrow(new LocationException("Could not determine client IP address"));
            
            // When/Then
            LocationException exception = assertThrows(
                    LocationException.class,
                    () -> hybridLocationProvider.getCurrentLocation()
            );
            
            assertTrue(exception.getMessage().contains("Could not determine"));
        }

        @Test
        @DisplayName("Should clear cached GPS location and fallback to IP")
        void shouldClearGpsAndFallbackToIp() {
            // Given
            hybridLocationProvider.setGpsLocation(GPS_LOCATION);
            when(ipLocationService.getLocationFromIp()).thenReturn(IP_LOCATION);
            
            // When
            hybridLocationProvider.setGpsLocation(null);
            GeographicLocation result = hybridLocationProvider.getCurrentLocation();
            
            // Then
            assertNotNull(result);
            assertEquals(IP_LOCATION.latitude(), result.latitude());
        }
    }

    @Nested
    @DisplayName("getProviderType Tests")
    class GetProviderTypeTests {

        @Test
        @DisplayName("Should return GPS when cached GPS location is available")
        void shouldReturnGpsProviderTypeWhenCached() {
            // Given
            hybridLocationProvider.setGpsLocation(GPS_LOCATION);
            
            // When
            ProviderType providerType = hybridLocationProvider.getProviderType();
            
            // Then
            assertEquals(ProviderType.GPS, providerType);
            verifyNoInteractions(ipLocationService);
        }

        @Test
        @DisplayName("Should return IP_GEOLOCATION when GPS is not cached but IP returns location")
        void shouldReturnIpGeolocationWhenGpsNotCachedAndIpAvailable() {
            // Given - CORREGIDO: getProviderType() usa getClientLocation(), NO getLocationFromIp()
            when(ipLocationService.getClientLocation()).thenReturn(IP_LOCATION);
            
            // When
            ProviderType providerType = hybridLocationProvider.getProviderType();
            
            // Then
            assertEquals(ProviderType.IP_GEOLOCATION, providerType);
            verify(ipLocationService).getClientLocation();
        }

        @Test
        @DisplayName("Should return NONE when both GPS and IP are unavailable")
        void shouldReturnNoneWhenBothUnavailable() {
            // Given - getClientLocation() retorna null para IPs locales
            when(ipLocationService.getClientLocation()).thenReturn(null);
            
            // When
            ProviderType providerType = hybridLocationProvider.getProviderType();
            
            // Then
            assertEquals(ProviderType.NONE, providerType);
            verify(ipLocationService).getClientLocation();
        }

        @Test
        @DisplayName("Should return IP_GEOLOCATION when GPS is null and IP returns location")
        void shouldReturnIpGeolocationWhenGpsNull() {
            // Given - CORREGIDO: usar getClientLocation()
            when(ipLocationService.getClientLocation()).thenReturn(IP_LOCATION);
            
            // When
            ProviderType providerType = hybridLocationProvider.getProviderType();
            
            // Then
            assertEquals(ProviderType.IP_GEOLOCATION, providerType);
            verify(ipLocationService).getClientLocation();
        }

        @Test
        @DisplayName("Should prefer GPS over IP when both are available")
        void shouldPreferGpsOverIp() {
            // Given
            hybridLocationProvider.setGpsLocation(GPS_LOCATION);
            
            // When
            ProviderType providerType = hybridLocationProvider.getProviderType();
            
            // Then
            assertEquals(ProviderType.GPS, providerType);
            verifyNoInteractions(ipLocationService);
        }
    }

    @Nested
    @DisplayName("setGpsLocation Tests")
    class SetGpsLocationTests {

        @Test
        @DisplayName("Should successfully set a valid GPS location")
        void shouldSetValidGpsLocation() {
            // Given
            GeographicLocation newLocation = GeographicLocation.of(52.5200, 13.4050);  // Berlin
            
            // When
            hybridLocationProvider.setGpsLocation(newLocation);
            GeographicLocation result = hybridLocationProvider.getCurrentLocation();
            
            // Then
            assertNotNull(result);
            assertEquals(newLocation.latitude(), result.latitude());
            assertEquals(newLocation.longitude(), result.longitude());
        }

        @Test
        @DisplayName("Should handle setting null GPS location with IP fallback")
        void shouldHandleNullGpsLocationWithIpFallback() {
            // Given
            hybridLocationProvider.setGpsLocation(GPS_LOCATION);
            when(ipLocationService.getLocationFromIp()).thenReturn(IP_LOCATION);
            
            // When
            hybridLocationProvider.setGpsLocation(null);
            
            // Then - IP fallback should work
            GeographicLocation result = hybridLocationProvider.getCurrentLocation();
            assertNotNull(result);
            assertEquals(IP_LOCATION.latitude(), result.latitude());
        }
    }

    @Nested
    @DisplayName("Constructor Tests")
    class ConstructorTests {

        @Test
        @DisplayName("Should throw exception when IP service is unavailable at initialization")
        void shouldThrowExceptionWhenIpServiceUnavailable() {
            // Given - Simular que getLocationFromIp() lanza excepción
            // Esto es lo que sucede cuando el servicio de IP falla
            when(ipLocationService.getLocationFromIp())
                    .thenThrow(new LocationException("Could not determine client IP address"));
            
            // When - Nueva instancia sin GPS cacheado
            HybridLocationProvider newProvider = new HybridLocationProvider(ipLocationService);
            
            // Then - getCurrentLocation() debe lanzar excepción porque no hay GPS ni IP válido
            assertThrows(LocationException.class, () -> newProvider.getCurrentLocation());
        }

        @Test
        @DisplayName("Should correctly handle injected IpLocationService dependency")
        void shouldHandleInjectedDependency() {
            // Given - getCurrentLocation() usa getLocationFromIp()
            GeographicLocation expectedLocation = GeographicLocation.of(41.3851, 2.1734);  // Barcelona
            when(ipLocationService.getLocationFromIp()).thenReturn(expectedLocation);
            
            // When
            GeographicLocation result = hybridLocationProvider.getCurrentLocation();
            
            // Then
            assertNotNull(result);
            assertEquals(expectedLocation.latitude(), result.latitude());
            assertEquals(expectedLocation.longitude(), result.longitude());
        }
    }

    @Nested
    @DisplayName("Integration Tests - Full Workflow")
    class IntegrationTests {

        @Test
        @DisplayName("Full workflow: Set GPS, verify GPS used, clear GPS, verify IP fallback")
        void fullWorkflowGpsToIpFallback() {
            // Given
            GeographicLocation gpsLocation = GeographicLocation.of(43.7102, 7.2620);  // Monaco
            GeographicLocation ipLocation = GeographicLocation.of(45.4642, 9.1900);  // Milan
            
            // IP service mocks
            when(ipLocationService.getLocationFromIp()).thenReturn(ipLocation);
            when(ipLocationService.getClientLocation()).thenReturn(ipLocation);

            // Step 1: No GPS set, should use IP
            GeographicLocation result1 = hybridLocationProvider.getCurrentLocation();
            assertNotNull(result1);
            assertEquals(ipLocation.latitude(), result1.latitude());

            // Step 2: Set GPS location
            hybridLocationProvider.setGpsLocation(gpsLocation);

            // Step 3: Verify GPS is used
            GeographicLocation result2 = hybridLocationProvider.getCurrentLocation();
            assertNotNull(result2);
            assertEquals(gpsLocation.latitude(), result2.latitude());

            // Step 4: Verify provider type is GPS
            assertEquals(ProviderType.GPS, hybridLocationProvider.getProviderType());

            // Step 5: Clear GPS
            hybridLocationProvider.setGpsLocation(null);

            // Step 6: Verify fallback to IP
            GeographicLocation result3 = hybridLocationProvider.getCurrentLocation();
            assertNotNull(result3);
            assertEquals(ipLocation.latitude(), result3.latitude());

            // Step 7: Verify provider type is IP_GEOLOCATION
            assertEquals(ProviderType.IP_GEOLOCATION, hybridLocationProvider.getProviderType());
        }

        @Test
        @DisplayName("Should handle rapid GPS location updates correctly")
        void shouldHandleRapidGpsUpdates() {
            // Given - Multiple GPS locations
            GeographicLocation location1 = GeographicLocation.of(37.7749, -122.4194);  // San Francisco
            GeographicLocation location2 = GeographicLocation.of(40.7128, -74.0060);   // New York
            GeographicLocation location3 = GeographicLocation.of(-33.8688, 151.2093); // Sydney
            
            // When/Then - Rapid updates
            hybridLocationProvider.setGpsLocation(location1);
            assertEquals(ProviderType.GPS, hybridLocationProvider.getProviderType());
            
            hybridLocationProvider.setGpsLocation(location2);
            assertEquals(ProviderType.GPS, hybridLocationProvider.getProviderType());
            
            hybridLocationProvider.setGpsLocation(location3);
            assertEquals(ProviderType.GPS, hybridLocationProvider.getProviderType());
            
            // Final verification
            GeographicLocation finalResult = hybridLocationProvider.getCurrentLocation();
            assertEquals(location3.latitude(), finalResult.latitude());
            assertEquals(location3.longitude(), finalResult.longitude());
        }
    }

    @Nested
    @DisplayName("Edge Cases Tests")
    class EdgeCasesTests {

        @Test
        @DisplayName("Should handle boundary latitude values")
        void shouldHandleBoundaryLatitudeValues() {
            // Given - Boundary values for latitude
            GeographicLocation minLat = GeographicLocation.of(-90.0, 0.0);
            GeographicLocation maxLat = GeographicLocation.of(90.0, 0.0);
            
            // When/Then
            hybridLocationProvider.setGpsLocation(minLat);
            assertEquals(ProviderType.GPS, hybridLocationProvider.getProviderType());
            
            hybridLocationProvider.setGpsLocation(maxLat);
            assertEquals(ProviderType.GPS, hybridLocationProvider.getProviderType());
        }

        @Test
        @DisplayName("Should handle zero coordinates")
        void shouldHandleZeroCoordinates() {
            // Given - Null Island (0, 0)
            GeographicLocation nullIsland = GeographicLocation.of(0.0, 0.0);
            hybridLocationProvider.setGpsLocation(nullIsland);
            
            // When
            GeographicLocation result = hybridLocationProvider.getCurrentLocation();
            
            // Then
            assertNotNull(result);
            assertEquals(0.0, result.latitude());
            assertEquals(0.0, result.longitude());
            assertEquals(ProviderType.GPS, hybridLocationProvider.getProviderType());
        }

        @Test
        @DisplayName("Should handle very precise coordinates")
        void shouldHandlePreciseCoordinates() {
            // Given - High precision coordinates
            GeographicLocation preciseLocation = GeographicLocation.of(40.4167754, -3.7026167);
            hybridLocationProvider.setGpsLocation(preciseLocation);
            
            // When
            GeographicLocation result = hybridLocationProvider.getCurrentLocation();
            
            // Then
            assertEquals(preciseLocation.latitude(), result.latitude());
            assertEquals(preciseLocation.longitude(), result.longitude());
        }
    }
}
