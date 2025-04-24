package com.tech.microservices.notification_service;

import com.tech.microservices.notification_service.client.OrderClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.context.annotation.Bean;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.support.RestClientAdapter;
import org.springframework.web.service.invoker.HttpServiceProxyFactory;

@SpringBootApplication
@EnableKafka
@EnableDiscoveryClient
public class NotificationServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(NotificationServiceApplication.class, args);
	}

	@Value("${order.service.uri}")
	private String orderServiceUri;

	@Bean
	@LoadBalanced
	public RestClient.Builder restClientBuilder() {
		return RestClient.builder();
	}

	@Bean
	public OrderClient orderClient(@LoadBalanced RestClient.Builder builder){
		RestClient restClient = builder
				.baseUrl(orderServiceUri)
				.build();
		RestClientAdapter restClientAdapter = RestClientAdapter.create(restClient);
		HttpServiceProxyFactory httpServiceProxyFactory = HttpServiceProxyFactory.
				builderFor(restClientAdapter)
				.build();
		return httpServiceProxyFactory.createClient(OrderClient.class);
	}
}
