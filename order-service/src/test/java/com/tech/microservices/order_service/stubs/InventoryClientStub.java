package com.tech.microservices.order_service.stubs;

import wiremock.com.fasterxml.jackson.core.JsonProcessingException;
import wiremock.com.fasterxml.jackson.databind.ObjectMapper;

import java.util.HashMap;
import java.util.Map;

import static com.github.tomakehurst.wiremock.client.WireMock.*;

public class InventoryClientStub {

    public static void stubInventoryCheckProductInStockCall(String productId, Long quantity){
        String url = "/api/inventory/check-product-in-stock?productId="+productId+"&quantity="+quantity;
        String body = quantity<10?"true":"false";
        stubFor(get(urlEqualTo(url))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type","application/json")
                        .withBody(body)
                ));
    }

    public static void stubInventoryChangeProductQuanityCall(String productId, Long quantity, String action) throws JsonProcessingException {
        String url = "/api/inventory/change-product-quantity";
        String body = """
            {
                "message": "Successfully change product quantity"
            }
        """;
        stubFor(put(urlEqualTo(url))
                .withHeader("Content-Type", equalTo("application/json"))
                .withRequestBody(matchingJsonPath("$.productId", equalTo(productId)))
                .withRequestBody(matchingJsonPath("$.quantity", equalTo(quantity.toString())))
                .withRequestBody(matchingJsonPath("$.action", equalTo(action)))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type","application/json")
                        .withBody(body)
                ));
    }
}
