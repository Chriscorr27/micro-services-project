package com.tech.microservices.product_service;

import com.tech.microservices.product_service.client.AuthClient;
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
public class ProductServiceApplication {

	@Value("${auth.service.uri}")
	private String authserviceUri;

	public static void main(String[] args) {
		SpringApplication.run(ProductServiceApplication.class, args);
	}
	@Bean
	@LoadBalanced
	public RestClient.Builder restClientBuilder() {
		return RestClient.builder();
	}

	@Bean
	public AuthClient authClient(@LoadBalanced RestClient.Builder builder){
		RestClient restClient = builder
				.baseUrl(authserviceUri)
				.build();
		RestClientAdapter restClientAdapter = RestClientAdapter.create(restClient);
		HttpServiceProxyFactory httpServiceProxyFactory = HttpServiceProxyFactory.
				builderFor(restClientAdapter)
				.build();
		return httpServiceProxyFactory.createClient(AuthClient.class);
	}

}
