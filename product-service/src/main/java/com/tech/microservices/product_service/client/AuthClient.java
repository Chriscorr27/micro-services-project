package com.tech.microservices.product_service.client;

import com.tech.microservices.dto.response.UserResponse;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.service.annotation.GetExchange;


public interface AuthClient {

    @GetExchange("/api/auth/user/{userId}")
    UserResponse getUser(@PathVariable Long userId);
}

//@FeignClient(name = "authClient", url = "${auth.service.uri}/api/auth")