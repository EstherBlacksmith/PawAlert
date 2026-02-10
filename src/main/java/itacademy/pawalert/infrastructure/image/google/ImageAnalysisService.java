package itacademy.pawalert.infrastructure.image.google;

import itacademy.pawalert.domain.image.exception.ImageValidationException;
import itacademy.pawalert.domain.image.model.ContentSafetyStatus;
import itacademy.pawalert.domain.image.model.ImageValidationResult;
import itacademy.pawalert.domain.image.port.inbound.ImageValidator;
import itacademy.pawalert.infrastructure.image.cloudinary.CloudinaryUploadService;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class ImageAnalysisService {

    private final ImageValidator imageValidator;
    private final CloudinaryUploadService uploadService;

    public ImageAnalysisService(ImageValidator imageValidator,
                                CloudinaryUploadService uploadService) {
        this.imageValidator = imageValidator;
        this.uploadService = uploadService;
    }

    public String uploadAndValidate(MultipartFile imageBytes, String folder) {
        // 1. Validate with Google Vision
        ImageValidationResult result = imageValidator.validate(imageBytes);

        if (!result.isValid()) {
            throw new ImageValidationException("Image unsafe: " + result.safetyStatus(),ContentSafetyStatus.UNSAFE);
        }

        // 2. Upload to Cloudinary
        return uploadService.upload(imageBytes, folder);

    }
}
