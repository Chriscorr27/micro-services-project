package com.tech.microservices.auth_service;

import org.springframework.boot.SpringApplication;
import org.testcontainers.utility.TestcontainersConfiguration;

public class TestAuthServiceApplication {

	public static void main(String[] args) {
		SpringApplication.from(AuthServiceApplication::main).with(TestcontainersConfiguration.class).run(args);
	}

}
