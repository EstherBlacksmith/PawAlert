package itacademy.pawalert.application.port.inbound;

import itacademy.pawalert.domain.alert.model.GeographicLocation;
import itacademy.pawalert.domain.alert.model.StatusNames;
import itacademy.pawalert.domain.alert.model.Title;
import itacademy.pawalert.domain.pet.model.PetDescription;
import itacademy.pawalert.domain.pet.model.PetName;
import itacademy.pawalert.domain.user.model.PhoneNumber;
import itacademy.pawalert.domain.user.model.Username;

import java.util.UUID;

public record TelegramAlertDTO(
        UUID alertId,
        PetName petName,
        GeographicLocation location,
        StatusNames status,
        Title title,
        PetDescription petDescription,
        Username username,
        PhoneNumber phoneNumber
) {}