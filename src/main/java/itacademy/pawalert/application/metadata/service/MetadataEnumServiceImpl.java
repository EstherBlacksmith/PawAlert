package itacademy.pawalert.application.metadata.service;

import itacademy.pawalert.domain.alert.model.*;
import itacademy.pawalert.domain.image.model.ContentSafetyStatus;
import itacademy.pawalert.domain.pet.model.Gender;
import itacademy.pawalert.domain.pet.model.PetDisplayableEnum;
import itacademy.pawalert.domain.pet.model.Size;
import itacademy.pawalert.domain.pet.model.Species;
import itacademy.pawalert.domain.user.Role;
import itacademy.pawalert.domain.user.model.UserDisplayableEnum;
import itacademy.pawalert.infrastructure.rest.metadata.dto.MetadataDto;
import itacademy.pawalert.infrastructure.rest.metadata.dto.MetadataEnumListDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class MetadataEnumServiceImpl implements MetadataEnumService {

    @Override
    public List<MetadataEnumListDto> getMetadataEnum() {
        List<MetadataEnumListDto> metadataEnumList = new ArrayList<>();
        metadataEnumList.add(createMetadataEnumListDto("ROLE", Role.values()));
        metadataEnumList.add(createMetadataEnumListDto("SIZE", Size.values()));
        metadataEnumList.add(createMetadataEnumListDto("GENDER", Gender.values()));
        metadataEnumList.add(createMetadataEnumListDto("SPECIES", Species.values()));
        metadataEnumList.add(createMetadataEnumListDto("EVENT_TYPE", EventType.values()));
        metadataEnumList.add(createMetadataEnumListDto("CLOSURE_REASON", ClosureReason.values()));
        metadataEnumList.add(createMetadataEnumListDto("STATUS_NAMES", StatusNames.values()));
        metadataEnumList.add(createMetadataEnumListDto("NOTIFICATION_CHANNEL", NotificationChannel.values()));
        metadataEnumList.add(createMetadataEnumListDto("CONTENT_SAFETY_STATUS", ContentSafetyStatus.values()));

        return metadataEnumList;
    }

    private MetadataEnumListDto createMetadataEnumListDto(String type, Enum<?>[] values) {
        List<MetadataDto> metadata = Arrays.stream(values)
                .map(value -> new MetadataDto(value.name(), formatEnumValue(value)))
                .collect(Collectors.toList());
        return new MetadataEnumListDto(type, metadata);
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