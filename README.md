# Darum EMS — Spring Boot Microservices (Redis Streams)
# 🏢 Darum EMS — Enterprise Microservices System

**Darum EMS** is a modular HR management platform built with **Spring Boot (3.2.x)** and **Spring Cloud 2024.x**, designed to demonstrate modern enterprise microservice patterns including service discovery, centralized configuration, API gateway routing, JWT-based authentication, and event-driven communication via **Redis Streams**.

**Services:** Config Server, Discovery (Eureka), API Gateway, Auth Service (JWT), Employee Service (HR).
**Infra:** PostgreSQL (authdb, hrdb), Redis Streams.  
**Java:** 17, **Spring Boot:** 3.2.x, **Spring Cloud:** 2024.x.
## ⚙️ Tech Stack Overview

| Layer | Technology | Purpose |
|-------|-------------|----------|
| **Core Framework** |Java 17| Spring Boot 3.2+, Spring Cloud 2024.x | Application foundation & microservice patterns |
| **Service Discovery** | Netflix Eureka | Dynamic registration and service lookup |
| **Config Management** | Spring Cloud Config Server | Centralized configuration and secrets handling |
| **Gateway** | Spring Cloud Gateway | Unified routing, JWT filter, and cross-service access |
| **Messaging** | Redis Streams (Spring Cloud Stream Binder) | Event-driven communication between services |
| **Database** | PostgreSQL 16 | Persistent storage per microservice |
| **Auth & Security** | Spring Security + JWT (HS256) | Authentication and role-based authorization |
| **Containerization** | Docker / Docker Compose | Portable, reproducible environments |
| **Testing** | JUnit 5, Mockito, Testcontainers | Unit, integration, and containerized testing |

---

## 🧩 System Architecture

**Microservices:**
1. 🧠 **Config Server** — Loads shared configurations from `config-repo/`
2. 🔍 **Discovery Service (Eureka)** — Handles service registration/discovery
3. 🚪 **API Gateway** — Routes external requests, applies JWT validation
4. 🔐 **Auth Service** — Manages user authentication, registration, and JWT issuance
5. 👔 **Employee Service** — Handles employee and departmental data management

**Infrastructure:**
- PostgreSQL databases (`authdb`, `employeedb`)
- Redis (used as event bus via Spring Cloud Stream)

---

## 🚀 Quick Start (with Docker)

```bash
docker compose up -d --build
## Quick Start (Docker)
```bash
docker compose up -d --build
# visit: http://localhost:8761 (Eureka), http://localhost:8080 (Gateway)
# auth swagger:     http://localhost:8101/swagger-ui.html
# employee swagger: http://localhost:8201/swagger-ui.html
```
> First run may take a while to download images.

## Local Dev (without Docker)
- Start Postgres 
(CREATE DATABASE authdb; ,
CREATE DATABASE employeedb;) and Redis locally.
- Run Config Server → Discovery → Gateway → Auth → Employee via IDE or:

```bash
./mvnw clean package -DskipTests
java -jar platform/config-server/target/*.jar
java -jar platform/discovery-service/target/*.jar
java -jar platform/api-gateway/target/*.jar
java -jar services/auth-service/target/*.jar
java -jar services/employee-service/target/*.jar

```
## Check health endpoints:
/actuator/health,
/actuator/info

## Endpoints (high level)
- Auth: http://localhost:8101/auth  /login, /register
- Employees: http://localhost:8201/api/employees GET /, POST /, PUT /{id}, DELETE /{id}
- Departments: http://localhost:8201/api/departments GET /, POST /, PUT /{id}, DELETE /{id}

## Eventing
When a new employee is created in Employee Service, an EmployeeCreatedEvent is published to Redis Streams (employee-created-topic).

The Auth Service subscribes to this event and automatically creates a linked user record for authentication(Spring Cloud Stream binder).

## Testing
- Unit + Web slice with JUnit/Mockito.
- Integration with Testcontainers (Postgres, Redis).

## Config
- All config is served from **Config Server** reading `config-repo/`. Services use `spring.config.import=optional:configserver:`.

## Assumptions
- HS256 JWT shared secret (can be rotated via config).
- Managers scoped to one department.
- Simplified guards for clarity.

## TODO / Future
- Transactional Outbox for events.
- Schema for events.
- CI/CD to push Docker images.
