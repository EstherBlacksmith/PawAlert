package itacademy.pawalert.application.port.inbound;

import itacademy.pawalert.domain.user.model.Email;

public interface DeleteUserUseCase {
    void deleteByEmail(Email email);
}
