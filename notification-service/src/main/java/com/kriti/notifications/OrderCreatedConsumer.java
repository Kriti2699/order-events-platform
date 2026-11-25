package com.kriti.notifications;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class OrderCreatedConsumer {

    private static final Logger log = LoggerFactory.getLogger(OrderCreatedConsumer.class);

    private final NotificationService notificationService;

    public OrderCreatedConsumer(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    @KafkaListener(topics = "order.created")
    public void onMessage(String payload) {
        log.debug("Received: {}", payload);
        notificationService.process(payload);
    }
}
