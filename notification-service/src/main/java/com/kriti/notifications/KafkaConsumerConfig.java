package com.kriti.notifications;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.listener.DeadLetterPublishingRecoverer;
import org.springframework.kafka.listener.DefaultErrorHandler;
import org.springframework.util.backoff.FixedBackOff;

@Configuration
public class KafkaConsumerConfig {

    /**
     * Retry a failing message 3 times (1s apart). If it still fails, publish it to
     * "order.created.DLT" (dead-letter topic) so one poison message cannot block
     * the partition. Spring Boot wires this handler into the listener container.
     */
    @Bean
    public DefaultErrorHandler errorHandler(KafkaTemplate<String, String> template) {
        DeadLetterPublishingRecoverer recoverer = new DeadLetterPublishingRecoverer(template);
        DefaultErrorHandler handler = new DefaultErrorHandler(recoverer, new FixedBackOff(1000L, 3));
        handler.addNotRetryableExceptions(IllegalArgumentException.class); // bad payload: retrying won't help
        return handler;
    }
}
