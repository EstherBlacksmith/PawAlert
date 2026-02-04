package itacademy.pawalert.application.port.inbound;

import itacademy.pawalert.domain.user.User;

public interface UpdateUserUseCase {
    User updateUsername(String email, String userName);
    User updateFullname(String email, String fullName);
}
