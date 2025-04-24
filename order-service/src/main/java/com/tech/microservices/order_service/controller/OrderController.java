package com.tech.microservices.order_service.controller;

import com.tech.microservices.dto.request.OrderRequest;
import com.tech.microservices.dto.response.MessageResponse;
import com.tech.microservices.dto.response.OrderResponse;
import com.tech.microservices.exception.ApplicationException;
import com.tech.microservices.order_service.service.OrderService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.concurrent.ExecutionException;

@RestController
@RequestMapping("/api/order")
public class OrderController {

    @Autowired
    OrderService orderService;

    @PostMapping("/place-order")
    @ResponseStatus(HttpStatus.CREATED)
    public OrderResponse createOrder(@Valid @RequestBody OrderRequest request,@RequestHeader("X-userId") Long userId) throws ApplicationException, ExecutionException, InterruptedException {
        return orderService.createOrder(request, userId);
    }

    @PatchMapping("/completed-order/{orderId}")
    @ResponseStatus(HttpStatus.OK)
    public MessageResponse completeOrder(@PathVariable String orderId,@RequestHeader("X-userId") Long userId) throws ApplicationException {
        orderService.completeOrder(orderId, userId);
        return new MessageResponse("Order completed successfully");
    }

    @PatchMapping("/cancel-order/{orderId}")
    @ResponseStatus(HttpStatus.OK)
    public MessageResponse cancelOrder(@PathVariable String orderId,@RequestHeader("X-userId") Long userId) throws ApplicationException {
        orderService.cancelOrder(orderId, userId);
        return new MessageResponse("Order cancelled successfully");
    }

    @GetMapping("")
    @ResponseStatus(HttpStatus.OK)
    public List<OrderResponse> getAllUserOrders(@RequestHeader("X-userId") Long userId){
        return orderService.getAllOrderByUserId(userId);
    }

    @GetMapping("/{orderId}")
    @ResponseStatus(HttpStatus.OK)
    public OrderResponse getOrderById(@PathVariable String orderId) throws ApplicationException {
        return orderService.getOrderByorderId(orderId);
    }
}
