package com.tech.microservices.product_service.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
@Document(value = "products")
public class Product {

    @Id
    private String id;
    private String name;
    private String desc;
    private double price;
    private Long ownerId;
}
