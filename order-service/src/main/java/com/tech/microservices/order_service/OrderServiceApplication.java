package com.tech.microservices.order_service;

import com.tech.microservices.order_service.client.AuthClient;
import com.tech.microservices.order_service.client.InventoryClient;
import com.tech.microservices.order_service.client.ProductClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.context.annotation.Bean;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.support.RestClientAdapter;
import org.springframework.web.service.invoker.HttpServiceProxyFactory;

@SpringBootApplication
@EnableDiscoveryClient
public class OrderServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(OrderServiceApplication.class, args);
	}

	@Value("${auth.service.uri}")
	private String authServiceUri;
	@Value("${product.service.uri}")
	private String productServiceUri;
	@Value("${inventory.service.uri}")
	private String inventoryServiceUri;

	@Bean
	@LoadBalanced
	public RestClient.Builder restClientBuilder() {
		return RestClient.builder();
	}

	@Bean
	public AuthClient authClient(@LoadBalanced RestClient.Builder builder){
		RestClient restClient = builder
				.baseUrl(authServiceUri)
				.build();
		RestClientAdapter restClientAdapter = RestClientAdapter.create(restClient);
		HttpServiceProxyFactory httpServiceProxyFactory = HttpServiceProxyFactory.
				builderFor(restClientAdapter)
				.build();
		return httpServiceProxyFactory.createClient(AuthClient.class);
	}

	@Bean
	public ProductClient productClient(@LoadBalanced RestClient.Builder builder){
		RestClient restClient = builder
				.baseUrl(productServiceUri)
				.build();
		RestClientAdapter restClientAdapter = RestClientAdapter.create(restClient);
		HttpServiceProxyFactory httpServiceProxyFactory = HttpServiceProxyFactory.
				builderFor(restClientAdapter)
				.build();
		return httpServiceProxyFactory.createClient(ProductClient.class);
	}

	@Bean
	public InventoryClient inventoryClient(@LoadBalanced RestClient.Builder builder){
		RestClient restClient = builder
				.baseUrl(inventoryServiceUri)
				.build();
		RestClientAdapter restClientAdapter = RestClientAdapter.create(restClient);
		HttpServiceProxyFactory httpServiceProxyFactory = HttpServiceProxyFactory.
				builderFor(restClientAdapter)
				.build();
		return httpServiceProxyFactory.createClient(InventoryClient.class);
	}
}
