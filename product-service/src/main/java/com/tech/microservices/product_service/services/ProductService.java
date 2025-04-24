package com.tech.microservices.product_service.services;

import com.tech.microservices.dto.request.ProductRequest;
import com.tech.microservices.dto.response.ProductResponse;
import com.tech.microservices.dto.response.UserResponse;
import com.tech.microservices.exception.ApplicationException;
import com.tech.microservices.product_service.client.AuthClient;
import com.tech.microservices.product_service.entity.Product;
import com.tech.microservices.product_service.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProductService {
    @Autowired
    ProductRepository productRepository;
    @Autowired
    AuthClient authClient;

    private ProductResponse getProductResponseFromProduct(Product product){
        UserResponse userResponse = authClient.getUser(product.getOwnerId());
        return ProductResponse.builder()
                .name(product.getName())
                .price(product.getPrice())
                .desc(product.getDesc())
                .id(product.getId())
                .owner(userResponse)
                .build();
    }

    public ProductResponse createProduct(ProductRequest request, Long userId){
        Product product = Product.builder()
                .name(request.getName())
                .desc(request.getDesc())
                .price(request.getPrice())
                .ownerId(userId)
                .build();
        product = productRepository.save(product);
        return getProductResponseFromProduct(product);
    }

    public List<ProductResponse> getAllProducts(){
        return productRepository.findAll().stream()
                .map(this::getProductResponseFromProduct)
                .toList();
    }

    public ProductResponse getProductById(String id) throws ApplicationException {
        return getProductResponseFromProduct(
                productRepository.findById(id)
                        .orElseThrow(()-> new ApplicationException("Product not found."))
        );
    }
}
