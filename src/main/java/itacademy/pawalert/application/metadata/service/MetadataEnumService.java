package itacademy.pawalert.application.metadata.service;

import itacademy.pawalert.infrastructure.rest.metadata.dto.MetadataEnumListDto;

import java.util.List;

public interface MetadataEnumService {

    List<MetadataEnumListDto> getMetadataEnum();
}