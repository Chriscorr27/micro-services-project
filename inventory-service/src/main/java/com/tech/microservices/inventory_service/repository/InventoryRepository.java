package com.tech.microservices.inventory_service.repository;

import com.tech.microservices.inventory_service.entity.Inventory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface InventoryRepository extends JpaRepository<Inventory, Long> {

    boolean existsByProductIdAndQuantityGreaterThanEqual(String productId, Long quantity);

    Optional<Inventory> findByProductId(String productId);
}
