package itacademy.pawalert;


import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.persistence.autoconfigure.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;


@SpringBootApplication
@EntityScan(basePackages = "itacademy.pawalert.infrastructure.persistence")
@EnableJpaRepositories(basePackages = "itacademy.pawalert.infrastructure.persistence")
public class PawAlertApplication {

    static void main(String[] args) {
        SpringApplication.run(PawAlertApplication.class, args);

        System.out.println("Go!");
    }

}
