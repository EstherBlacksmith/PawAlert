package itacademy.pawalert;

import itacademy.pawalert.domain.Alert;
import itacademy.pawalert.domain.Description;

import itacademy.pawalert.domain.Tittle;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class PawAlertApplication {

    public static void main(String[] args) {
        SpringApplication.run(PawAlertApplication.class, args);

        Alert alert = new Alert(new Tittle("prueba"),new Description("prueba desc" ));
        System.out.println(alert);
        alert.seen();
        alert.seen();
        alert.closed();
        alert.open();
        alert.closed();
    }

}
