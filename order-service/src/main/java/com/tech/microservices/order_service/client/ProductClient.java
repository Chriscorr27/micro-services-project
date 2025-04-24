package com.tech.microservices.order_service.client;

import com.tech.microservices.dto.response.ProductResponse;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.service.annotation.GetExchange;

public interface ProductClient {
    @GetExchange("/api/product/{productId}")
    ProductResponse getProduct(@PathVariable String productId);
}

