package com.tech.microservices.auth_service;

import io.restassured.RestAssured;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.testcontainers.containers.MySQLContainer;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class AuthServiceApplicationTests {
	@ServiceConnection
	static MySQLContainer mySQLContainer = new MySQLContainer("mysql:8.0");
	@LocalServerPort
	private int port;

	@BeforeEach
	void setup(){
		RestAssured.baseURI = "http://localhost";
		RestAssured.port = port;
	}

	static {
		mySQLContainer.start();
	}

	@Test
	void shouldCreateuser() {
		String requestBody = """
				{
				   "email":"chrishal@gmail.com",
				   "name":"Chrishal Correia",
				   "password":"pass123"
			   }
		""";


		RestAssured.given()
				.contentType("application/json")
				.body(requestBody)
				.when()
				.post("/api/auth/register")
				.then()
				.statusCode(201)
				.body("user", notNullValue())
				.body("user.id", notNullValue())
				.body("user.name", equalTo("Chrishal Correia"))
				.body("user.email", equalTo("chrishal@gmail.com"));
	}

}
