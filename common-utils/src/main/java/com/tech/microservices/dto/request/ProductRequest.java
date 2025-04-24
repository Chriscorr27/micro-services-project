package com.tech.microservices.dto.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class ProductRequest {
    @NotNull(message = "Name is required")
    private String name;
    @NotNull(message = "desc is required")
    private String desc;
    @Positive(message = "Price should a positive value.")
    private double price;
}
