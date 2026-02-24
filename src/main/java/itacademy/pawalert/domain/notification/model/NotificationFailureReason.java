package itacademy.pawalert.domain.notification.model;

import lombok.Getter;

@Getter
public enum NotificationFailureReason  {
    CHAT_NOT_FOUND("Chat not found"),
    BOT_BLOCKED("Bot blocked"),
    INVALID_CHAT_ID("Invalid chat Id"),
    NETWORK_ERROR("Network error"),
    UNKNOWN("Unknown");

    private final String value;

    NotificationFailureReason(String value) {
        this.value = value;
    }
}
