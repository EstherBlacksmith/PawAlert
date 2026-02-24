package itacademy.pawalert.infrastructure.notificationqueues;

import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.RabbitMQContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

/**
 * Base class for RabbitMQ integration tests using Testcontainers.
 * Provides a reusable RabbitMQ container configuration for all integration tests.
 */
@Testcontainers
@SpringBootTest
@ActiveProfiles("integration-test")
public abstract class AbstractRabbitMQIntegrationTest {

    @Container
    protected static final RabbitMQContainer rabbitMQ = 
        new RabbitMQContainer(DockerImageName.parse("rabbitmq:3.12-management"))
            .withExposedPorts(5672, 15672);

    @DynamicPropertySource
    static void configureRabbitMQ(DynamicPropertyRegistry registry) {
        registry.add("spring.rabbitmq.host", rabbitMQ::getHost);
        registry.add("spring.rabbitmq.port", rabbitMQ::getAmqpPort);
        registry.add("spring.rabbitmq.username", rabbitMQ::getAdminUsername);
        registry.add("spring.rabbitmq.password", rabbitMQ::getAdminPassword);
    }
}