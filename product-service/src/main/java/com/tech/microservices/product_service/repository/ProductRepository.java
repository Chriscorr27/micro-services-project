package com.tech.microservices.product_service.repository;

import com.tech.microservices.product_service.entity.Product;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface ProductRepository extends MongoRepository<Product, String> {

//    public Optional<Product> findById(String id);
}
