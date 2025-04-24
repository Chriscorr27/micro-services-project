package com.tech.microservices.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class OrderRequest {
    @Positive(message = "Quantity should be positive value.")
    private int quantity;
    @NotBlank(message = "productId is required")
    private String productId;
}
