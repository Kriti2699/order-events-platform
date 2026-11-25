package com.kriti.orders.dto;

import com.kriti.orders.entity.Order;
import com.kriti.orders.entity.OrderStatus;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public record OrderResponse(UUID id, String customerEmail, BigDecimal amount,
                            OrderStatus status, Instant createdAt) {

    public static OrderResponse from(Order o) {
        return new OrderResponse(o.getId(), o.getCustomerEmail(), o.getAmount(),
                o.getStatus(), o.getCreatedAt());
    }
}
