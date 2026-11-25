package com.kriti.orders.service;

import com.kriti.orders.dto.CreateOrderRequest;
import com.kriti.orders.entity.Order;
import com.kriti.orders.entity.OutboxEvent;
import com.kriti.orders.event.OrderCreatedEvent;
import com.kriti.orders.repository.OrderRepository;
import com.kriti.orders.repository.OutboxEventRepository;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.HttpStatus;

import java.util.UUID;

@Service
public class OrderService {

    private final OrderRepository orderRepository;
    private final OutboxEventRepository outboxRepository;
    private final ObjectMapper objectMapper;

    public OrderService(OrderRepository orderRepository,
                        OutboxEventRepository outboxRepository,
                        ObjectMapper objectMapper) {
        this.orderRepository = orderRepository;
        this.outboxRepository = outboxRepository;
        this.objectMapper = objectMapper;
    }

    /**
     * Transactional outbox: the order and its event are written in ONE database
     * transaction, so we can never save an order without eventually publishing
     * its event (and never publish an event for an order that was rolled back).
     */
    @Transactional
    public Order createOrder(CreateOrderRequest request) {
        Order order = orderRepository.save(new Order(request.customerEmail(), request.amount()));

        OrderCreatedEvent event = new OrderCreatedEvent(
                UUID.randomUUID(), order.getId(), order.getCustomerEmail(),
                order.getAmount(), order.getCreatedAt());

        outboxRepository.save(new OutboxEvent(
                event.eventId(), order.getId(), "order.created", toJson(event)));
        return order;
    }

    @Transactional(readOnly = true)
    public Order getOrder(UUID id) {
        return orderRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Order not found"));
    }

    private String toJson(OrderCreatedEvent event) {
        try {
            return objectMapper.writeValueAsString(event);
        } catch (JsonProcessingException e) {
            throw new IllegalStateException("Could not serialize event", e);
        }
    }
}
