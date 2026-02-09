package itacademy.pawalert.application.user.port.inbound;

import itacademy.pawalert.domain.user.User;
import itacademy.pawalert.domain.user.model.*;

import java.util.UUID;

public interface UpdateUserUseCase {
    void changePassword(UUID userId, Password currentPassword, Password newPassword);
    User updatePhonenumber(UUID userId, PhoneNumber phoneNumber);


    User updateUsername(UUID userId, Username userName);
    User updateSurname(UUID userId, Surname fullName);
    User updateEmail(UUID userId, Email newEmail);



}
