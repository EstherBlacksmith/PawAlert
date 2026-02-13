package itacademy.pawalert.infrastructure.image.google;

import com.google.cloud.vision.v1.ImageAnnotatorClient;
import com.google.cloud.vision.v1.ImageAnnotatorSettings;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;
import java.io.InputStream;

@Configuration
public class GoogleVisionConfig {

    @Value("${google.cloud.credentials.path:}")
    private String credentialsPath;

    @Bean
    public ImageAnnotatorClient imageAnnotatorClient() throws IOException {
        // If credentials path is specified, use it
        if (credentialsPath != null && !credentialsPath.isEmpty()) {
            ImageAnnotatorSettings settings = ImageAnnotatorSettings.newBuilder()
                    .setCredentialsProvider(() -> {
                        try {
                            InputStream credentialsStream = getClass().getResourceAsStream("/google-credentials.json");
                            if (credentialsStream == null) {
                                throw new RuntimeException("No se encontró google-credentials.json en el classpath");
                            }
                            return com.google.auth.oauth2.GoogleCredentials.fromStream(credentialsStream);

                        } catch (IOException e) {
                            throw new RuntimeException("Failed to load Google credentials", e);
                        }
                    })
                    .build();
            return ImageAnnotatorClient.create(settings);
        }
        // Use default credentials (GOOGLE_APPLICATION_CREDENTIALS env var)
        return ImageAnnotatorClient.create();
    }
}
