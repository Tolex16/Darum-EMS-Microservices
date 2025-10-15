package com.darum.ems.employee.application.events;

import lombok.RequiredArgsConstructor;
import org.springframework.cloud.stream.function.StreamBridge;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EmployeeEventPublisher {
  private final StreamBridge streamBridge;

  public void publish(EmployeeCreatedEvent event) {
    // Publish the EmployeeCreatedEvent to the auth-service channel (Auth service listens to this)
    streamBridge.send("employeeCreated-out-0", event);
}
}
