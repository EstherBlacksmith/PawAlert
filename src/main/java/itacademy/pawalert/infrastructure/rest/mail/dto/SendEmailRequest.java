package itacademy.pawalert.infrastructure.rest.mail.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SendEmailRequest {

    @NotBlank(message = "El destinatario es obligatorio")
    @Email(message = "Debe ser un email válido")
    private String to;

    @NotBlank(message = "El asunto es obligatorio")
    private String subject;

    @NotBlank(message = "El contenido es obligatorio")
    private String content;
}
