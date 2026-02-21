package itacademy.pawalert.application.alert.service;

import itacademy.pawalert.domain.alert.model.Alert;
import itacademy.pawalert.domain.alert.model.StatusNames;
import itacademy.pawalert.domain.pet.model.Pet;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

@Service
public class AlertNotificationFormatter {

    @Value("${app.frontend.url:http://localhost:5173}")
    private String frontendUrl;

    public AlertNotificationFormatter() {}

    public String formatStatusChangeMessage(Alert alert, Pet pet, StatusNames newStatus) {

        return String.format(
                "🔔 <b>Alert PawAlert - Status Update</b>\n\n" +
                        "🐕 Pet: <b>%s</b>\n" +
                        "📊 New status: %s\n\n" +
                        "🔗 View details: /alerts/%s",
                pet.getOfficialPetName(),
                newStatus,
                alert.getId()
        );
    }

    public String formatEmailSubject(StatusNames status) {
        return "🐾 PawAlert - Estado de Alerta Actualizado: " + getStatusDisplayName(status);
    }

    public String formatEmailBody(Alert alert, Pet pet, StatusNames oldStatus, StatusNames newStatus) {
        String petImageUrl = pet.getPetImage() != null ? pet.getPetImage().value() : null;
        String petName = pet.getOfficialPetName().value();
        String species = pet.getSpecies() != null ? pet.getSpecies().toString() : "Mascota";
        String breed = pet.getBreed() != null ? pet.getBreed().value() : "";
        String color = pet.getColor() != null ? pet.getColor().value() : "";
        String description = alert.getDescription() != null ? alert.getDescription().getValue() : "Descripción no disponible";
        String title = alert.getTitle() != null ? alert.getTitle().getValue() : "Alerta";
        String alertId = alert.getId().toString();
        String alertUrl = frontendUrl + "/alerts/" + alertId;
        String statusDisplayNew = getStatusDisplayName(newStatus);
        String statusDisplayOld = getStatusDisplayName(oldStatus);
        String timestamp = new SimpleDateFormat("dd/MM/yyyy HH:mm", new Locale("es", "ES")).format(new Date());
        
        // Build status badge color
        String statusColor = getStatusColor(newStatus);
        
        // Build pet description
        String petDescription = pet.getPetDescription() != null ? pet.getPetDescription().value() : "";
        
        StringBuilder details = new StringBuilder();
        details.append("<li><strong>📋 Estado anterior:</strong> ").append(statusDisplayOld).append("</li>");
        details.append("<li><strong>✅ Nuevo estado:</strong> <span style='color:").append(statusColor).append("'>").append(statusDisplayNew).append("</span></li>");
        
        if (species != null && !species.isEmpty()) {
            details.append("<li><strong>🐕 Especie:</strong> ").append(species).append("</li>");
        }
        if (breed != null && !breed.isEmpty()) {
            details.append("<li><strong>🐩 Raza:</strong> ").append(breed).append("</li>");
        }
        if (color != null && !color.isEmpty()) {
            details.append("<li><strong>🎨 Color:</strong> ").append(color).append("</li>");
        }
        if (description != null && !description.isEmpty()) {
            details.append("<li><strong>📝 Descripción:</strong> ").append(description).append("</li>");
        }
        if (petDescription != null && !petDescription.isEmpty()) {
            details.append("<li><strong>📋 Notas de la mascota:</strong> ").append(petDescription).append("</li>");
        }
        
        // Build image HTML
        String imageHtml = getPetImageHtml(petImageUrl, petName);
        
        return "<!DOCTYPE html>\n" +
                "<html>\n" +
                "<head>\n" +
                "    <meta charset='UTF-8'>\n" +
                "    <meta name='viewport' content='width=device-width, initial-scale=1.0'>\n" +
                "    <style>\n" +
                "        body { font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif; background-color: #f5f7fa; margin: 0; padding: 0; }\n" +
                "        .container { max-width: 600px; margin: 20px auto; background: #ffffff; border-radius: 16px; overflow: hidden; box-shadow: 0 10px 40px rgba(0,0,0,0.1); }\n" +
                "        .header { background: linear-gradient(135deg, #667eea 0%, #764ba2 100%); color: white; padding: 30px; text-align: center; }\n" +
                "        .header h1 { margin: 0; font-size: 28px; }\n" +
                "        .header p { margin: 10px 0 0; opacity: 0.9; font-size: 16px; }\n" +
                "        .content { padding: 30px; }\n" +
                "        .pet-card { background: #f8f9ff; border-radius: 12px; padding: 20px; margin-bottom: 20px; text-align: center; }\n" +
                "        .pet-name { font-size: 24px; font-weight: bold; color: #2d3748; margin: 10px 0; }\n" +
                "        .status-badge { display: inline-block; padding: 8px 20px; border-radius: 20px; font-weight: bold; font-size: 14px; margin: 10px 0; }\n" +
                "        .details { list-style: none; padding: 0; margin: 20px 0; }\n" +
                "        .details li { padding: 10px 0; border-bottom: 1px solid #e2e8f0; color: #4a5568; }\n" +
                "        .details li:last-child { border-bottom: none; }\n" +
                "        .button { display: inline-block; background: linear-gradient(135deg, #667eea 0%, #764ba2 100%); color: white; padding: 14px 30px; border-radius: 8px; text-decoration: none; font-weight: bold; margin: 20px 0; }\n" +
                "        .footer { background: #f7fafc; padding: 20px; text-align: center; color: #718096; font-size: 12px; }\n" +
                "    </style>\n" +
                "</head>\n" +
                "<body>\n" +
                "    <div class='container'>\n" +
                "        <div class='header'>\n" +
                "            <h1>🔔 Alerta de Mascota</h1>\n" +
                "            <p>El estado de la alerta ha cambiado</p>\n" +
                "        </div>\n" +
                "        <div class='content'>\n" +
                "            <div class='pet-card'>\n" +
                "                " + imageHtml + "\n" +
                "                <div class='pet-name'>" + petName + "</div>\n" +
                "                <div class='status-badge' style='background: " + statusColor + "; color: white;'>" + statusDisplayNew + "</div>\n" +
                "            </div>\n" +
                "            <ul class='details'>\n" +
                "                " + details.toString() + "\n" +
                "            </ul>\n" +
                "            <div style='text-align: center;'>\n" +
                "                <a href='" + alertUrl + "' class='button'>Ver Detalles de la Alerta</a>\n" +
                "            </div>\n" +
                "        </div>\n" +
                "        <div class='footer'>\n" +
                "            <p>🐾 PawAlert - Ayudando a encontrar mascotas perdidas</p>\n" +
                "            <p>Enviado el " + timestamp + "</p>\n" +
                "        </div>\n" +
                "    </div>\n" +
                "</body>\n" +
                "</html>";
    }
    
    private String getPetImageHtml(String petImageUrl, String petName) {
        if (petImageUrl == null || petImageUrl.isEmpty()) {
            return "<div style='background: linear-gradient(135deg, #667eea 0%, #764ba2 100%); width: 150px; height: 150px; border-radius: 12px; display: flex; align-items: center; justify-content: center; margin: 10px auto; font-size: 60px;'>🐾</div>";
        }
        
        // Check if it's a base64 image
        if (petImageUrl.startsWith("data:")) {
            // Use base64 directly
            return "<img src='" + petImageUrl + "' alt='Foto de " + petName + "' style='max-width: 200px; max-height: 200px; border-radius: 12px; margin: 10px 0; box-shadow: 0 4px 8px rgba(0,0,0,0.1);' />";
        }
        
        // Otherwise it's a URL (Cloudinary or other)
        return "<img src='" + petImageUrl + "' alt='Foto de " + petName + "' style='max-width: 200px; max-height: 200px; border-radius: 12px; margin: 10px 0; box-shadow: 0 4px 8px rgba(0,0,0,0.1);' />";
    }
    
    private String getStatusDisplayName(StatusNames status) {
        if (status == null) return "Desconocido";
        return switch (status) {
            case OPENED -> "🟢 Abierta";
            case SEEN -> "👀 Vista";
            case CLOSED -> "🔴 Cerrada";
            case SAFE -> "✅ Segura";
            default -> status.getDisplayName();
        };
    }
    
    private String getStatusColor(StatusNames status) {
        if (status == null) return "#718096";
        return switch (status) {
            case OPENED -> "#38a169";
            case SEEN -> "#3182ce";
            case CLOSED -> "#e53e3e";
            case SAFE -> "#d69e2e";
            default -> "#718096";
        };
    }
    
    public String formatTelegramMessage(Alert alert, Pet pet, StatusNames newStatus) {
        String petImageUrl = pet.getPetImage() != null ? pet.getPetImage().value() : null;
        String petName = pet.getOfficialPetName().value();
        String species = pet.getSpecies() != null ? pet.getSpecies().toString() : "Mascota";
        String description = alert.getDescription() != null ? alert.getDescription().getValue() : "";
        String alertId = alert.getId().toString();
        String alertUrl = frontendUrl + "/alerts/" + alertId;
        String statusDisplay = getStatusDisplayName(newStatus);
        String statusEmoji = getStatusEmoji(newStatus);
        
        // Check if image is base64
        boolean isBase64 = petImageUrl != null && petImageUrl.startsWith("data:");
        
        StringBuilder message = new StringBuilder();
        message.append("🔔 <b>NUEVA ACTUALIZACIÓN DE ALERTA</b>\n\n");
        
        // Only add photo link if it's a URL, not base64
        if (petImageUrl != null && !petImageUrl.isEmpty() && !isBase64) {
            message.append("📷 <a href=\"").append(petImageUrl).append("\">Ver foto de ").append(petName).append("</a>\n\n");
        }
        
        message.append("🐾 <b>").append(petName).append("</b>\n");
        message.append("━━━━━━━━━━━━━━━━━━━━━━\n");
        message.append("📋 <b>Estado:</b> ").append(statusEmoji).append(" ").append(statusDisplay).append("\n");
        
        if (species != null && !species.isEmpty()) {
            message.append("🐕 <b>Tipo:</b> ").append(species).append("\n");
        }
        
        if (description != null && !description.isEmpty()) {
            message.append("📝 <b>Descripción:</b> ").append(description).append("\n");
        }
        
        message.append("━━━━━━━━━━━━━━━━━━━━━━\n");
        message.append("🔗 <a href=\"").append(alertUrl).append("\">Ver detalles de la alerta</a>\n\n");
        message.append("<i>🐾 PawAlert - Ayudando a encontrar mascotas perdidas</i>");
        
        return message.toString();
    }
    
    private String getStatusEmoji(StatusNames status) {
        if (status == null) return "⚪";
        return switch (status) {
            case OPENED -> "🟢";
            case SEEN -> "👀";
            case CLOSED -> "🔴";
            case SAFE -> "✅";
            default -> "⚪";
        };
    }
}
