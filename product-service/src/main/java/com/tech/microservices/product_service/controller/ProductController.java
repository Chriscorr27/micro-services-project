package com.tech.microservices.product_service.controller;

import com.tech.microservices.dto.request.ProductRequest;
import com.tech.microservices.dto.response.ProductResponse;
import com.tech.microservices.exception.ApplicationException;
import com.tech.microservices.product_service.services.ProductService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/product")
public class ProductController {
    @Autowired
    ProductService productService;

    @PostMapping("")
    @ResponseStatus(HttpStatus.CREATED)
    public ProductResponse createProduct(@Valid @RequestBody ProductRequest request,@RequestHeader("X-userId") Long userId){
        return productService.createProduct(request, userId);
    }

    @GetMapping("")
    @ResponseStatus(HttpStatus.OK)
    public List<ProductResponse> getAllProducts(){
        return productService.getAllProducts();
    }

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public ProductResponse getProductById(@PathVariable String id) throws ApplicationException {
        return productService.getProductById(id);
    }
}
