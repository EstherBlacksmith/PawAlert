package itacademy.pawalert.application.port.outbound;

import itacademy.pawalert.domain.alert.model.Alert;
import java.util.List;
import java.util.Optional;

public interface AlertRepositoryPort {

    Alert save(Alert alert);

    Optional<Alert> findById(String alertId);

    List<Alert> findAllByPetId(String petId);

    boolean existsById(String alertId);
}
