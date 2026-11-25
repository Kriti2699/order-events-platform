package com.kriti.notifications;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@JsonIgnoreProperties(ignoreUnknown = true)
public record OrderCreatedEvent(
        UUID eventId,
        UUID orderId,
        String customerEmail,
        BigDecimal amount,
        Instant createdAt) {
}
