package com.kriti.orders.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "orders")
public class Order {

    @Id
    private UUID id;

    @Column(name = "customer_email", nullable = false)
    private String customerEmail;

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal amount;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private OrderStatus status;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    protected Order() {}

    public Order(String customerEmail, BigDecimal amount) {
        this.id = UUID.randomUUID();
        this.customerEmail = customerEmail;
        this.amount = amount;
        this.status = OrderStatus.CREATED;
        this.createdAt = Instant.now();
    }

    public UUID getId() { return id; }
    public String getCustomerEmail() { return customerEmail; }
    public BigDecimal getAmount() { return amount; }
    public OrderStatus getStatus() { return status; }
    public Instant getCreatedAt() { return createdAt; }
}
