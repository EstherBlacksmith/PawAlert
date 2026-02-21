package itacademy.pawalert.infrastructure.rest.alert.dto;

import itacademy.pawalert.domain.alert.model.Description;
import itacademy.pawalert.domain.alert.model.StatusNames;
import itacademy.pawalert.domain.alert.model.Title;
import itacademy.pawalert.domain.user.model.PhoneNumber;
import itacademy.pawalert.domain.user.model.Surname;

import java.util.UUID;

public record AlertWithContactDTO(
        UUID id,
        UUID petId,
        UUID userId,
        Title title,
        Description description,
        StatusNames status,
        PhoneNumber creatorPhone,
        Surname creatorName) {

}
