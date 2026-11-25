package com.kriti.orders.repository;

import com.kriti.orders.entity.Order;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.UUID;

public interface OrderRepository extends JpaRepository<Order, UUID> {
}
