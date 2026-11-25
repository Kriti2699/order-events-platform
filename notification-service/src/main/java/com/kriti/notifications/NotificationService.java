package com.kriti.notifications;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class NotificationService {

    private static final Logger log = LoggerFactory.getLogger(NotificationService.class);

    private final NotificationRepository notificationRepository;
    private final ProcessedEventRepository processedEventRepository;
    private final ObjectMapper objectMapper;

    public NotificationService(NotificationRepository notificationRepository,
                               ProcessedEventRepository processedEventRepository,
                               ObjectMapper objectMapper) {
        this.notificationRepository = notificationRepository;
        this.processedEventRepository = processedEventRepository;
        this.objectMapper = objectMapper;
    }

    /**
     * Idempotent processing: the "already processed?" marker and the business
     * write happen in the same transaction. Kafka delivers at-least-once, so the
     * same event can arrive twice; the second time we simply skip it.
     */
    @Transactional
    public void process(String payload) {
        OrderCreatedEvent event = parse(payload);

        if (processedEventRepository.existsById(event.eventId())) {
            log.info("Duplicate event {} ignored", event.eventId());
            return;
        }

        String message = "Hi! Your order " + event.orderId() + " for amount "
                + event.amount() + " has been received.";
        notificationRepository.save(new Notification(event.orderId(), event.customerEmail(), message));
        processedEventRepository.save(new ProcessedEvent(event.eventId()));

        // Real system: send email/SMS here (e.g. AWS SES / SNS).
        log.info("Notification sent to {} for order {}", event.customerEmail(), event.orderId());
    }

    private OrderCreatedEvent parse(String payload) {
        try {
            OrderCreatedEvent event = objectMapper.readValue(payload, OrderCreatedEvent.class);
            if (event.eventId() == null || event.orderId() == null || event.customerEmail() == null) {
                throw new IllegalArgumentException("Event is missing required fields");
            }
            return event;
        } catch (JsonProcessingException e) {
            throw new IllegalArgumentException("Malformed event payload", e);
        }
    }
}
