package com.tech.microservices.auth_service.controller;

import com.tech.microservices.auth_service.entity.User;
import com.tech.microservices.auth_service.service.AuthService;
import com.tech.microservices.dto.request.LoginRequest;
import com.tech.microservices.dto.request.RegisterRequest;
import com.tech.microservices.dto.response.AuthResponse;
import com.tech.microservices.exception.ApplicationException;
import com.tech.microservices.utils.JWTUtil;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    @Autowired
    private AuthService authService;

    @PostMapping("/login")
    @ResponseStatus(HttpStatus.OK)
    public AuthResponse login(@Valid @NotNull @RequestBody LoginRequest request) throws Exception {
        return authService.login(request);
    }


    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    public AuthResponse register(@Valid @NotNull @RequestBody RegisterRequest request) throws ApplicationException {
        return authService.register(request);
    }

    @GetMapping("/profile")
    @ResponseStatus(HttpStatus.OK)
    public User profile(@RequestHeader("X-userId") Long userId) throws ApplicationException {
        return authService.getUserById(userId);
    }


    @GetMapping("/user/{userId}")
    @ResponseStatus(HttpStatus.OK)
    public User getUserById(@PathVariable Long userId) throws ApplicationException {
        return authService.getUserById(userId);
    }
}
