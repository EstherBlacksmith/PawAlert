package itacademy.pawalert;

import itacademy.pawalert.domain.Alert;
import itacademy.pawalert.domain.Description;

import itacademy.pawalert.domain.Tittle;
import itacademy.pawalert.domain.UserId;
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
        UserId userId = new UserId(UUID.randomUUID().toString());
        Alert alert = new Alert(UUID.randomUUID(),userId,new Tittle("prueba"),new Description("prueba desc" ));
        System.out.println(alert);
        alert.seen();
        alert.seen();
        alert.closed();

    }

}
