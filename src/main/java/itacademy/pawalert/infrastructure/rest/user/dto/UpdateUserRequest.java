package itacademy.pawalert.infrastructure.rest.user.dto;


public record UpdateUserRequest(String newUsername,
                                String newSurname,
                                String newPhonenumber,
                                String newEmail,
                                boolean emailNotificationsEnabled,
                                boolean telegramNotificationsEnabled,
                                String telegramChatId) {
}
