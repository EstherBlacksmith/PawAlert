package itacademy.pawalert.application.image.service;

import itacademy.pawalert.domain.image.model.ImageValidationResult;
import itacademy.pawalert.domain.image.port.inbound.ImageValidator;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class ImageValidationService {

    private final ImageValidator imageValidator;

    public ImageValidationService(ImageValidator imageValidator) {
        this.imageValidator = imageValidator;
    }

    public ImageValidationResult validate(MultipartFile file) {
        return imageValidator.validate(file);
    }
}
