package com.tech.microservices.inventory_service.controller;

import com.tech.microservices.dto.request.InventoryRequest;
import com.tech.microservices.dto.response.MessageResponse;
import com.tech.microservices.exception.ApplicationException;
import com.tech.microservices.inventory_service.service.InventoryService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/internal-inventory")
public class InternalInventoryController {

    @Autowired
    InventoryService inventoryService;

    @GetMapping("/check-product-in-stock")
    @ResponseStatus(HttpStatus.OK)
    public boolean checkProductInStock(@RequestParam String productId, @RequestParam Long quantity){
        return inventoryService.isInStock(productId, quantity);
    }

    @PutMapping("/change-product-quantity")
    @ResponseStatus(HttpStatus.OK)
    public MessageResponse changeProductQuantity(@Valid @RequestBody InventoryRequest request) throws ApplicationException {
        inventoryService.changeProductQuantity(request);
        return new MessageResponse("Successfully change product quantity");
    }
}
