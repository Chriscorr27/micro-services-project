package com.tech.microservices.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class InventoryRequest {
    @NotNull(message = "productId is required")
    private String productId;
    @Positive(message = "quantity should a positive value.")
    private Long quantity;
    @NotBlank(message = "action is required")
    @Pattern(
            regexp = "^(ADD|SUBTRACT)$",
            message = "action must be either ADD or SUBTRACT"
    )
    private String action;
}
