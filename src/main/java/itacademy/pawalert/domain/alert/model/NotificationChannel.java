package itacademy.pawalert.domain.alert.model;

import lombok.Getter;

@Getter
public enum NotificationChannel implements AlertDisplayableEnum {
    EMAIL("Email"),
    PUSH("Push"),
    SMS("Sms"),
    WHATSAPP("WhatsApp"),
    TELEGRAM("Telegram"),
    ALL("All");

    private final String value;

    NotificationChannel(String value) {
        this.value = value;
    }

    @Override
    public String getDisplayName() {
        return value;
    }
}