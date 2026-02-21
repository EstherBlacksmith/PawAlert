package itacademy.pawalert.infrastructure.rest.image.dto;

import org.springframework.web.multipart.MultipartFile;

public class ImageUploadRequest {
    private MultipartFile file;
    private String folder;

    // Getters y setters
    public MultipartFile getFile() {
        return file;
    }

    public void setFile(MultipartFile file) {
        this.file = file;
    }

    public String getFolder() {
        return folder;
    }

    public void setFolder(String folder) {
        this.folder = folder;
    }
}
