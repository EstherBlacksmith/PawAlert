package itacademy.pawalert.infrastructure.rest.metadata.service;

import itacademy.pawalert.infrastructure.rest.metadata.dto.MetadataListDto;

import java.util.List;

public interface MetadataService {

    List<MetadataListDto> getMetadata();
}