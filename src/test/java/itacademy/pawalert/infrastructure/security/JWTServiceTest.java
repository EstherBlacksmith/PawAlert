package itacademy.pawalert.infrastructure.security;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit test for JWTService
 * Verify the tokens JWT generation and validation
 */
class JWTServiceTest {

    private JWTService jwtService;

    @BeforeEach
    void setUp() {
        jwtService = new JWTService();
    }

    @Test
    void testGenerateToken_ShouldReturnValidJWT() {
        // Given: User with credentials
        UserDetails userDetails = User.builder()
                .username("testuser")
                .password("encodedPassword")
                .authorities(Collections.emptyList())
                .build();

        // When: Generate a token
        String token = jwtService.generateToken(userDetails);

        // Then: Token must be valid
        assertNotNull(token, "Token should not be null");
        assertFalse(token.isEmpty(), "Token should not be empty");
        
        // A JWT valid token have 3 separated parts, split by dots
        String[] parts = token.split("\\.");
        assertEquals(3, parts.length, "JWT must have 3 parts (header.payload.signature)");
    }

    @Test
    void testExtractUsername_ShouldReturnCorrectUsername() {
        // Given
        UserDetails userDetails = User.builder()
                .username("paco")
                .password("password")
                .authorities(Collections.emptyList())
                .build();
        String token = jwtService.generateToken(userDetails);

        // When
        String extractedUsername = jwtService.extractUsername(token);

        // Then
        assertEquals("paco", extractedUsername, "Extracted username should match");
    }

    @Test
    void testValidateToken_WithValidToken_ShouldReturnTrue() {
        // Given
        UserDetails userDetails = User.builder()
                .username("testuser")
                .password("password")
                .authorities(Collections.emptyList())
                .build();
        String token = jwtService.generateToken(userDetails);

        // When
        boolean isValid = jwtService.validateToken(token, userDetails);

        // Then
        assertTrue(isValid, "Valid token should return true");
    }

    @Test
    void testValidateToken_WithDifferentUser_ShouldReturnFalse() {
        // Given: Token generated for user1
        UserDetails user1 = User.builder()
                .username("user1")
                .password("password")
                .authorities(Collections.emptyList())
                .build();
        String token = jwtService.generateToken(user1);

        // And: UserDetails for user2
        UserDetails user2 = User.builder()
                .username("user2")
                .password("password")
                .authorities(Collections.emptyList())
                .build();

        // When
        boolean isValid = jwtService.validateToken(token, user2);

        // Then
        assertFalse(isValid, "Token from user1 should not be valid for user2");
    }

    @Test
    void testIsTokenExpired_WithFreshToken_ShouldReturnFalse() {
        // Given
        UserDetails userDetails = User.builder()
                .username("testuser")
                .password("password")
                .authorities(Collections.emptyList())
                .build();
        String token = jwtService.generateToken(userDetails);

        // When
        boolean isExpired = jwtService.isTokenExpired(token);

        // Then
        assertFalse(isExpired, "Fresh token should not be expired");
    }

    @Test
    void testGenerateToken_DifferentUsers_ShouldProduceDifferentTokens() {
        // Given
        UserDetails user1 = User.builder()
                .username("user1")
                .password("password")
                .authorities(Collections.emptyList())
                .build();
        UserDetails user2 = User.builder()
                .username("user2")
                .password("password")
                .authorities(Collections.emptyList())
                .build();

        // When
        String token1 = jwtService.generateToken(user1);
        String token2 = jwtService.generateToken(user2);

        // Then
        assertNotEquals(token1, token2, "Tokens for different users should be different");
    }
}
