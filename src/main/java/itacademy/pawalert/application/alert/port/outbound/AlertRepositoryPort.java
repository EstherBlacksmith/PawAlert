package itacademy.pawalert.application.alert.port.outbound;

import itacademy.pawalert.domain.alert.model.Alert;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Component
public interface AlertRepositoryPort {
    Alert save(Alert alert);
    Optional<Alert> findById(UUID alertId);
    List<Alert> findAllByPetId(UUID petId);
    boolean existsById(UUID alertId);
    void deleteById(UUID alertId);

    List<Alert> findAll();

    //For the Specifications
    List<Alert> findAll(Specification<Alert> spec);
}
