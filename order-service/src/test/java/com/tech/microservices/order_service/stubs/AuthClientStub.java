package com.tech.microservices.order_service.stubs;

import wiremock.com.fasterxml.jackson.core.JsonProcessingException;
import wiremock.com.fasterxml.jackson.databind.ObjectMapper;

import java.util.HashMap;
import java.util.Map;

import static com.github.tomakehurst.wiremock.client.WireMock.*;

public class AuthClientStub {

    public static void stubGetUserCall(Long userId, String name, String email) throws JsonProcessingException {
        String url = "/api/auth/user/"+userId;
        Map<String, Object> payload = new HashMap<>();
        payload.put("id", userId);
        payload.put("email", email);
        payload.put("name", name);

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
