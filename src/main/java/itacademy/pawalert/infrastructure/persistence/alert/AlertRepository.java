package itacademy.pawalert.infrastructure.persistence.alert;

import itacademy.pawalert.domain.alert.model.Alert;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;

public interface AlertRepository extends JpaRepository<AlertEntity, String>,
        JpaSpecificationExecutor<Alert> {
    List<AlertEntity> findByStatus(String status);

    List<AlertEntity> findAllByPetId(String petID);
}
