package itacademy.pawalert.application.port.inbound;

import itacademy.pawalert.domain.user.model.Email;
import itacademy.pawalert.domain.user.model.Password;

public interface UpdatePasswordUseCase {
    void changePassword(Email email, Password currentPassword, Password newPassword);
}