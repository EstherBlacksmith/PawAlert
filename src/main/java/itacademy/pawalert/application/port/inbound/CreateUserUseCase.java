package itacademy.pawalert.application.port.inbound;

import itacademy.pawalert.domain.user.User;

public interface CreateUserUseCase {
    User register(String userNane,String fullname,String email,String phoneNumber,String password);
    boolean existsByUsername(String userName);
    boolean existsByEmail(String email);
}
