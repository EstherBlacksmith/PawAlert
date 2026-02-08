package itacademy.pawalert.infrastructure.location;

import itacademy.pawalert.domain.alert.exception.LocationException;
import itacademy.pawalert.domain.alert.model.GeographicLocation;
import itacademy.pawalert.domain.pet.exception.PetNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

/**
 * Unit tests for IpLocationService.
 * 
 * Tests IP address detection from HTTP headers and local/private IP validation.
 */
@DisplayName("IpLocationService Unit Tests")
class IpLocationServiceTest {

    private IpLocationService ipLocationService;
    private MockHttpServletRequest mockRequest;

    @BeforeEach
    void setUp() {
        ipLocationService = new IpLocationService();
        mockRequest = new MockHttpServletRequest();
        
        // Set up the request context
        ServletRequestAttributes requestAttributes = new ServletRequestAttributes(mockRequest);
        RequestContextHolder.setRequestAttributes(requestAttributes);
    }

    @Nested
    @DisplayName("getClientIp Tests")
    class GetClientIpTests {

        @Test
        @DisplayName("Should extract IP from X-Forwarded-For header")
        void shouldExtractFromXForwardedFor() {
            // Given
            mockRequest.addHeader("X-Forwarded-For", "203.0.113.195, 70.41.3.18, 150.172.238.178");
            
            // When
            String result = ipLocationService.getClientIp();
            
            // Then
            assertEquals("203.0.113.195", result);
        }

        @Test
        @DisplayName("Should extract IP from X-Real-IP header when X-Forwarded-For is missing")
        void shouldExtractFromXRealIp() {
            // Given
            mockRequest.addHeader("X-Real-IP", "198.51.100.42");
            
            // When
            String result = ipLocationService.getClientIp();
            
            // Then
            assertEquals("198.51.100.42", result);
        }

        @Test
        @DisplayName("Should use RemoteAddr when no headers are present")
        void shouldUseRemoteAddrWhenNoHeaders() {
            // Given
            mockRequest.setRemoteAddr("192.0.2.1");
            
            // When
            String result = ipLocationService.getClientIp();
            
            // Then
            assertEquals("192.0.2.1", result);
        }

        @Test
        @DisplayName("Should return null when no request context is available")
        void shouldReturnNullWhenNoRequestContext() {
            // Given
            RequestContextHolder.resetRequestAttributes();
            
            // When
            String result = ipLocationService.getClientIp();
            
            // Then
            assertNull(result);
        }

        @Test
        @DisplayName("Should handle X-Forwarded-For with single IP")
        void shouldHandleSingleIpInXForwardedFor() {
            // Given
            mockRequest.addHeader("X-Forwarded-For", "203.0.113.195");
            
            // When
            String result = ipLocationService.getClientIp();
            
            // Then
            assertEquals("203.0.113.195", result);
        }

        @Test
        @DisplayName("Should trim whitespace from X-Forwarded-For IP")
        void shouldTrimWhitespaceFromXForwardedFor() {
            // Given
            mockRequest.addHeader("X-Forwarded-For", "  203.0.113.195  ");
            
            // When
            String result = ipLocationService.getClientIp();
            
            // Then
            assertEquals("203.0.113.195", result);
        }
    }

    @Nested
    @DisplayName("isLocalOrPrivateIp Tests")
    class IsLocalOrPrivateIpTests {

        @ParameterizedTest
        @ValueSource(strings = {
                "127.0.0.1", "localhost", "0.0.0.0", "::1"
        })
        @DisplayName("Should return true for localhost IPs")
        void shouldReturnTrueForLocalhostIps(String ip) {
            assertTrue(ipLocationService.isLocalOrPrivateIp(ip));
        }

        @ParameterizedTest
        @ValueSource(strings = {
                "192.168.0.1", "192.168.1.100", "192.168.255.255"
        })
        @DisplayName("Should return true for 192.168.x.x private network")
        void shouldReturnTrueFor192_168_PrivateNetwork(String ip) {
            assertTrue(ipLocationService.isLocalOrPrivateIp(ip));
        }

        @ParameterizedTest
        @ValueSource(strings = {
                "10.0.0.1", "10.255.255.255"
        })
        @DisplayName("Should return true for 10.x.x.x private network")
        void shouldReturnTrueFor10_PrivateNetwork(String ip) {
            assertTrue(ipLocationService.isLocalOrPrivateIp(ip));
        }

        @ParameterizedTest
        @ValueSource(strings = {
                "172.16.0.1", "172.17.0.1", "172.18.0.1", "172.19.0.1",
                "172.20.0.1", "172.21.0.1", "172.22.0.1", "172.23.0.1",
                "172.24.0.1", "172.25.0.1", "172.26.0.1", "172.27.0.1",
                "172.28.0.1", "172.29.0.1", "172.30.0.1", "172.31.0.1",
                "172.31.255.255"
        })
        @DisplayName("Should return true for 172.16.x.x - 172.31.x.x private networks")
        void shouldReturnTrueFor172_PrivateNetwork(String ip) {
            assertTrue(ipLocationService.isLocalOrPrivateIp(ip));
        }

        @ParameterizedTest
        @ValueSource(strings = {
                "172.15.0.1", "172.32.0.1"
        })
        @DisplayName("Should return false for 172.15.x.x and 172.32.x.x (not private)")
        void shouldReturnFalseForNonPrivate172Networks(String ip) {
            assertFalse(ipLocationService.isLocalOrPrivateIp(ip));
        }

        @ParameterizedTest
        @ValueSource(strings = {
                "8.8.8.8", "1.1.1.1", "203.0.113.195", "198.51.100.42"
        })
        @DisplayName("Should return false for public IPs")
        void shouldReturnFalseForPublicIps(String ip) {
            assertFalse(ipLocationService.isLocalOrPrivateIp(ip));
        }

        @Test
        @DisplayName("Should return true for null IP")
        void shouldReturnTrueForNullIp() {
            assertTrue(ipLocationService.isLocalOrPrivateIp(null));
        }
    }

    @Nested
    @DisplayName("getClientLocation Tests")
    class GetClientLocationTests {

        @Test
        @DisplayName("Should return null when IP is localhost")
        void shouldReturnNullForLocalhostIp() {
            // Given
            mockRequest.setRemoteAddr("127.0.0.1");
            
            // When
            GeographicLocation result = ipLocationService.getClientLocation();
            
            // Then
            assertNull(result);
        }

        @Test
        @DisplayName("Should return null when IP is private network")
        void shouldReturnNullForPrivateNetworkIp() {
            // Given
            mockRequest.setRemoteAddr("192.168.1.100");
            
            // When
            GeographicLocation result = ipLocationService.getClientLocation();
            
            // Then
            assertNull(result);
        }

        @Test
        @DisplayName("Should return null when IP is 10.x.x.x private network")
        void shouldReturnNullFor10PrivateNetworkIp() {
            // Given
            mockRequest.setRemoteAddr("10.0.0.1");
            
            // When
            GeographicLocation result = ipLocationService.getClientLocation();
            
            // Then
            assertNull(result);
        }

        @Test
        @DisplayName("Should return null when request has no IP")
        void shouldReturnNullWhenNoIp() {
            // Given
            RequestContextHolder.resetRequestAttributes();
            
            // When
            GeographicLocation result = ipLocationService.getClientLocation();
            
            // Then
            assertNull(result);
        }
    }

    @Nested
    @DisplayName("getLocationFromIp Tests")
    class GetLocationFromIpTests {

        @Test
        @DisplayName("Should throw LocationException for private IP")
        void shouldThrowExceptionForPrivateIpInGetLocationFromIp() {
            // Given - Set a private IP in the request
            mockRequest.setRemoteAddr("192.168.1.100");
            
            // When/Then - getLocationFromIp() should throw LocationException for private IPs
            LocationException exception = assertThrows(
                    LocationException.class,
                    () -> ipLocationService.getLocationFromIp()
            );
            
            assertTrue(exception.getMessage().contains("Could not determine"));
        }

        @Test
        @DisplayName("Should throw LocationException for localhost IP")
        void shouldThrowExceptionForLocalhostIpInGetLocationFromIp() {
            // Given - Set localhost IP
            mockRequest.setRemoteAddr("127.0.0.1");
            
            // When/Then
            assertThrows(
                    LocationException.class,
                    () -> ipLocationService.getLocationFromIp()
            );
        }

        @Test
        @DisplayName("Should throw LocationException when no request context")
        void shouldThrowExceptionWhenNoRequestContext() {
            // Given
            RequestContextHolder.resetRequestAttributes();
            
            // When/Then
            assertThrows(
                    LocationException.class,
                    () -> ipLocationService.getLocationFromIp()
            );
        }

        @Test
        @DisplayName("Should return null for failed API response - getClientLocation()")
        void shouldReturnNullForFailedApiResponseGetClientLocation() {
            // Given - Set a private IP so getClientLocation() returns null early
            mockRequest.setRemoteAddr("192.168.1.100");
            
            // When/Then - getClientLocation() returns null for private IPs
            assertNull(ipLocationService.getClientLocation());
        }
    }


    @Nested
    @DisplayName("Edge Cases Tests")
    class EdgeCasesTests {

        @Test
        @DisplayName("Should handle empty X-Forwarded-For header")
        void shouldHandleEmptyXForwardedForHeader() {
            // Given
            mockRequest.addHeader("X-Forwarded-For", "");
            mockRequest.setRemoteAddr("8.8.8.8");
            
            // When
            String result = ipLocationService.getClientIp();
            
            // Then
            assertEquals("8.8.8.8", result);
        }

        @Test
        @DisplayName("Should handle empty X-Real-IP header")
        void shouldHandleEmptyXRealIpHeader() {
            // Given
            mockRequest.addHeader("X-Real-IP", "");
            mockRequest.setRemoteAddr("1.2.3.4");
            
            // When
            String result = ipLocationService.getClientIp();
            
            // Then
            assertEquals("1.2.3.4", result);
        }

        @Test
        @DisplayName("Should prioritize X-Forwarded-For over X-Real-IP")
        void shouldPrioritizeXForwardedForOverXRealIp() {
            // Given
            mockRequest.addHeader("X-Forwarded-For", "203.0.113.195");
            mockRequest.addHeader("X-Real-IP", "198.51.100.42");
            mockRequest.setRemoteAddr("192.0.2.1");
            
            // When
            String result = ipLocationService.getClientIp();
            
            // Then
            assertEquals("203.0.113.195", result);
        }

        @Test
        @DisplayName("Should handle multiple spaces in X-Forwarded-For")
        void shouldHandleMultipleSpacesInXForwardedFor() {
            // Given
            mockRequest.addHeader("X-Forwarded-For", "203.0.113.195,   70.41.3.18  , 150.172.238.178");
            
            // When
            String result = ipLocationService.getClientIp();
            
            // Then
            assertEquals("203.0.113.195", result);
        }
    }
}
