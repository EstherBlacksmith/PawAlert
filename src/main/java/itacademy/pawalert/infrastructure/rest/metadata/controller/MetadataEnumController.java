package itacademy.pawalert.infrastructure.rest.metadata.controller;

import itacademy.pawalert.application.metadata.service.MetadataEnumService;
import itacademy.pawalert.infrastructure.rest.metadata.dto.MetadataEnumListDto;
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
public class MetadataEnumController {

    private final MetadataEnumService metadataEnumService;

    @GetMapping("/get-metadata")
    public ResponseEntity<List<MetadataEnumListDto>> getMetadata() {
        List<MetadataEnumListDto> metadata = metadataEnumService.getMetadataEnum();
        return ResponseEntity.ok(metadata);
    }
}