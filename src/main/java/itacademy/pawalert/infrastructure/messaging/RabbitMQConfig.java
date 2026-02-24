package itacademy.pawalert.infrastructure.messaging;

import org.springframework.amqp.core.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

    // Queue Names
    public static final String TELEGRAM_NOTIFICATION_QUEUE = "telegram-notifications";
    public static final String TELEGRAM_DLQ = "telegram-notifications-dlq";
    public static final String DLX_EXCHANGE = "telegram-dlx";
    public static final String DLX_ROUTING_KEY = "telegram-notifications-dlq";

      // Main Queue with DLX configuration
    @Bean
    public Queue telegramNotificationQueue() {
        return QueueBuilder.durable(TELEGRAM_NOTIFICATION_QUEUE)
                .withArgument("x-dead-letter-exchange", DLX_EXCHANGE)
                .withArgument("x-dead-letter-routing-key", DLX_ROUTING_KEY)
                .build();
    }

    // Dead Letter Queue
    @Bean
    public Queue telegramDeadLetterQueue() {
        return QueueBuilder.durable(TELEGRAM_DLQ).build();
    }

    // Dead Letter Exchange
    @Bean
    public DirectExchange deadLetterExchange() {
        return new DirectExchange(DLX_EXCHANGE);
    }

    // Binding: DeadLetterQueue to deadLetterExchange
    @Bean
    public Binding dlqBinding(Queue telegramDeadLetterQueue, DirectExchange deadLetterExchange) {
        return BindingBuilder.bind(telegramDeadLetterQueue)
                .to(deadLetterExchange)
                .with(DLX_ROUTING_KEY);
    }
}
