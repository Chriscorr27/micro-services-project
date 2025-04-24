package com.tech.microservices.order_service.stubs;

import com.tech.microservices.dto.response.UserResponse;
import wiremock.com.fasterxml.jackson.core.JsonProcessingException;
import wiremock.com.fasterxml.jackson.databind.ObjectMapper;

import java.util.HashMap;
import java.util.Map;

import static com.github.tomakehurst.wiremock.client.WireMock.*;

public class ProductClientStub {

    public static void stubGetProductCall(String productId, String name, String desc, double price, UserResponse owner) throws JsonProcessingException {
        String url = "/api/product/"+productId;
        Map<String, Object> payload = new HashMap<>();
        payload.put("id", productId);
        payload.put("desc", desc);
        payload.put("name", name);
        payload.put("price", price);
        payload.put("owner", owner);

        ObjectMapper mapper = new ObjectMapper();
        String responseBody = mapper.writeValueAsString(payload);

        stubFor(get(urlEqualTo(url))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type","application/json")
                        .withBody(responseBody)
                ));
    }


}
