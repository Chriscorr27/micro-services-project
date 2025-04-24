package com.tech.microservices.order_service.repository;

import com.tech.microservices.order_service.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface OrderRepository extends JpaRepository<Order, Long> {

    Optional<Order> findByOrderId(String orderId);

    List<Order> findAllByUserId(Long userId);
}
