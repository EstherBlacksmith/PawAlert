package itacademy.pawalert.infrastructure.image.cloudinary;

import com.cloudinary.Cloudinary;
import itacademy.pawalert.domain.image.port.inbound.ImageUploader;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.Map;

@Service
public class CloudinaryUploadService  implements ImageUploader {

    private final Cloudinary cloudinary;

    // Inject Cloudinary by constructor
    public CloudinaryUploadService(Cloudinary cloudinary) {
        this.cloudinary = cloudinary;
    }

    public String upload(MultipartFile file, String folder) {
        try {
            //Create the temporal file
            byte[] imageBytes = file.getBytes();
            File tempFile = File.createTempFile("upload", ".tmp");
            Files.write(tempFile.toPath(), imageBytes);

            // Upload options
            Map<String, Object> options = new HashMap<>();
            options.put("folder", folder);  // Folder in Cloudinary
            options.put("resource_type", "image");

            // Upload
            Map result = cloudinary.uploader().upload(tempFile, options);

            // Delete the temporal file
            tempFile.delete();

            // Returns the URL
            return (String) result.get("secure_url");
        } catch (IOException e) {
            throw new RuntimeException("Failed to upload to Cloudinary", e);
        }
    }
}

