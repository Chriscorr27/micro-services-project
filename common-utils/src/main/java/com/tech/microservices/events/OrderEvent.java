package com.tech.microservices.events;

import com.tech.microservices.type.OrderEventAction;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderEvent {
    private String orderId;
    private String email;
    private OrderEventAction action = OrderEventAction.CREATED;
}
