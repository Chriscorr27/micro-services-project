package com.tech.microservices.inventory_service.config;

import io.swagger.v3.oas.models.ExternalDocumentation;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenAPIConfig {

    @Bean
    public OpenAPI authServiceAPI(){
        return new OpenAPI()
                .info(new Info().title("Inventory Service API")
                        .description("This is the REST API for Inventory service")
                        .version("V0.0.1")
                        .license(new License().name("Apache 2.0")))
                .externalDocs(new ExternalDocumentation()
                        .description("You can refer to Inventory Service wiki Documentation.")
                        .url("https://inventory-service.com/docs")
                );
    }
}
