package itacademy.pawalert.domain.image.port.inbound;

import org.springframework.web.multipart.MultipartFile;

public interface ImageUploader {
    String upload(MultipartFile file, String folder);
}