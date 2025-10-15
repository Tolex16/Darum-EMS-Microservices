package com.darum.ems.auth.Config;

import com.darum.ems.auth.Implementation.AuthServiceImpl;
import com.darum.ems.auth.Implementation.ServiceInterface.AuthService;
import com.darum.ems.auth.Listener.EmployeeCreatedEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import java.util.function.Consumer;

@Configuration
@RequiredArgsConstructor
public class AuthEventsConfig {

    private final AuthService authService;

    @Bean
    public Consumer<EmployeeCreatedEvent> employeeCreated() {
        return event -> {
            System.out.println("📩 Received EmployeeCreatedEvent: " + event.getEmail());
            authService.createUser(event.getEmail());
        };
    }
}
