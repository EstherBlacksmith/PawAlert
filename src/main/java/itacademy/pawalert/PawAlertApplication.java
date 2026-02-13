package itacademy.pawalert;

import itacademy.pawalert.domain.alert.model.Alert;
import itacademy.pawalert.domain.alert.model.Description;
import itacademy.pawalert.domain.alert.model.Title;
import itacademy.pawalert.domain.alert.model.UserId;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.persistence.autoconfigure.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

import java.util.UUID;


@SpringBootApplication
@EntityScan(basePackages = "itacademy.pawalert.infrastructure.persistence")
@EnableJpaRepositories(basePackages = "itacademy.pawalert.infrastructure.persistence")
public class PawAlertApplication {

    public static void main(String[] args) {
        SpringApplication.run(PawAlertApplication.class, args);
        UUID userId = UUID.randomUUID();
        Alert alert = new Alert(UUID.randomUUID(), userId, Title.of(("prueba")), Description.of("prueba desc"));
        System.out.println(alert);
        alert.seen();
        alert.seen();
        alert.closed();

    }

}
