package itacademy.pawalert.application.pet.port.inbound;

import itacademy.pawalert.infrastructure.rest.pet.dto.ImageValidationResponse;
import org.springframework.web.multipart.MultipartFile;

public interface ValidateImageUseCase {
    ImageValidationResponse validateImage(MultipartFile file);
}
