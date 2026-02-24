package itacademy.pawalert.infrastructure.messaging.telegram;

import org.springframework.amqp.core.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class TelegramQueueConfig {

    public static final String TELEGRAM_QUEUE = "telegram-notifications";
    public static final String TELEGRAM_DLQ = "telegram-notifications-dlq";
    public static final String TELEGRAM_DLX = "telegram-dlx";
    public static final String TELEGRAM_DLX_ROUTING_KEY = "telegram-notifications-dlq";

    @Bean
    public Queue telegramNotificationQueue() {
        return QueueBuilder.durable(TELEGRAM_QUEUE)
                .withArgument("x-dead-letter-exchange", TELEGRAM_DLX)
                .withArgument("x-dead-letter-routing-key", TELEGRAM_DLX_ROUTING_KEY)
                .build();
    }

    @Bean
    public Queue telegramDeadLetterQueue() {
        return QueueBuilder.durable(TELEGRAM_DLQ).build();
    }

    @Bean
    public DirectExchange telegramDeadLetterExchange() {
        return new DirectExchange(TELEGRAM_DLX);
    }

    @Bean
    public Binding telegramDlqBinding(Queue telegramDeadLetterQueue,
                                      DirectExchange telegramDeadLetterExchange) {
        return BindingBuilder.bind(telegramDeadLetterQueue)
                .to(telegramDeadLetterExchange)
                .with(TELEGRAM_DLX_ROUTING_KEY);
    }
}