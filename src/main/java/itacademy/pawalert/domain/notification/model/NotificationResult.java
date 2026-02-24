package itacademy.pawalert.domain.notification.model;

public sealed interface NotificationResult {

    record Success(String chatId) implements NotificationResult {}

    record Failure(String chatId, String errorMessage,
                  NotificationFailureReason reason)
            implements NotificationResult {}

    static NotificationResult success(String chatId) {
        return new Success(chatId);
    }

    static NotificationResult failure(String chatId, String errorMessage,
                                      NotificationFailureReason reason) {
        return new Failure(chatId, errorMessage, reason);
    }

    default boolean isSuccess() {
        return this instanceof Success;
    }

    default boolean isFailure() {
        return this instanceof Failure;
    }
}