package itacademy.pawalert.application.alert.port.inbound;


import java.util.UUID;

public interface DeleteAlertUseCase {
    void deleteAlertById(UUID alertId);
}
