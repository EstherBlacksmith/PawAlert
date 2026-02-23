package itacademy.pawalert.infrastructure.image.google;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.vision.v1.ImageAnnotatorClient;
import com.google.cloud.vision.v1.ImageAnnotatorSettings;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

import java.io.IOException;
import java.io.InputStream;

@Configuration
public class GoogleVisionConfig {

    @Value("${google.cloud.credentials.path:}")
    private String credentialsPath;

    @Bean
    public ImageAnnotatorClient imageAnnotatorClient() throws IOException {
        GoogleCredentials credentials = loadCredentials();
        
        if (credentials != null) {
            ImageAnnotatorSettings settings = ImageAnnotatorSettings.newBuilder()
                    .setCredentialsProvider(() -> credentials)
                    .build();
            return ImageAnnotatorClient.create(settings);
        }
        
        // Fall back to Application Default Credentials (ADC)
        return ImageAnnotatorClient.create();
    }

    /**
     * Implements a robust fallback mechanism for loading Google Cloud credentials:
     * 1. First checks the google.cloud.credentials.path property
     * 2. Falls back to GOOGLE_APPLICATION_CREDENTIALS environment variable
     * 3. Falls back to classpath resource /google-credentials.json
     * 4. Returns null to allow ADC lookup
     */
    private GoogleCredentials loadCredentials() throws IOException {
        // Step 1: Check google.cloud.credentials.path property
        if (credentialsPath != null && !credentialsPath.isEmpty()) {
            try {
                InputStream credentialsStream = loadResourceStream(credentialsPath);
                if (credentialsStream != null) {
                    return GoogleCredentials.fromStream(credentialsStream);
                }
            } catch (IOException e) {
                // Log and continue to next fallback
                System.err.println("Failed to load credentials from path: " + credentialsPath + ". Error: " + e.getMessage());
            }
        }

        // Step 2: Check GOOGLE_APPLICATION_CREDENTIALS environment variable
        String envCredentialsPath = System.getenv("GOOGLE_APPLICATION_CREDENTIALS");
        if (envCredentialsPath != null && !envCredentialsPath.isEmpty()) {
            try {
                InputStream credentialsStream = loadResourceStream(envCredentialsPath);
                if (credentialsStream != null) {
                    return GoogleCredentials.fromStream(credentialsStream);
                }
            } catch (IOException e) {
                // Log and continue to next fallback
                System.err.println("Failed to load credentials from GOOGLE_APPLICATION_CREDENTIALS: " + e.getMessage());
            }
        }

        // Step 3: Check classpath resource /google-credentials.json
        try {
            InputStream credentialsStream = getClass().getResourceAsStream("/google-credentials.json");
            if (credentialsStream != null) {
                return GoogleCredentials.fromStream(credentialsStream);
            }
        } catch (IOException e) {
            // Log and continue to ADC
            System.err.println("Failed to load credentials from classpath resource: " + e.getMessage());
        }

        // Step 4: Return null to allow ADC lookup
        return null;
    }

    /**
     * Helper method to load a resource stream from various sources
     */
    private InputStream loadResourceStream(String resourcePath) throws IOException {
        // Handle classpath resources (e.g., "classpath:/google-credentials.json")
        if (resourcePath.startsWith("classpath:")) {
            String path = resourcePath.substring("classpath:".length());
            Resource resource = new ClassPathResource(path);
            if (resource.exists()) {
                return resource.getInputStream();
            }
            return null;
        }

        // Handle file system paths
        try {
            Resource resource = new org.springframework.core.io.FileSystemResource(resourcePath);
            if (resource.exists()) {
                return resource.getInputStream();
            }
        } catch (Exception e) {
            // Continue to next fallback
        }

        return null;
    }
}
