package itacademy.pawalert.application.service;

import itacademy.pawalert.domain.alert.model.*;

import java.util.UUID;

/**
 * Factory to create Alert instances specifically for testing.
 * <p>
 * This factory provides convenient methods to create Alerts with
 * different states (OPENED, SEEN, SAFE, CLOSED) respecting the
 * valid state transitions of the domain.
 * <p>
 * Applied principles:
 * - Factory Method: Static methods for creation
 * - Builder Pattern: Fluent API for maximum flexibility
 * - DDD: Respects invariants and state transitions
 */
public final class TestAlertFactory {

    // Private constructor to prevent instantiation
    private TestAlertFactory() {
        throw new UnsupportedOperationException("This is a utility class, not instantiable");
    }

    // ═══════════════════════════════════════════════════════════════════════════
    // Convenience Methods - Create Alerts with specific states
    // ═══════════════════════════════════════════════════════════════════════════

    /**
     * Creates an Alert in OPENED state (initial).
     *
     * @param id    ID of the alert
     * @param petId ID of the pet
     * @return Alert in OPENED state
     */
    public static Alert createOpenedAlert(UUID id, UUID petId, UserId userId) {
        // Initial state is OPENED by default, no transition needed
        return new Alert(
                id,
                petId,
                new UserId(userId.toString()),
                new Title("Test Alert - " + id.toString().substring(0, 8)),
                new Description("Test alert created for testing purposes")
        );
    }



    /**
     * Creates an Alert in SEEN state.
     * Transition: OPENED → SEEN
     *
     * @param id    ID of the alert
     * @param petId ID of the pet
     * @return Alert in SEEN state
     */
    public static Alert createSeenAlert(UUID id, UUID petId, UserId userId) {
        Alert alert = createOpenedAlert(id, petId, userId);
        alert.seen();
        return alert;
    }

    /**
     * Creates an Alert in SAFE state.
     * Transition: OPENED → SEEN → SAFE
     *
     * @param id    ID of the alert
     * @param petId ID of the pet
     * @return Alert in SAFE state
     */
    public static Alert createSafeAlert(UUID id, UUID petId, UserId userId) {
        Alert alert = createSeenAlert(id, petId, userId);
        alert.safe();
        return alert;
    }

    /**
     * Creates an Alert in CLOSED state.
     * Transition: OPENED → SEEN → SAFE → CLOSED
     *
     * @param id    ID of the alert
     * @param petId ID of the pet
     * @return Alert in CLOSED state
     */
    public static Alert createClosedAlert(UUID id, UUID petId, UserId userId) {
        Alert alert = createSafeAlert(id, petId, userId);
        alert.closed();
        return alert;
    }

    // ═══════════════════════════════════════════════════════════════════════════
    // Builder Pattern - For maximum flexibility in tests
    // ═══════════════════════════════════════════════════════════════════════════

    /**
     * Starts building a custom Alert.
     * <p>
     * Typical usage:
     * <pre>
     * Alert alert = TestAlertFactory.builder()
     *     .id(UUID.randomUUID())
     *     .petId(petId)
     *     .title("My Test Alert")
     *     .description("Custom description")
     *     .status(StatusNames.SEEN)
     *     .build();
     * </pre>
     *
     * @return Builder configured to create Alerts
     */
    public static AlertBuilder builder() {
        return new AlertBuilder();
    }

    public static Alert createModificableAlert(String alertId, String creatorId, String originalTitle, String description) {
        return new Alert(
                UUID.fromString(alertId),
                UUID.randomUUID(),
                new UserId(creatorId),
                new Title(originalTitle),
                new Description(description)
        );

    }

    /**
     * Builder to create Alerts with custom configuration.
     * <p>
     * Usage example:
     * <pre>
     * Alert alert = TestAlertFactory.builder()
     *     .id(alertId)
     *     .petId(petId)
     *     .title("Test Alert")
     *     .description("Description")
     *     .status(StatusNames.OPENED)
     *     .build();
     * </pre>
     */
    public static final class AlertBuilder {

        private UUID id = UUID.randomUUID();
        private UUID petId = UUID.randomUUID();
        private final UUID userId = UUID.randomUUID();
        private String title = "Test Alert";
        private String description = "Test Description";
        private StatusNames status = StatusNames.OPENED;

        // Package-private constructor
        AlertBuilder() {
        }

        /**
         * Sets the Alert ID.
         *
         * @param id UUID of the alert
         * @return this (for chaining)
         */
        public AlertBuilder id(UUID id) {
            this.id = id;
            return this;
        }

        /**
         * Sets the pet ID.
         *
         * @param petId UUID of the pet
         * @return this (for chaining)
         */
        public AlertBuilder petId(UUID petId) {
            this.petId = petId;
            return this;
        }

        /**
         * Sets the Alert title.
         *
         * @param title Title of the alert
         * @return this (for chaining)
         */
        public AlertBuilder title(String title) {
            this.title = title;
            return this;
        }

        /**
         * Sets the Alert description.
         *
         * @param description Description of the alert
         * @return this (for chaining)
         */
        public AlertBuilder description(String description) {
            this.description = description;
            return this;
        }

        /**
         * Sets the Alert state.
         * The builder will automatically handle the necessary transitions:
         * - OPENED: no transitions
         * - SEEN: OPENED → SEEN
         * - SAFE: OPENED → SEEN → SAFE
         * - CLOSED: OPENED → SEEN → SAFE → CLOSED
         *
         * @param status Desired state of the alert
         * @return this (for chaining)
         */
        public AlertBuilder status(StatusNames status) {
            this.status = status;
            return this;
        }

        /**
         * Builds and returns the Alert with the specified configuration.
         *
         * @return Alert ready to use in tests
         * @throws IllegalStateException if configuration is invalid
         */
        public Alert build() {
            // Basic validation
            if (id == null) {
                throw new IllegalStateException("Alert ID cannot be null");
            }
            if (petId == null) {
                throw new IllegalStateException("Pet ID cannot be null");
            }

            if (userId == null) {
                throw new IllegalStateException("User ID cannot be null");
            }

            // Create the base Alert
            Alert alert = new Alert(
                    id,
                    petId,
                    new UserId(userId.toString()),
                    new Title(title),
                    new Description(description)
            );

            // Apply necessary state transitions
            // This ensures we always follow valid domain transitions
            switch (status) {
                case OPENED:
                    // Initial state, no transition needed
                    break;
                case SEEN:
                    alert.seen();
                    break;
                case SAFE:
                    alert.seen();
                    alert.safe();
                    break;
                case CLOSED:
                    alert.seen();
                    alert.safe();
                    alert.closed();
                    break;
                default:
                    throw new IllegalArgumentException("Unrecognized state: " + status);
            }

            return alert;
        }
    }
}
