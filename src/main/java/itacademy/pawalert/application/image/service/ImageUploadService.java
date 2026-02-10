package itacademy.pawalert.application.image.service;

import itacademy.pawalert.domain.image.port.inbound.ImageUploader;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class ImageUploadService {

    private final ImageUploader imageUploader;

    public ImageUploadService(ImageUploader imageUploader) {
        this.imageUploader = imageUploader;
    }

    public String upload(MultipartFile file, String folder) {
        return imageUploader.upload(file, folder);
    }
}
