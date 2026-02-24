# PawAlert Development Guide

This document provides essential information for agents working on the PawAlert project.

## Project Overview

- **Project Type**: Spring Boot 4.0.2 REST API with Java 21
- **Build Tool**: Maven
- **Architecture**: Hexagonal (Ports & Adapters) with domain-driven design patterns

---

## Build, Lint, and Test Commands

### Build Commands
```bash
# Compile the project
./mvnw compile

# Package the application
./mvnw package

# Run the application
./mvnw spring-boot:run

# Skip tests during build
./mvnw clean package -DskipTests
```

### Running Tests

```bash
# Run all unit tests
./mvnw test

# Run all tests including integration tests
./mvnw verify

# Run a specific test class
./mvnw test -Dtest=AlertServiceTest

# Run a specific test method
./mvnw test -Dtest=AlertServiceTest#shouldCreateAlertWithInitialEvent

# Run tests matching a pattern
./mvnw test -Dtest="*ServiceTest"

# Run tests with verbose output
./mvnw test -X

# Run integration tests only
./mvnw test -Dtest="*IntegrationTest"
```

### Code Quality

```bash
# No built-in linter - code uses standard Java conventions
# Run tests to verify code compiles and works
./mvnw compile test-compile
```

---

## Code Style Guidelines

### Project Structure

The project follows hexagonal architecture with clear layer separation:

```
src/main/java/itacademy/pawalert/
├── application/           # Application services and use cases
│   ├── alert/            # Alert-related use cases
│   │   ├── port/inbound/ # Use case interfaces
│   │   ├── port/outbound/# Repository port interfaces
│   │   └── service/     # Service implementations
│   ├── user/
│   └── metadata/
├── domain/               # Domain models and business logic
│   ├── alert/model/     # Domain entities
│   ├── alert/exception/ # Domain exceptions
│   ├── pet/
│   └── user/
├── infrastructure/      # Adapters and infrastructure
│   ├── rest/           # REST controllers and DTOs
│   ├── persistence/    # JPA repositories
│   ├── security/       # JWT, auth
│   ├── notifications/  # Notification services
│   └── image/          # Image handling
```

### Naming Conventions

- **Packages**: Lowercase, singular (e.g., `itacademy.pawalert.domain.alert`)
- **Classes**: PascalCase (e.g., `AlertService`, `AlertController`)
- **Interfaces**: PascalCase with meaningful names (e.g., `CreateAlertUseCase`)
- **Methods**: camelCase (e.g., `createOpenedAlert`, `getAlertById`)
- **Constants**: UPPER_SNAKE_CASE (e.g., `OPENED`, `StatusNames.OPENED`)
- **DTOs**: PascalCase, typically suffixed with DTO (e.g., `AlertDTO`, `CreateUserRequest`)
- **Tests**: ClassNameTest (e.g., `AlertServiceTest`)

### Package by Feature

Organize code by feature, not by technical layer:
- `application/alert/service/AlertService.java`
- `domain/alert/model/Alert.java`
- `infrastructure/rest/alert/controller/AlertController.java`

### Imports

Order imports according to the example:
1. Java/Spring imports
2. Project imports (grouped by package)
3. Static imports

```java
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import itacademy.pawalert.application.alert.port.inbound.*;
import itacademy.pawalert.domain.alert.model.*;
import itacademy.pawalert.infrastructure.rest.alert.dto.*;
```

### Class Structure

**Controllers** (`@RestController`):
- Use constructor injection
- Add `@Valid` for request body validation
- Include Swagger annotations (`@Operation`, `@ApiResponse`, `@Tag`)
- Use `@SecurityRequirement` for protected endpoints
- Log incoming requests with appropriate levels

```java
@RestController
@RequestMapping("/api/alerts")
@Tag(name = "Alerts", description = "Alert management endpoints")
public class AlertController {

    private static final Logger logger = LoggerFactory.getLogger(AlertController.class);

    private final CreateAlertUseCase createAlertUseCase;
    private final AlertMapper alertMapper;

    public AlertController(CreateAlertUseCase createAlertUseCase, AlertMapper alertMapper) {
        this.createAlertUseCase = createAlertUseCase;
        this.alertMapper = alertMapper;
    }

    @PostMapping
    @Operation(summary = "Create alert", description = "...")
    @SecurityRequirement(name = "Bearer JWT")
    public ResponseEntity<AlertDTO> createAlert(@Valid @RequestBody AlertDTO alertDTO) {
        logger.info("[API-CONTROLLER] Received alert creation request: petId={}", alertDTO.getPetId());
        // ...
    }
}
```

**Services** (`@Service`):
- Use `@Slf4j` for logging
- Implement UseCase interfaces for clean architecture
- Use constructor injection
- Mark transactional methods with `@Transactional`

```java
@Slf4j
@Service
public class AlertService implements CreateAlertUseCase, GetAlertUseCase {

    private final AlertRepositoryPort alertRepository;
    private final AlertEventRepositoryPort eventRepository;

    public AlertService(AlertRepositoryPort alertRepository, AlertEventRepositoryPort eventRepository) {
        this.alertRepository = alertRepository;
        this.eventRepository = eventRepository;
    }

    @Transactional
    public Alert createOpenedAlert(...) {
        log.info("[ALERT-CREATION] Starting to create opened alert...");
        // ...
    }
}
```

**Domain Models**:
- Use immutable objects where possible
- Value objects use static factory methods (e.g., `Title.of("text")`)
- Use `@Getter` from Lombok for simple models

```java
@Getter
public class Alert {
    private final UUID id;
    private final UUID petId;
    private final Title title;

    public Alert(UUID id, UUID petId, Title title) {
        this.id = id;
        this.petId = petId;
        this.title = title;
    }

    public static Alert create(...) { ... }
}
```

### DTO Conventions

- Use Java records for simple DTOs
- Use Jakarta validation annotations (`@NotNull`, `@NotBlank`, `@Size`)
- Include Swagger schema annotations for documentation

```java
public record AlertDTO(
    @Schema(description = "Alert unique identifier", example = "550e8400-e29b-41d4-a716-446655440000")
    String id,
    @NotBlank
    @Size(min = 3, max = 100)
    String title,
    // ...
) {}
```

### Value Objects

Create value object classes for domain primitives:
- `Title`, `Description` - text with validation
- `Email`, `PhoneNumber`, `Username` - formatted strings
- `GeographicLocation` - coordinates
- Use static factory method pattern: `Title.of("text")`

### Exception Handling

- Domain exceptions go in `domain/*/exception/`
- Application exceptions go in `application/exception/`
- Use custom exceptions extending `RuntimeException`
- Handle all exceptions in `GlobalExceptionHandler` with `@RestControllerAdvice`
- Return `ErrorResponse` with proper HTTP status codes

```java
public class AlertNotFoundException extends RuntimeException {
    public AlertNotFoundException(String message) {
        super(message);
    }
}
```

### Testing Conventions

**Unit Tests**:
- Use JUnit 5 with Mockito
- Use `@ExtendWith(MockitoExtension.class)`
- Use `@Mock` for dependencies, `@InjectMocks` for the service
- Follow Given-When-Then structure
- Use `@DisplayName` for descriptive test names
- Use `@Nested` for grouping related tests
- Use `TestAlertFactory` for creating test fixtures

```java
@ExtendWith(MockitoExtension.class)
@DisplayName("AlertService Unit Tests")
class AlertServiceTest {

    @Mock
    private AlertRepositoryPort alertRepository;

    @InjectMocks
    private AlertService alertService;

    @BeforeEach
    void setUp() {
        lenient().when(alertRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));
    }

    @Test
    @DisplayName("Should create alert with OPENED status")
    void shouldCreateAlertWithInitialEvent() {
        // Given
        Title title = Title.of("Test Alert");
        
        // When
        Alert result = alertService.createOpenedAlert(petId, title, ...);
        
        // Then
        assertNotNull(result);
        assertEquals(StatusNames.OPENED, result.currentStatus().getStatusName());
    }
}
```

**Integration Tests**:
- Extend `AbstractRabbitMQIntegrationTest` for message queue tests
- Use Testcontainers for external services (RabbitMQ)
- Use `@DirtiesContext` when needed

### Logging

- Use appropriate log levels:
  - `ERROR`: Actual errors/exceptions
  - `WARN`: Expected but concerning situations
  - `INFO`: Significant business events
  - `DEBUG`: Detailed flow information
- Include contextual information in log messages

```java
log.info("[ALERT-CREATION] Starting to create opened alert for petId={}, userId={}", petId, userId);
log.warn("[ALERT-CREATION] Pet already has active alert: petId={}", petId);
log.debug("Processing alert with status: {}", status);
```

### Security

- JWT authentication via `JwtAuthenticationFilter`
- Use `@PreAuthorize` for role-based access
- All password handling through `Password` value object
- Secrets stored in environment variables (use `.env` with dotenv-java)

### Database

- JPA with Hibernate
- PostgreSQL for production, H2 for tests
- Use entities in `infrastructure/persistence/`
- Use repository ports in `application/*/port/outbound/`

### API Documentation

- SpringDoc OpenAPI for Swagger UI at `/swagger-ui.html`
- OpenAPI 3.0 documentation at `/v3/api-docs`
- Use `@Operation`, `@ApiResponse`, `@Tag` annotations
- Document all parameters with `@Parameter`

### API Endpoints

The API is organized into the following endpoint groups:

#### Alert Management (`/api/alerts`)
- `GET /api/alerts/public/nearby` - Get nearby alerts within radius (public)
- `GET /api/alerts/public/active` - Get all active (non-closed) alerts (public)
- `POST /api/alerts` - Create a new alert (JWT required)
- `GET /api/alerts/{id}` - Get alert by ID
- `DELETE /api/alerts/{id}` - Delete alert by ID (JWT required)
- `POST /api/alerts/{id}/close` - Close alert with closure reason (JWT required)
- `PATCH /api/alerts/{id}/status` - Change alert status (excludes closing) (JWT required)
- `PUT /api/alerts/{alertId}/title` - Update alert title (JWT required)
- `PUT /api/alerts/{alertId}/description` - Update alert description (JWT required)
- `GET /api/alerts/search` - Search alerts with filters
- `GET /api/alerts` - Get all alerts
- `GET /api/alerts/{id}/events` - Get alert event history
- `GET /api/alerts/pets/{petId}/active` - Get active alert for a pet
- `GET /api/alerts/admin/all` - Get all alerts (ADMIN role required)

#### Alert Subscriptions (`/api/alerts`)
- `POST /api/alerts/{alertId}/subscribe` - Subscribe to alert notifications (JWT required)
- `DELETE /api/alerts/{alertId}/subscribe` - Unsubscribe from alert notifications (JWT required)
- `GET /api/alerts/{alertId}/subscribed` - Check if user is subscribed (JWT required)
- `GET /api/alerts/subscriptions/me` - Get all user's subscriptions (JWT required)

#### User Management (`/api/users`)
- `POST /api/users/register` - Register new user (public)
- `GET /api/users/by-username/{username}` - Get user by username
- `GET /api/users/by-email/{email}` - Get user by email
- `GET /api/users/{userId}` - Get user by ID
- `PUT /api/users/{userId}/change-password` - Change password (JWT required)
- `PUT /api/users/{userId}/change-username` - Update username (JWT required)
- `PUT /api/users/{userId}/change-surname` - Update surname (JWT required)
- `PUT /api/users/{userId}/change-phonenumber` - Update phone number (JWT required)
- `PUT /api/users/{userId}/change-email` - Update email (JWT required)
- `PUT /api/users/{userId}/email-notifications` - Toggle email notifications (JWT required)
- `PUT /api/users/{userId}/telegram-notifications` - Toggle Telegram notifications (JWT required)
- `PUT /api/users/{userId}/telegram-chat-id` - Update Telegram chat ID (JWT required)
- `DELETE /api/users/{userId}` - Delete user (JWT required)
- `GET /api/users/admin/all` - Get all users (ADMIN role required)
- `DELETE /api/users/admin/{id}` - Delete user by admin (ADMIN role required)
- `PUT /api/users/admin/{id}` - Update user by admin (ADMIN role required)

#### Authentication (`/api/auth`)
- `POST /api/auth/login` - User login (public, returns JWT token)

#### Pet Management (`/api/pets`)
- `POST /api/pets` - Create new pet for authenticated user (JWT required)
- `GET /api/pets/{id}` - Get pet by ID
- `PATCH /api/pets/{petId}` - Update pet information (JWT required)
- `DELETE /api/pets/{petId}` - Delete pet by ID (JWT required)
- `GET /api/pets/my-pets` - Get all pets for authenticated user (JWT required)
- `POST /api/pets/validate-image` - Validate pet image (JWT required)
- `GET /api/pets/admin/all` - Get all pets (ADMIN role required)
- `DELETE /api/pets/admin/{petId}` - Delete pet by admin (ADMIN role required)

#### Image Processing (`/api/images`)
- `POST /api/images/validate` - Validate image (JWT required)
- `POST /api/images/upload` - Upload image to Cloudinary (JWT required)
- `POST /api/images/analyze` - Analyze pet image using Google Vision (JWT required)
- `POST /api/images/classify` - Classify pet species (dog/cat/other) (JWT required)

#### Notifications (`/api/notifications`)
- `GET /api/notifications/stream` - SSE connection for real-time notifications (JWT required)
- `GET /api/notifications/connected-count` - Get number of connected SSE clients (JWT required)

#### Admin Management (`/api/admin`)
- `POST /api/admin/alerts/{alertId}/notify` - Relaunch alert notifications (ADMIN role required)

#### Metadata (`/api/v1/metadata`)
- `GET /api/v1/metadata/get-metadata` - Get system metadata (public)

---

## Key Dependencies

- Spring Boot 4.0.2
- Spring Security (JWT)
- Spring Data JPA
- Spring AMQP (RabbitMQ)
- PostgreSQL
- Lombok
- Jackson
- Testcontainers
- Awaitility (async testing)
- SpringDoc OpenAPI

---

## Configuration

Configuration is managed through:
- `application.properties` / `application.yml`
- Environment variables (via Docker Compose)
- `.env` file for local development (loaded by dotenv-java)
