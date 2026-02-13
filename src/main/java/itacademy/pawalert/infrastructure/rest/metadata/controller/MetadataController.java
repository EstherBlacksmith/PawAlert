package itacademy.pawalert.infrastructure.rest.metadata.controller;

import itacademy.pawalert.infrastructure.rest.metadata.dto.MetadataListDto;
import itacademy.pawalert.infrastructure.rest.metadata.service.MetadataService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping(path = "/api/v1/metadata", produces = MediaType.APPLICATION_JSON_VALUE)
public class MetadataController {

    private final MetadataService metadataService;

    @GetMapping("/get-metadata")
    public ResponseEntity<List<MetadataListDto>> getMetadata() {
        List<MetadataListDto> metadata = metadataService.getMetadata();
        return ResponseEntity.ok(metadata);
    }
}