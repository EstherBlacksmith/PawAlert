package itacademy.pawalert.infrastructure.rest.image.controller;

import itacademy.pawalert.application.image.service.ImageUploadService;
import itacademy.pawalert.application.image.service.ImageValidationService;
import itacademy.pawalert.domain.image.model.ContentSafetyStatus;
import itacademy.pawalert.domain.image.model.ImageValidationResult;
import itacademy.pawalert.domain.image.port.inbound.PetImageAnalyzer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockMultipartFile;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ImageControllerTest {

    @Mock
    private ImageUploadService uploadService;

    @Mock
    private ImageValidationService validationService;

    private ImageController controller;

    private PetImageAnalyzer petImageAnalyzer;

    @BeforeEach
    void setUp() {
        controller = new ImageController(validationService, uploadService,petImageAnalyzer);
    }

    @Test
    void uploadImage_success() {
        MockMultipartFile file = new MockMultipartFile(
                "file", "perro.jpg", "image/jpeg", "contenido".getBytes()
        );
        String expectedUrl = "https://res.cloudinary.com/demo/image/upload/pets/perro.jpg";
        when(uploadService.upload(any(), eq("pets"))).thenReturn(expectedUrl);

        ResponseEntity<String> response = controller.upload(file, "pets");

        assertEquals(200, response.getStatusCode().value());
        assertEquals(expectedUrl, response.getBody());
        verify(uploadService).upload(file, "pets");
    }

    @Test
    void validateImage_success() {
        MockMultipartFile file = new MockMultipartFile(
                "file", "perro.jpg", "image/jpeg", "contenido".getBytes()
        );
        ImageValidationResult result = ImageValidationResult.safe(
                "Golden Retriever",
                List.of("Dog", "Pet", "Animal")
        );
        when(validationService.validate(any())).thenReturn(result);

        ResponseEntity<ImageValidationResult> response = controller.validate(file);

        assertEquals(200, response.getStatusCode().value());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().isValid());
        assertEquals(ContentSafetyStatus.SAFE, response.getBody().safetyStatus());
        assertEquals("Golden Retriever", response.getBody().aiDescription());
    }

    @Test
    void validateImage_unsafe() {
        MockMultipartFile file = new MockMultipartFile(
                "file", "imagen.jpg", "image/jpeg", "contenido".getBytes()
        );
        ImageValidationResult result = ImageValidationResult.unsafe("Contenido inapropiado");
        when(validationService.validate(any())).thenReturn(result);

        ResponseEntity<ImageValidationResult> response = controller.validate(file);

        assertEquals(200, response.getStatusCode().value());
        assertNotNull(response.getBody());
        assertFalse(response.getBody().isValid());
        assertEquals(ContentSafetyStatus.UNSAFE, response.getBody().safetyStatus());
    }
}
