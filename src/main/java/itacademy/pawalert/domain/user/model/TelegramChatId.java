package itacademy.pawalert.domain.user.model;

import com.fasterxml.jackson.annotation.JsonValue;

import java.util.regex.Pattern;


public record TelegramChatId(@JsonValue String value) {
    private static final Pattern TELEGRAM_ID_PATTERN =
            Pattern.compile("^-?[0-9]+$");

    public TelegramChatId {
        // Value can be null
        if (value != null && !value.isBlank()) {
            if (!TELEGRAM_ID_PATTERN.matcher(value).matches()) {
                throw new IllegalArgumentException(
                        "Invalid Telegram Chat ID format. Must be numeric (can start with -)");
            }

            // Validate minimum length (Telegram IDs are typically 9+ digits)
            if (value.length() < 5) {
                throw new IllegalArgumentException(
                        "Telegram Chat ID is too short (minimum 5 digits)");
            }
        }
    }

    // Factory method
    public static TelegramChatId of(String value) {
        if (value == null || value.isBlank()) {
            return null; // User hasn't linked Telegram yet
        }
        return new TelegramChatId(value);
    }

    public boolean isLinked() {
        return value != null && !value.isBlank();
    }
}
