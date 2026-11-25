package com.kriti.notifications;

import jakarta.persistence.*;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "notifications")
public class Notification {

    @Id
    private UUID id;

    @Column(name = "order_id", nullable = false)
    private UUID orderId;

    @Column(nullable = false)
    private String recipient;

    @Column(nullable = false, columnDefinition = "text")
    private String message;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    protected Notification() {}

    public Notification(UUID orderId, String recipient, String message) {
        this.id = UUID.randomUUID();
        this.orderId = orderId;
        this.recipient = recipient;
        this.message = message;
        this.createdAt = Instant.now();
    }

    public UUID getId() { return id; }
    public UUID getOrderId() { return orderId; }
    public String getRecipient() { return recipient; }
    public String getMessage() { return message; }
    public Instant getCreatedAt() { return createdAt; }
}
