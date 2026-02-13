package itacademy.pawalert.infrastructure.rest.metadata.service;

import itacademy.pawalert.domain.alert.model.*;
import itacademy.pawalert.domain.image.model.ContentSafetyStatus;
import itacademy.pawalert.domain.pet.model.Gender;
import itacademy.pawalert.domain.pet.model.PetDisplayableEnum;
import itacademy.pawalert.domain.pet.model.Size;
import itacademy.pawalert.domain.pet.model.Species;
import itacademy.pawalert.domain.user.Role;
import itacademy.pawalert.domain.user.model.UserDisplayableEnum;
import itacademy.pawalert.infrastructure.rest.metadata.dto.MetadataDto;
import itacademy.pawalert.infrastructure.rest.metadata.dto.MetadataListDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class MetadataServiceImpl implements MetadataService {

    @Override
    public List<MetadataListDto> getMetadata() {
        List<MetadataListDto> metadataList = new ArrayList<>();
        metadataList.add(createMetadataListDto("ROLE", Role.values()));
        metadataList.add(createMetadataListDto("SIZE", Size.values()));
        metadataList.add(createMetadataListDto("GENDER", Gender.values()));
        metadataList.add(createMetadataListDto("SPECIES", Species.values()));
        metadataList.add(createMetadataListDto("EVENT_TYPE", EventType.values()));
        metadataList.add(createMetadataListDto("CLOSURE_REASON", ClosureReason.values()));
        metadataList.add(createMetadataListDto("STATUS_NAMES", StatusNames.values()));
        metadataList.add(createMetadataListDto("NOTIFICATION_CHANNEL", NotificationChannel.values()));
        metadataList.add(createMetadataListDto("CONTENT_SAFETY_STATUS", ContentSafetyStatus.values()));

        return metadataList;
    }

    private MetadataListDto createMetadataListDto(String type, Enum<?>[] values) {
        List<MetadataDto> metadata = Arrays.stream(values)
                .map(value -> new MetadataDto(value.name(), formatEnumValue(value)))
                .collect(Collectors.toList());
        return new MetadataListDto(type, metadata);
    }

    private String formatEnumValue(Enum<?> value) {
        if (value instanceof AlertDisplayableEnum) {
            return ((AlertDisplayableEnum) value).getDisplayName();
        }

        if (value instanceof PetDisplayableEnum) {
            return ((PetDisplayableEnum) value).getDisplayName();
        }

        if (value instanceof UserDisplayableEnum) {
            return ((UserDisplayableEnum) value).getDisplayName();
        }

        return value.name();
    }
}