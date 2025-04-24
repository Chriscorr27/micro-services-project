package com.tech.microservices.order_service;

import com.tech.microservices.dto.response.UserResponse;
import com.tech.microservices.order_service.stubs.AuthClientStub;
import com.tech.microservices.order_service.stubs.InventoryClientStub;
import com.tech.microservices.order_service.stubs.ProductClientStub;
import com.tech.microservices.type.InventoryActionType;
import com.tech.microservices.type.OrderStatusType;
import io.restassured.RestAssured;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.cloud.contract.wiremock.AutoConfigureWireMock;
import org.testcontainers.containers.MySQLContainer;
import wiremock.com.fasterxml.jackson.core.JsonProcessingException;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureWireMock(port = 0)
class OrderServiceApplicationTests {

	@ServiceConnection
	static MySQLContainer mySQLContainer = new MySQLContainer("mysql:8.0");
	@LocalServerPort
	private int port;

	@BeforeEach
	void setup(){
		RestAssured.baseURI = "http://localhost";
		RestAssured.port = port;

		InventoryClientStub.stubInventoryCheckProductInStockCall("6800de8c24719926ad1bc3a0", 2L);
		InventoryClientStub.stubInventoryCheckProductInStockCall("6800de8c24719926ad1bc3a0", 11L);
		try{
			InventoryClientStub.stubInventoryChangeProductQuanityCall("6800de8c24719926ad1bc3a0",2L, InventoryActionType.SUBTRACT.toString());
			AuthClientStub.stubGetUserCall(2L, "Chrishal Correia", "chrishal@gmail.com");
			UserResponse owner = new UserResponse(1L, "Chris Correia", "chris@gmail.com");
			ProductClientStub.stubGetProductCall("6800de8c24719926ad1bc3a0", "Iphone 14", "This is Iphone 14", 50000.0, owner);
		} catch (JsonProcessingException e) {
			System.out.println(e.getMessage());
		}


	}

	static {
		mySQLContainer.start();
	}

	@Test
	void shouldCreateOrder() {
		String requestBody = """
				{
					 "productId":"6800de8c24719926ad1bc3a0",
					 "quantity":2
				 }
		""";


		RestAssured.given()
				.contentType("application/json")
				.header("X-userId",2)
				.body(requestBody)
				.when()
				.post("/api/order/place-order")
				.then()
				.statusCode(201)
				.body("orderId", notNullValue())
				.body("status", equalTo(OrderStatusType.PENDING.toString()))
				.body("quantity", equalTo(2))
				.body("price", equalTo(50000.0F))
				.body("totalPrice",equalTo(100000.0F))
				.body("product.id", equalTo("6800de8c24719926ad1bc3a0"))
				.body("user.id", equalTo(2))
				.body("user.name", equalTo("Chrishal Correia"))
				.body("user.email", equalTo("chrishal@gmail.com"));
	}

	@Test
	void shouldNotCreateOrder() {
		String requestBody = """
				{
					 "productId":"6800de8c24719926ad1bc3a0",
					 "quantity":11
				 }
		""";


		RestAssured.given()
				.contentType("application/json")
				.header("X-userId",2)
				.body(requestBody)
				.when()
				.post("/api/order/place-order")
				.then()
				.statusCode(400)
				.body("message", equalTo("quantity not available."));
	}
}
