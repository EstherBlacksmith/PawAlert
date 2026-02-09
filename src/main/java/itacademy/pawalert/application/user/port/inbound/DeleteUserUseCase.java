package itacademy.pawalert.application.user.port.inbound;

import itacademy.pawalert.domain.user.model.Email;

public interface DeleteUserUseCase {
    void deleteByEmail(Email email);
}
