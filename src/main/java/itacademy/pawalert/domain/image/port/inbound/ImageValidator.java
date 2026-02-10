package itacademy.pawalert.domain.image.port.inbound;

import itacademy.pawalert.domain.image.model.ImageValidationResult;
import org.springframework.web.multipart.MultipartFile;

public interface ImageValidator {
    ImageValidationResult validate(MultipartFile file);
}