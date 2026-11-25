package com.kriti.orders.messaging;

import com.kriti.orders.entity.OutboxEvent;
import com.kriti.orders.repository.OutboxEventRepository;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * Polls the outbox table and relays events to Kafka. Delivery is at-least-once:
 * if the app crashes after sending but before marking published, the event is
 * re-sent, so consumers must be idempotent (the notification-service is).
 */
@Component
public class OutboxPublisher {

    private static final Logger log = LoggerFactory.getLogger(OutboxPublisher.class);

    private final OutboxEventRepository repository;
    private final KafkaTemplate<String, String> kafkaTemplate;
    private final String topic;

    public OutboxPublisher(OutboxEventRepository repository,
                           KafkaTemplate<String, String> kafkaTemplate,
                           @Value("${app.topics.order-created}") String topic) {
        this.repository = repository;
        this.kafkaTemplate = kafkaTemplate;
        this.topic = topic;
    }

    @Scheduled(fixedDelay = 1000)
    @Transactional
    public void publishPending() {
        List<OutboxEvent> pending = repository.findTop50ByPublishedFalseOrderByCreatedAtAsc();
        for (OutboxEvent event : pending) {
            try {
                // key = orderId keeps all events for one order on the same partition (ordering)
                kafkaTemplate.send(topic, event.getAggregateId().toString(), event.getPayload())
                        .get(5, TimeUnit.SECONDS);
                event.markPublished();
                log.info("Published event {} to {}", event.getId(), topic);
            } catch (Exception e) {
                log.warn("Failed to publish event {}, will retry: {}", event.getId(), e.getMessage());
                break; // keep ordering; retry on next tick
            }
        }
    }
}
