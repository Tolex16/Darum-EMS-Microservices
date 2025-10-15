package com.darum.ems.employee.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI employeeServiceOpenAPI() {
        Server localServer = new Server();
        localServer.setUrl("http://localhost:8082");
        localServer.setDescription("Employee Service - Local");

        Server gatewayServer = new Server();
        gatewayServer.setUrl("http://localhost:8080/api");
        gatewayServer.setDescription("Employee Service via API Gateway");

        Contact contact = new Contact();
        contact.setName("Your Name");
        contact.setEmail("your.email@company.com");

        Info info = new Info()
                .title("Employee Management System - Employee Service API")
                .version("1.0.0")
                .contact(contact)
                .description("Employee and Department Management Service with Redis Streams Events");

        SecurityScheme securityScheme = new SecurityScheme()
                .type(SecurityScheme.Type.HTTP)
                .scheme("bearer")
                .bearerFormat("JWT")
                .description("JWT Authentication");

        SecurityRequirement securityRequirement = new SecurityRequirement()
                .addList("Bearer Authentication");

        return new OpenAPI()
                .info(info)
                .servers(List.of(localServer, gatewayServer))
                .addSecurityItem(securityRequirement)
                .components(new Components()
                        .addSecuritySchemes("Bearer Authentication", securityScheme));
    }
}
