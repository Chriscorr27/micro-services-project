package com.tech.microservices.APIGateway.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.server.mvc.filter.CircuitBreakerFilterFunctions;
import org.springframework.cloud.gateway.server.mvc.handler.GatewayRouterFunctions;
import org.springframework.cloud.gateway.server.mvc.handler.HandlerFunctions;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.web.client.RestClient;
import org.springframework.web.servlet.function.RequestPredicates;
import org.springframework.web.servlet.function.RouterFunction;
import org.springframework.web.servlet.function.ServerResponse;

import java.net.URI;

import static org.springframework.cloud.gateway.server.mvc.filter.FilterFunctions.setPath;


@Configuration
public class APIGatewayConfig {
    @Autowired
    UserIdHeaderHandlerFilterFunction userIdHeaderHandlerFilterFunction;
    @Autowired
    RestClient restClient;

    @Value("${auth.service.uri}")
    private String authServiceUri;

    @Value("${product.service.uri}")
    private String productServiceUri;

    @Value("${inventory.service.uri}")
    private String inventoryServiceUri;

    @Value("${order.service.uri}")
    private String orderServiceUri;

    @Bean
    public RouterFunction<ServerResponse> authServiceRoute(){
        return GatewayRouterFunctions.route("auth_service")
                .before(userIdHeaderHandlerFilterFunction)
                .route(RequestPredicates.path("/api/auth/**"),
                        HandlerFunctions.http(authServiceUri))
                .filter(CircuitBreakerFilterFunctions.circuitBreaker("auth_service_circuitbreaker",
                        URI.create("forward:/fallback")))
                .build();
    }

    @Bean
    public RouterFunction<ServerResponse> authServiceSwaggerRoute(){
        return GatewayRouterFunctions.route("auth_service_swagger")
                .route(RequestPredicates.path("/aggregate/auth-service/v3/api-docs"),
                        HandlerFunctions.http(authServiceUri))
                .filter(CircuitBreakerFilterFunctions.circuitBreaker("auth_service_swagger_circuitbreaker",
                        URI.create("forward:/fallback")))
                .filter(setPath("/api-docs"))
                .build();
    }

    @Bean
    public RouterFunction<ServerResponse> productServiceRoute(){
        return GatewayRouterFunctions.route("product_service")
                .before(userIdHeaderHandlerFilterFunction)
                .route(RequestPredicates.path("/api/product/**"),
                        HandlerFunctions.http(productServiceUri))
                .filter(CircuitBreakerFilterFunctions.circuitBreaker("product_service_circuitbreaker",
                        URI.create("forward:/fallback")))
                .build();
    }

    @Bean
    public RouterFunction<ServerResponse> productServiceSwaggerRoute(){
        return GatewayRouterFunctions.route("product_service_swagger")
                .route(RequestPredicates.path("/aggregate/product-service/v3/api-docs"),
                        HandlerFunctions.http(productServiceUri))
                .filter(setPath("/api-docs"))
                .filter(CircuitBreakerFilterFunctions.circuitBreaker("product_service_swagger_circuitbreaker",
                        URI.create("forward:/fallback")))
                .build();
    }

    @Bean
    public RouterFunction<ServerResponse> inventoryServiceRoute(){
        return GatewayRouterFunctions.route("inventory_service")
                .before(userIdHeaderHandlerFilterFunction)
                .route(RequestPredicates.path("/api/inventory/**"),
                        HandlerFunctions.http(inventoryServiceUri))
                .filter(CircuitBreakerFilterFunctions.circuitBreaker("inventory_service_circuitbreaker",
                        URI.create("forward:/fallback")))
                .build();
    }

    @Bean
    public RouterFunction<ServerResponse> inventoryServiceSwaggerRoute(){
        return GatewayRouterFunctions.route("inventory_service_swagger")
                .route(RequestPredicates.path("/aggregate/inventory-service/v3/api-docs"),
                        HandlerFunctions.http(inventoryServiceUri))
                .filter(setPath("/api-docs"))
                .filter(CircuitBreakerFilterFunctions.circuitBreaker("inventory_service_swagger_circuitbreaker",
                        URI.create("forward:/fallback")))
                .build();
    }

    @Bean
    public RouterFunction<ServerResponse> orderServiceRoute(){
        return GatewayRouterFunctions.route("order_service")
                .before(userIdHeaderHandlerFilterFunction)
                .route(RequestPredicates.path("/api/order/**"),
                        HandlerFunctions.http(orderServiceUri))
                .filter(CircuitBreakerFilterFunctions.circuitBreaker("order_service_circuitbreaker",
                        URI.create("forward:/fallback")))
                .build();
    }

    @Bean
    public RouterFunction<ServerResponse> orderServiceSwaggerRoute(){
        return GatewayRouterFunctions.route("order_service_swagger")
                .route(RequestPredicates.path("/aggregate/order-service/v3/api-docs"),
                        HandlerFunctions.http(orderServiceUri))
                .filter(setPath("/api-docs"))
                .filter(CircuitBreakerFilterFunctions.circuitBreaker("order_service_swagger_circuitbreaker",
                        URI.create("forward:/fallback")))
                .build();
    }

    @Bean
    public RouterFunction<ServerResponse> fallbackRoute(){
        return GatewayRouterFunctions.route("fallback")
                .GET("/fallback",
                        request -> ServerResponse.status(HttpStatus.SERVICE_UNAVAILABLE)
                                .body("Service Unavailable please try later.")
                ).build();
    }

}
