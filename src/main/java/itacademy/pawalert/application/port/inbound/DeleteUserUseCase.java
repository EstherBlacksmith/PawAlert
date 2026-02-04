package itacademy.pawalert.application.port.inbound;

public interface DeleteUserUseCase {
    void deleteByEmail(String email);
}
