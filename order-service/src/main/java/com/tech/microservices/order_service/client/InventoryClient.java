package com.tech.microservices.order_service.client;

import com.tech.microservices.dto.request.InventoryRequest;
import com.tech.microservices.dto.response.MessageResponse;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.service.annotation.GetExchange;
import org.springframework.web.service.annotation.PutExchange;

public interface InventoryClient {
    @GetExchange("/api/internal-inventory/check-product-in-stock")
    boolean checkProductInStock(@RequestParam String productId, @RequestParam Long quantity);

    @PutExchange("/api/internal-inventory/change-product-quantity")
    MessageResponse changeProductQuantity(@RequestBody InventoryRequest request);
}
