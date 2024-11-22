package com.bankaudit.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;

@Configuration
public class OpenApiConfig {
@Bean
    public OpenAPI openAPI() {
        return new OpenAPI()
        .addServersItem(new Server().url("http://localhost:8081/"))
        .addServersItem(new Server().url("https://isoftnew5.finakon.in/api/"))
        .addServersItem(new Server().url("https://isoftnew4.finakon.in/api/"))
                .info(new Info().title("BaaS API")
                        .description("BaaS API")
                        .version("1.0"))
                .components(new Components()
                        .addSecuritySchemes("Authorization", 
                                new SecurityScheme()
                                        .type(SecurityScheme.Type.HTTP)
                                        .scheme("Bearer").name("Authorization"))).addSecurityItem(new SecurityRequirement().addList("Authorization"));
    }
    


}
