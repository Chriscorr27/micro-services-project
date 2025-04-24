package com.tech.microservices.notification_service.client;

import com.tech.microservices.dto.response.OrderResponse;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.service.annotation.GetExchange;

public interface OrderClient {
    @GetExchange("/api/order/{orderId}")
    public OrderResponse getOrderById(@PathVariable String orderId);
}
