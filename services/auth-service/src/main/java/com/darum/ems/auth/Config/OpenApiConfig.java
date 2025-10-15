package com.darum.ems.auth.Config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI authServiceOpenAPI() {
        Server localServer = new Server();
        localServer.setUrl("http://localhost:8081");
        localServer.setDescription("Auth Service - Local");

        Server gatewayServer = new Server();
        gatewayServer.setUrl("http://localhost:8080/api");
        gatewayServer.setDescription("Auth Service via API Gateway");

        Contact contact = new Contact();
        contact.setName("Your Name");
        contact.setEmail("your.email@company.com");

        Info info = new Info()
                .title("Employee Management System - Auth Service API")
                .version("1.0.0")
                .contact(contact)
                .description("Authentication and Authorization Service for Employee Management System")
                .license(new License().name("Apache 2.0").url("http://springdoc.org"));

        return new OpenAPI()
                .info(info)
                .servers(List.of(localServer, gatewayServer));
    }
}
