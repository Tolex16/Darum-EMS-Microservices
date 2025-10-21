Darum EMS — Enterprise Microservices System (Kafka Streams Ready)

Darum EMS is a modular HR management platform built with Spring Boot 3.3.3 and Spring Cloud 2024.x, showcasing enterprise microservice patterns such as service discovery, centralized configuration, API gateway routing, JWT-based authentication, and event-driven communication (via Kafka).

Services:

Config Server · Discovery (Eureka) · API Gateway · Auth Service · Employee Service

Infrastructure:

PostgreSQL (authdb, employeedb) · Kafka · Spring Cloud Gateway

⚙️ Tech Stack Overview
Layer	Technology	Purpose
Core	Java 17, Spring Boot 3.3+, Spring Cloud 2024.x	Application & microservice foundation
Discovery	Netflix Eureka	Dynamic service registration and lookup
Config	Spring Cloud Config Server	Centralized config and secrets
Gateway	Spring Cloud Gateway	Unified routing, JWT filter, and rate limiting
Messaging	Kafka	Event-driven microservice communication
Database	PostgreSQL 16	Service-level persistence
Security	Spring Security + JWT (HS256)	Authentication & authorization
Validation	Custom @StrongPassword Annotation	Enforces strong password policy
Rate Limiting	Spring Cloud Gateway + Redis	Prevents abuse and stabilizes load
Containerization	Docker / Docker Compose	Consistent deployment environments

 🧩 System Architecture

Config Server — loads shared configs

Discovery (Eureka) — registers microservices

API Gateway — central entry point, JWT verification & rate limiting

Auth Service — authentication, JWT issuance, password enforcement

Employee Service — CRUD for employees & departments, event publisher

🔒 Key Features
1️⃣ Rate Limiter (Gateway)

Integrated RedisRateLimiter limits requests per user/IP:

Default: 5 requests/second with burst capacity of 10

Protects against brute-force attacks & API overloads

2️⃣ Custom Password Validation

Annotation: @StrongPassword
Ensures:

≥8 chars

1 uppercase, 1 lowercase, 1 number, 1 special character

Example usage:

public class RegisterRequest {
    @StrongPassword
    private String password;
}

🚀 Quick Start (Docker)
docker compose up -d --build


Visit:

Eureka: http://localhost:8761

Gateway: http://localhost:8080

Auth Swagger: http://localhost:8101/swagger-ui.html

Employee Swagger: http://localhost:8201/swagger-ui.html

🌐 API Endpoints
🔐 AUTH SERVICE (/auth)
1. Register User

POST /auth/register

Request:

{
  "email": "jane.doe@example.com",
  "password": "Passw0rd!",
  "firstName": "Jane",
  "lastName": "Doe",
  "role": "EMPLOYEE"
}


Response:

{
  "message": "User registered successfully",
  "userId": 12,
  "timestamp": "2025-10-21T14:30:22Z"
}

2. Login

POST /auth/login

Request:

{
  "email": "jane.doe@example.com",
  "password": "Passw0rd!"
}


Response:

{
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "expiresIn": 3600,
  "role": "EMPLOYEE"
}

👔 EMPLOYEE SERVICE (/api/employees)
1. Create Employee

POST /api/employees

Request:

{
  "firstName": "John",
  "lastName": "Smith",
  "email": "john.smith@example.com",
  "department": "Engineering",
  "position": "Software Engineer",
  "salary": 350000
}


Response:

{
  "id": 7,
  "firstName": "John",
  "lastName": "Smith",
  "email": "john.smith@example.com",
  "department": "Engineering",
  "position": "Software Engineer",
  "salary": 350000,
  "createdAt": "2025-10-21T13:00:00Z"
}


📨 Publishes EmployeeCreatedEvent to employee-created-topic for AuthService to consume.

2. Get All Employees

GET /api/employees

Response:

[
  {
    "id": 1,
    "firstName": "Jane",
    "lastName": "Doe",
    "department": "HR",
    "email": "jane.doe@example.com"
  },
  {
    "id": 2,
    "firstName": "John",
    "lastName": "Smith",
    "department": "Engineering",
    "email": "john.smith@example.com"
  }
]

3. Get Employee by ID

GET /api/employees/{id}

Response:

{
  "id": 1,
  "firstName": "Jane",
  "lastName": "Doe",
  "email": "jane.doe@example.com",
  "department": "HR"
}

4. Update Employee

PUT /api/employees/{id}

Request:

{
  "department": "Operations",
  "position": "HR Manager",
  "salary": 400000
}


Response:

{
  "message": "Employee record updated successfully",
  "timestamp": "2025-10-21T14:00:00Z"
}

5. Delete Employee

DELETE /api/employees/{id}

Response:

{
  "message": "Employee deleted successfully"
}

🏢 DEPARTMENTS (/api/departments)
Get All Departments

GET /api/departments

Response:

[
  { "id": 1, "name": "Engineering", "employeeCount": 10 },
  { "id": 2, "name": "HR", "employeeCount": 3 }
]

Create Department

POST /api/departments

Request:

{ "name": "Operations" }


Response:

{
  "id": 3,
  "name": "Operations",
  "message": "Department created successfully"
}

📡 Event Flow Example

Employee Service publishes:

{
  "employeeId": 7,
  "email": "john.smith@example.com",
  "department": "Engineering"
}


Auth Service consumes event:

✅ Received EmployeeCreatedEvent for john.smith@example.com


→ Creates a login user with temporary password and assigned role.

🧪 Testing
Test Type	Tools	Scope
Unit Tests	JUnit 5, Mockito	Service & Repository layers
Integration	Testcontainers	PostgreSQL, Kafka
End-to-End	WebTestClient	Full microservice flow
🔧 Config Management

All microservices import shared properties:

spring.config.import=optional:configserver:


Each service registers itself dynamically with Eureka and fetches centralized configuration from Config Server.
