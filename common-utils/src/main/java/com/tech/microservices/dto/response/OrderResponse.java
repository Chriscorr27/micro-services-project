package com.tech.microservices.dto.response;

import com.tech.microservices.type.OrderStatusType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class OrderResponse {
    private Long id;
    private String orderId;
    private String status;
    private int quantity;
    private double price;
    private double totalPrice;
    private ProductResponse product;
    private UserResponse user;
}
