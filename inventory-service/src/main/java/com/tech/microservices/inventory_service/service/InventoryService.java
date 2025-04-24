package com.tech.microservices.inventory_service.service;

import com.tech.microservices.dto.request.InventoryRequest;
import com.tech.microservices.exception.ApplicationException;
import com.tech.microservices.inventory_service.entity.Inventory;
import com.tech.microservices.inventory_service.repository.InventoryRepository;
import com.tech.microservices.type.InventoryActionType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class InventoryService {

    @Autowired
    InventoryRepository inventoryRepository;

    public boolean isInStock(String productId, Long quantity){
        return quantity>0 && inventoryRepository.existsByProductIdAndQuantityGreaterThanEqual(productId, quantity);
    }

    public void changeProductQuantity(InventoryRequest request) throws ApplicationException {
        if(InventoryActionType.ADD.toString().equals(request.getAction()) || isInStock(request.getProductId(), request.getQuantity())){
            Inventory inventory = inventoryRepository.findByProductId(request.getProductId())
                    .orElseThrow(()->new ApplicationException("Product is out of stock"));
            if(InventoryActionType.ADD.toString().equals(request.getAction())){
                inventory.setQuantity(inventory.getQuantity()+request.getQuantity());
            }else{
                inventory.setQuantity(inventory.getQuantity()-request.getQuantity());
            }
            inventoryRepository.save(inventory);
        }else{
            throw new ApplicationException("Product is out of stock.");
        }
    }
}
