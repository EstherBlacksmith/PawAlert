package itacademy.pawalert.application.port.inbound;


import java.util.UUID;

public interface DeleteAlertUseCase {
    void deleteAlertById(UUID alertId);
}
