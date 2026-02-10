package itacademy.pawalert.infrastructure.rest.image.controller;


import itacademy.pawalert.application.image.service.ImageUploadService;
import itacademy.pawalert.application.image.service.ImageValidationService;
import itacademy.pawalert.domain.image.model.ImageValidationResult;

import org.springframework.http.ResponseEntity;

import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/images")
public class ImageController {
    private final ImageValidationService validationService;
    private final ImageUploadService uploadService;

    public ImageController(ImageValidationService validationService, ImageUploadService uploadService) {
        this.validationService = validationService;
        this.uploadService = uploadService;
    }

    @PostMapping("/validate")
    public ResponseEntity<ImageValidationResult> validate(@RequestParam MultipartFile file) {
        ImageValidationResult result = validationService.validate(file);
        return ResponseEntity.ok(result);
    }


    @PostMapping("/upload")
    public ResponseEntity<String> upload(@RequestParam MultipartFile file,
                                         @RequestParam String folder) {
        String url = uploadService.upload(file, folder);

        return ResponseEntity.ok(url);
    }
}
