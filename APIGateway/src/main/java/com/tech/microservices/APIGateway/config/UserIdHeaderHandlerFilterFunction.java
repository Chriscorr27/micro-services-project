package com.tech.microservices.APIGateway.config;

import org.springframework.stereotype.Component;
import org.springframework.web.servlet.function.ServerRequest;

import java.util.function.Function;

@Component
public class UserIdHeaderHandlerFilterFunction implements Function<ServerRequest, ServerRequest> {


    @Override
    public ServerRequest apply(ServerRequest request) {
        ServerRequest filteredRequest;

        // Remove X-userId if the client has sent it
        if (request.headers().header("X-userId").stream().findFirst().isPresent()) {
            filteredRequest = ServerRequest.from(request).headers(headers -> {
                headers.remove("X-userId");
            }).build();
        } else {
            filteredRequest = request;
        }

        // Add X-userId if user login
        return filteredRequest.principal()
                .map(principal -> {
                    return ServerRequest.from(filteredRequest)
                            .headers(httpHeaders -> httpHeaders.remove("Authorization"))
                            .header("X-userId", principal.getName())  // Add authenticated username
                            .build();
                })
                .orElseGet(() -> {
                    // If there is no authenticated user, continue with the filtered request
                    return filteredRequest;
                });
    }
}
