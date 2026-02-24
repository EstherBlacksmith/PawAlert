package itacademy.pawalert.infrastructure.messaging.email;

import org.springframework.amqp.core.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


@Configuration
public class EmailQueueConfig {

    public static final String EMAIL_QUEUE = "email-notifications";
    public static final String EMAIL_DLQ = "email-notifications-dlq";
    public static final String EMAIL_DLX = "email-dlx";
    public static final String EMAIL_DLX_ROUTING_KEY = "email-notifications-dlq";

    @Bean
    public Queue emailNotificationQueue() {
        return QueueBuilder.durable(EMAIL_QUEUE)
                .withArgument("x-dead-letter-exchange", EMAIL_DLX)
                .withArgument("x-dead-letter-routing-key", EMAIL_DLX_ROUTING_KEY)
                .build();
    }

    @Bean
    public Queue emailDeadLetterQueue() {
        return QueueBuilder.durable(EMAIL_DLQ).build();
    }

    @Bean
    public DirectExchange emailDeadLetterExchange() {
        return new DirectExchange(EMAIL_DLX);
    }

    @Bean
    public Binding emailDlqBinding(Queue emailDeadLetterQueue,
                                   DirectExchange emailDeadLetterExchange) {
        return BindingBuilder.bind(emailDeadLetterQueue)
                .to(emailDeadLetterExchange)
                .with(EMAIL_DLX_ROUTING_KEY);
    }
}
