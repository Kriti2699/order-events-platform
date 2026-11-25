package com.kriti.orders.event;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

/** Event contract published to the order.created topic. */
public record OrderCreatedEvent(
        UUID eventId,
        UUID orderId,
        String customerEmail,
        BigDecimal amount,
        Instant createdAt) {
}
