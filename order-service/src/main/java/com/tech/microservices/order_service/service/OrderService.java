package com.tech.microservices.order_service.service;

import com.tech.microservices.dto.request.InventoryRequest;
import com.tech.microservices.dto.request.OrderRequest;
import com.tech.microservices.dto.response.MessageResponse;
import com.tech.microservices.dto.response.OrderResponse;
import com.tech.microservices.dto.response.ProductResponse;
import com.tech.microservices.dto.response.UserResponse;
import com.tech.microservices.events.OrderEvent;
import com.tech.microservices.exception.ApplicationException;
import com.tech.microservices.order_service.client.AuthClient;
import com.tech.microservices.order_service.client.InventoryClient;
import com.tech.microservices.order_service.client.ProductClient;
import com.tech.microservices.order_service.entity.Order;
import com.tech.microservices.order_service.repository.OrderRepository;
import com.tech.microservices.type.InventoryActionType;
import com.tech.microservices.type.OrderEventAction;
import com.tech.microservices.type.OrderStatusType;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.ExecutionException;

@Service
@Slf4j
public class OrderService {
    @Autowired
    OrderRepository orderRepository;
    @Autowired
    ProductClient productClient;
    @Autowired
    AuthClient authClient;
    @Autowired
    InventoryClient inventoryClient;
    @Autowired
    KafkaTemplate<String, Object> kafkaTemplate;

    private OrderResponse getOrderResponseFromOrder(Order order){
        UserResponse user = authClient.getUser(order.getUserId());
        ProductResponse product = productClient.getProduct(order.getProductId());
        return OrderResponse.builder()
                .orderId(order.getOrderId())
                .id(order.getId())
                .price(order.getPrice())
                .quantity(order.getQuantity())
                .totalPrice(order.getTotalPrice())
                .user(user)
                .product(product)
                .status(order.getStatus().toString())
                .build();
    }

    @Transactional
    public OrderResponse createOrder(OrderRequest request, Long userId) throws ApplicationException, ExecutionException, InterruptedException {
        if(inventoryClient.checkProductInStock(request.getProductId(), (long) request.getQuantity())){
            ProductResponse product = productClient.getProduct(request.getProductId());
            Order order = Order.builder()
                    .orderId(UUID.randomUUID().toString())
                    .price(product.getPrice())
                    .quantity(request.getQuantity())
                    .userId(userId)
                    .productId(request.getProductId())
                    .totalPrice(request.getQuantity()*product.getPrice())
                    .status(OrderStatusType.PENDING)
                    .build();
            order = orderRepository.save(order);
            MessageResponse messageResponse = inventoryClient.changeProductQuantity(new InventoryRequest(
                    request.getProductId(),
                    (long) request.getQuantity(),
                    InventoryActionType.SUBTRACT.toString()));
            log.info("MessageResponse : {}", messageResponse);
            OrderResponse orderResponse = getOrderResponseFromOrder(order);
            OrderEvent createdOrderEvent = new OrderEvent(orderResponse.getOrderId(), orderResponse.getUser().getEmail(), OrderEventAction.CREATED);
            log.info("Start- Sending CreatedOrderEvent {} to Kafka Topic", createdOrderEvent);
            kafkaTemplate.send("orders",createdOrderEvent)
                    .whenComplete((result, ex)->{
                        if(ex!=null){
                            log.info("Error- Sending CreatedOrderEvent {} to Kafka Topic\nError: {}", createdOrderEvent, ex.getMessage());
                        }else{
                            log.info("End- Sending CreatedOrderEvent {} to Kafka Topic", createdOrderEvent);
                        }
                    });
            return orderResponse;
        } else{
            throw new ApplicationException("quantity not available.");
        }
    }

    public OrderResponse getOrderByorderId(String orderId) throws ApplicationException {
        return getOrderResponseFromOrder(orderRepository.findByOrderId(orderId)
                .orElseThrow(()->new ApplicationException("order not found.")));
    }

    public List<OrderResponse> getAllOrderByUserId(Long userId){
        return orderRepository.findAllByUserId(userId).stream()
                .map(this::getOrderResponseFromOrder)
                .toList();
    }

    @Transactional
    public void completeOrder(String orderId, Long userId) throws ApplicationException {
        Order order = orderRepository.findByOrderId(orderId)
                .orElseThrow(()->new ApplicationException("Order not found"));
        OrderResponse orderResponse = getOrderResponseFromOrder(order);
        if(OrderStatusType.PENDING.toString().equals(order.getStatus().toString())){
            if(Objects.equals(orderResponse.getProduct().getOwner().getId(), userId)){
                order.setStatus(OrderStatusType.COMPLETED);
                orderRepository.save(order);
                OrderEvent completeOrderEvent = new OrderEvent(orderResponse.getOrderId(), orderResponse.getProduct().getOwner().getEmail(), OrderEventAction.COMPLETED);
                log.info("Start- Sending CompleteOrderEvent {} to Kafka Topic", completeOrderEvent);
                kafkaTemplate.send("orders",completeOrderEvent)
                        .whenComplete((result, ex)->{
                            if(ex!=null){
                                log.info("Error- Sending CompleteOrderEvent {} to Kafka Topic\nError: {}", completeOrderEvent, ex.getMessage());
                            }else{
                                log.info("End- Sending CompleteOrderEvent {} to Kafka Topic", completeOrderEvent);
                            }
                        });
            }else{
                throw new ApplicationException("Don't have access to complete this order.");
            }

        }else{
           throw new ApplicationException("Order cannot be completed");
        }

    }

    @Transactional
    public void cancelOrder(String orderId, Long userId) throws ApplicationException {
        Order order = orderRepository.findByOrderId(orderId)
                .orElseThrow(()->new ApplicationException("Order not found"));
        if(OrderStatusType.PENDING.toString().equals(order.getStatus().toString())){
            if(Objects.equals(order.getUserId(), userId)){
                order.setStatus(OrderStatusType.CANCELLED);
                orderRepository.save(order);
                MessageResponse messageResponse = inventoryClient.changeProductQuantity(new InventoryRequest(
                        order.getProductId(),
                        (long) order.getQuantity(),
                        InventoryActionType.ADD.toString()));
                OrderResponse orderResponse = getOrderResponseFromOrder(order);
                OrderEvent cancelOrderEvent = new OrderEvent(orderResponse.getOrderId(), orderResponse.getProduct().getOwner().getEmail(), OrderEventAction.CANCELLED);
                log.info("Start- Sending CancelOrderEvent {} to Kafka Topic", cancelOrderEvent);
                kafkaTemplate.send("orders",cancelOrderEvent)
                        .whenComplete((result, ex)->{
                            if(ex!=null){
                                log.info("Error- Sending CancelOrderEvent {} to Kafka Topic\nError: {}", cancelOrderEvent, ex.getMessage());
                            }else{
                                log.info("End- Sending CancelOrderEvent {} to Kafka Topic", cancelOrderEvent);
                            }
                        });
            }else{
                throw new ApplicationException("Don't have access to cancel this order.");
            }

        }else{
            throw new ApplicationException("Order cannot be cancelled");
        }
    }
}
