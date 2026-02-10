package itacademy.pawalert.infrastructure.image.google;

import itacademy.pawalert.domain.image.model.ImageValidationResult;
import itacademy.pawalert.domain.image.port.inbound.ImageValidator;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Service
public class GoogleVisionImageValidator implements ImageValidator {
    private final GoogleVisionService visionService;

    public GoogleVisionImageValidator(GoogleVisionService visionService) {
        this.visionService = visionService;
    }

    public ImageValidationResult validate(MultipartFile file) {
        try {
            return visionService.analyzeImage(file.getBytes());
        } catch (IOException e) {
            throw new RuntimeException("Failed to validate image", e);
        }
    }
}
