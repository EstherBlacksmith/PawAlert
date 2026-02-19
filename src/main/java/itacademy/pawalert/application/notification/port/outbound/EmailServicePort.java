package itacademy.pawalert.application.notification.port.outbound;



public interface EmailServicePort {
    void sendToUser(String email, String subject, String body);
}
