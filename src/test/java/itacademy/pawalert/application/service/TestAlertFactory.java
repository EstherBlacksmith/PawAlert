package itacademy.pawalert.application.service;

import itacademy.pawalert.domain.*;

import java.util.UUID;

/**
 * Factory para crear instancias de Alert específicamente para testing.
 * <p>
 * Este factory proporciona métodos convenientes para crear Alerts con
 * diferentes estados (OPENED, SEEN, SAFE, CLOSED) respetando las
 * transiciones de estado válidas del dominio.
 * <p>
 * Principios aplicados:
 * - Factory Method: Métodos estáticos para creación
 * - Builder Pattern: API fluida para máxima flexibilidad
 * - DDD: Respeta invariantes y transiciones de estado
 */
public final class TestAlertFactory {

    // Constructor privado para evitar instanciación
    private TestAlertFactory() {
        throw new UnsupportedOperationException("Esta es una clase de utilidad, no instanciable");
    }

    // ═══════════════════════════════════════════════════════════════════════════
    // Métodos de conveniencia - Crean Alerts con estados específicos
    // ═══════════════════════════════════════════════════════════════════════════

    /**
     * Crea un Alert en estado OPENED (inicial).
     *
     * @param id    ID del alert
     * @param petId ID de la mascota
     * @return Alert en estado OPENED
     */
    public static Alert createOpenedAlert(UUID id, UUID petId, UserId userId) {
        // Estado inicial por defecto es OPENED, no necesita transición
        return new Alert(
                id,
                petId,
                new UserId(userId.toString()),
                new Tittle("Test Alert - " + id.toString().substring(0, 8)),
                new Description("Test alert created for testing purposes")
        );
    }

    /**
     * Crea un Alert en estado SEEN.
     * Transición: OPENED → SEEN
     *
     * @param id    ID del alert
     * @param petId ID de la mascota
     * @return Alert en estado SEEN
     */
    public static Alert createSeenAlert(UUID id, UUID petId, UserId userId) {
        Alert alert = createOpenedAlert(id, petId, userId);
        alert.seen();
        return alert;
    }

    /**
     * Crea un Alert en estado SAFE.
     * Transición: OPENED → SEEN → SAFE
     *
     * @param id    ID del alert
     * @param petId ID de la mascota
     * @return Alert en estado SAFE
     */
    public static Alert createSafeAlert(UUID id, UUID petId, UserId userId) {
        Alert alert = createSeenAlert(id, petId, userId);
        alert.safe();
        return alert;
    }

    /**
     * Crea un Alert en estado CLOSED.
     * Transición: OPENED → SEEN → SAFE → CLOSED
     *
     * @param id    ID del alert
     * @param petId ID de la mascota
     * @return Alert en estado CLOSED
     */
    public static Alert createClosedAlert(UUID id, UUID petId, UserId userId) {
        Alert alert = createSafeAlert(id, petId, userId);
        alert.closed();
        return alert;
    }

    // ═══════════════════════════════════════════════════════════════════════════
    // Builder Pattern - Para máxima flexibilidad en tests
    // ═══════════════════════════════════════════════════════════════════════════

    /**
     * Inicia la construcción de un Alert personalizado.
     * <p>
     * Uso típico:
     * <pre>
     * Alert alert = TestAlertFactory.builder()
     *     .id(UUID.randomUUID())
     *     .petId(petId)
     *     .title("Mi Alert de Test")
     *     .description("Descripción personalizada")
     *     .status(StatusNames.SEEN)
     *     .build();
     * </pre>
     *
     * @return Builder configurado para crear Alerts
     */
    public static AlertBuilder builder() {
        return new AlertBuilder();
    }

    /**
     * Builder para crear Alerts con configuración personalizada.
     * <p>
     * Ejemplo de uso:
     * <pre>
     * Alert alert = TestAlertFactory.builder()
     *     .id(alertId)
     *     .petId(petId)
     *     .title("Alert de prueba")
     *     .description("Descripción")
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

        // Constructor package-private
        AlertBuilder() {
        }

        /**
         * Define el ID del Alert.
         *
         * @param id UUID del alert
         * @return this (para encadenamiento)
         */
        public AlertBuilder id(UUID id) {
            this.id = id;
            return this;
        }

        /**
         * Define el ID de la mascota.
         *
         * @param petId UUID de la mascota
         * @return this (para encadenamiento)
         */
        public AlertBuilder petId(UUID petId) {
            this.petId = petId;
            return this;
        }

        /**
         * Define el título del Alert.
         *
         * @param title Título del alert
         * @return this (para encadenamiento)
         */
        public AlertBuilder title(String title) {
            this.title = title;
            return this;
        }

        /**
         * Define la descripción del Alert.
         *
         * @param description Descripción del alert
         * @return this (para encadenamiento)
         */
        public AlertBuilder description(String description) {
            this.description = description;
            return this;
        }

        /**
         * Define el estado del Alert.
         * El builder会自动处理 las transiciones necesarias:
         * - OPENED: sin transiciones
         * - SEEN: OPENED → SEEN
         * - SAFE: OPENED → SEEN → SAFE
         * - CLOSED: OPENED → SEEN → SAFE → CLOSED
         *
         * @param status Estado deseado del alert
         * @return this (para encadenamiento)
         */
        public AlertBuilder status(StatusNames status) {
            this.status = status;
            return this;
        }

        /**
         * Construye y retorna el Alert con la configuración especificada.
         *
         * @return Alert listo para usar en tests
         * @throws IllegalStateException si la configuración es inválida
         */
        public Alert build() {
            // Validación básica
            if (id == null) {
                throw new IllegalStateException("El ID del alert no puede ser null");
            }
            if (petId == null) {
                throw new IllegalStateException("El petId no puede ser null");
            }

            if (userId == null) {
                throw new IllegalStateException("The userId can't be null");
            }

            // Crear el Alert base
            Alert alert = new Alert(
                    id,
                    petId,
                    new UserId(userId.toString()),
                    new Tittle(title),
                    new Description(description)
            );

            // Aplicar transiciones de estado necesarias
            // Esto asegura que siempre sigamos las transiciones válidas del dominio
            switch (status) {
                case OPENED:
                    // Estado inicial, no necesita transición
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
                    throw new IllegalArgumentException("Estado no reconocido: " + status);
            }

            return alert;
        }
    }
}