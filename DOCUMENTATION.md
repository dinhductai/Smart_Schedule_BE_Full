# Smart Schedule - Project Documentation

## 1. Project Overview

**Smart Schedule** is a microservices-based task and schedule management system with an AI-powered scheduling assistant. It enables users to create, manage, and intelligently schedule tasks across multiple services.

### Main Features
- **User Management**: Registration, authentication, profile management
- **Task Management**: CRUD operations for tasks with priority, status, and deadline
- **AI Scheduling**: GPT-powered assistant that suggests optimal task scheduling based on free time
- **Product & Order Management**: Product catalog and order processing
- **JWT Authentication**: Secure token-based authentication with role support

---

## 2. Tech Stack

| Component | Technology |
|-----------|------------|
| Language | Java 21 |
| Framework | Spring Boot 3.3.5 |
| Cloud | Spring Cloud 2023.0.0 |
| Database | PostgreSQL |
| Service Discovery | Netflix Eureka |
| API Gateway | Spring Cloud Gateway |
| Authentication | JWT (HS384) |
| AI | OpenAI GPT-3.5-Turbo |
| Containerization | Docker |
| Build Tool | Maven |

---

## 3. Architecture

### 3.1 Microservices Structure

```
┌─────────────────────────────────────────────────────────────┐
│                     API Gateway (:8888)                     │
│              (Authentication, Routing, Security)            │
└──────────────────────┬──────────────────────────────────────┘
                       │
        ┌──────────────┼──────────────┐
        ▼              ▼              ▼
┌──────────────┐ ┌──────────┐ ┌──────────────┐
│ Auth Service │ │User Service│ │ Task Service │
│   (:8081)    │ │  (:8082)  │ │   (:8084)    │
└──────────────┘ └──────────┘ └──────────────┘
                       │              │
        ┌──────────────┼──────────────┼──────────────┐
        ▼              ▼              ▼              ▼
   ┌──────────┐  ┌──────────┐  ┌──────────┐  ┌──────────┐
   │ Products │  │  Orders  │  │    AI    │  │Discovery │
   │ Service  │  │ Service  │  │ Service  │  │ Server   │
   │ (:8085)  │  │  (:8086) │  │  (:8087) │  │ (:8761)  │
   └──────────┘  └──────────┘  └──────────┘  └──────────┘
```

### 3.2 Request Flow

```
Client Request
      │
      ▼
┌─────────────┐
│API Gateway  │──▶ Authentication Filter
│ (:8888)     │──▶ Request Routing
└──────┬──────┘
       │
       ▼
┌──────────────┐    ┌────────────┐    ┌───────────┐
│  Controller  │───▶│  Service   │───▶│Repository │
│  (REST API)  │    │  (Logic)   │    │  (JPA)    │
└──────────────┘    └────────────┘    └─────┬─────┘
                                            │
                                       ┌─────▼─────┐
                                       │ PostgreSQL│
                                       └───────────┘
```

---

## 4. Modules Overview

### 4.1 Core Services

| Service | Port | Purpose | Database |
|---------|------|---------|----------|
| **discovery-server** | 8761 | Eureka service registry | - |
| **api-gateway** | 8888 | Entry point, routing, authentication | - |
| **auth-service** | 8081 | User authentication, JWT token management | `auth_db` |
| **user-service** | 8082 | User profile management | `user_db` |
| **task-service** | 8084 | Task CRUD, scheduling logic | `task_db` |
| **ai-service** | 8087 | AI chat integration for scheduling | `ai_db` |
| **product-service** | 8085 | Product catalog management | `product_db` |
| **order-service** | 8086 | Order processing | `order_db` |

### 4.2 Module Details

#### Discovery Server (`discovery-server/`)
- Netflix Eureka server for service registration/discovery
- No business logic

#### API Gateway (`api-gateway/`)
- Routes requests to appropriate services
- JWT authentication filter
- CORS configuration

#### Auth Service (`auth-service/`)
- **Entities**: `User`, `Role`
- **Features**: Login, register, JWT token generation, token refresh
- **Security**: BCrypt password encoding, JWT with HS384

#### User Service (`user-service/`)
- **Entities**: `UserProfile`, `UserSettings`
- **Features**: Profile CRUD, avatar upload, settings management
- **Mapper**: `UserMapper` for DTO conversions

#### Task Service (`task-service/`)
- **Entities**: `Task`, `Category`, `Schedule`, `FreeTime`
- **Features**: Task CRUD, categorization, weekly scheduling, free time calculation
- **Key Endpoints**: `POST /api/tasks/ai/schedule` - AI-powered scheduling

#### AI Service (`ai-service/`)
- **Entities**: `ChatMessage`, `ChatSession`
- **Features**: OpenAI GPT-3.5-Turbo integration, Vietnamese AI persona
- **Prompt**: Structured JSON prompts for scheduling recommendations

#### Product Service (`product-service/`)
- **Entities**: `Product`
- **Features**: Product catalog CRUD, stock management

#### Order Service (`order-service/`)
- **Entities**: `Order`, `OrderItem`
- **Features**: Order creation, status tracking

---

## 5. Database Schema

### 5.1 Auth Database (`auth_db`)

```
┌─────────────┐       ┌─────────────┐
│    User     │       │    Role     │
├─────────────┤       ├─────────────┤
│ id (PK)     │◀──N:1─│ id (PK)     │
│ username    │       │ name        │
│ password    │       └─────────────┘
│ email       │
│ enabled     │
└─────────────┘
```

### 5.2 User Database (`user_db`)

```
┌─────────────────┐
│  UserProfile    │
├─────────────────┤
│ id (PK)         │
│ username (UK)   │
│ fullName        │
│ email           │
│ phone           │
│ avatar          │
│ dateOfBirth     │
│ address         │
│ userId (FK→Auth)│
└─────────────────┘
```

### 5.3 Task Database (`task_db`)

```
┌─────────────┐  1:N  ┌──────────────┐
│    Task     │───────│  Category    │
├─────────────┤       ├──────────────┤
│ id (PK)     │       │ id (PK)      │
│ title       │       │ name         │
│ description │       └──────────────┘
│ priority    │
│ status      │
│ deadline    │
│ estimatedTime│
│ categoryId(FK)
│ userId (FK) │
│ createdAt   │
│ updatedAt   │
└─────────────┘
```

### 5.4 AI Database (`ai_db`)

```
┌─────────────────┐  1:N  ┌─────────────────┐
│  ChatSession    │───────│  ChatMessage     │
├─────────────────┤       ├─────────────────┤
│ id (PK)         │       │ id (PK)         │
│ userId          │       │ sessionId (FK)  │
│ title           │       │ role            │
│ createdAt       │       │ content         │
│ updatedAt       │       │ createdAt       │
└─────────────────┘       └─────────────────┘
```

### 5.5 Product Database (`product_db`)

```
┌─────────────┐
│  Product    │
├─────────────┤
│ id (PK)     │
│ name        │
│ description │
│ price       │
│ stock       │
│ imageUrl    │
│ category    │
│ active      │
│ createdAt   │
│ updatedAt   │
└─────────────┘
```

### 5.6 Order Database (`order_db`)

```
┌─────────────┐  1:N  ┌─────────────┐
│    Order    │───────│  OrderItem  │
├─────────────┤       ├─────────────┤
│ id (PK)     │       │ id (PK)     │
│ userId      │       │ orderId(FK) │
│ status      │       │ productId   │
│ totalAmount │       │ quantity    │
│ shippingAddr│       │ price       │
│ createdAt   │       └─────────────┘
│ updatedAt   │
└─────────────┘
```

---

## 6. API Endpoints

### 6.1 Authentication (`auth-service`)

| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | `/api/auth/register` | Register new user |
| POST | `/api/auth/login` | User login |
| POST | `/api/auth/refresh` | Refresh token |

### 6.2 User (`user-service`)

| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/api/users/me` | Get current user profile |
| PUT | `/api/users/me` | Update profile |
| PUT | `/api/users/me/avatar` | Update avatar |

### 6.3 Task (`task-service`)

| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/api/tasks` | List all tasks |
| POST | `/api/tasks` | Create task |
| PUT | `/api/tasks/{id}` | Update task |
| DELETE | `/api/tasks/{id}` | Delete task |
| GET | `/api/tasks/week` | Get weekly task schedule |
| POST | `/api/tasks/ai/schedule` | AI-powered scheduling |

### 6.4 AI Chat (`ai-service`)

| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | `/api/ai/chat` | Send message to AI |
| GET | `/api/ai/sessions` | List chat sessions |
| GET | `/api/ai/sessions/{id}` | Get session history |

### 6.5 Product (`product-service`)

| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/api/products` | List products |
| GET | `/api/products/{id}` | Get product |
| POST | `/api/products` | Create product |
| PUT | `/api/products/{id}` | Update product |
| DELETE | `/api/products/{id}` | Delete product |

### 6.6 Order (`order-service`)

| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/api/orders` | List orders |
| GET | `/api/orders/{id}` | Get order |
| POST | `/api/orders` | Create order |
| PUT | `/api/orders/{id}/status` | Update order status |

---

## 7. Configuration

### 7.1 Environment Variables

```bash
# Database
POSTGRES_USER=postgres
POSTGRES_PASSWORD=postgres

# JWT
JWT_SECRET=<base64-encoded-secret>
JWT_EXPIRATION=86400000

# AI
OPENAI_API_KEY=sk-...
OPENAI_BASE_URL=https://api.openai.com/v1

# Eureka
EUREKA_CLIENT_SERVICEURL_DEFAULTZONE=http://discovery-server:8761/eureka/
```

### 7.2 Service Ports

| Service | Default Port |
|---------|-------------|
| Discovery | 8761 |
| API Gateway | 8888 |
| Auth | 8081 |
| User | 8082 |
| Task | 8084 |
| AI | 8087 |
| Product | 8085 |
| Order | 8086 |

---

## 8. Developer Guide

### 8.1 Project Setup

```bash
# 1. Configure environment variables in .env
# 2. Start infrastructure
docker-compose up -d postgres redis

# 3. Start discovery server first
cd discovery-server && mvn spring-boot:run

# 4. Start other services (each in separate terminal)
cd auth-service && mvn spring-boot:run
cd user-service && mvn spring-boot:run
# ... etc

# Or use Docker Compose for all services
docker-compose up -d
```

### 8.2 Adding a New Feature

**Step 1: Create the module**
```bash
mkdir new-service && cd new-service
mvn archetype:generate -DgroupId=com.microsv -DartifactId=new-service
```

**Step 2: Add dependencies to pom.xml**
```xml
<dependency>
    <groupId>com.microsv</groupId>
    <artifactId>common-exception-lib</artifactId>
    <version>1.0.0</version>
</dependency>
```

**Step 3: Configure application.yml**
```yaml
spring:
  datasource:
    url: jdbc:postgresql://postgres:5432/new_service_db
    username: ${POSTGRES_USER}
    password: ${POSTGRES_PASSWORD}

eureka:
  client:
    service-url:
      defaultZone: ${EUREKA_CLIENT_SERVICEURL_DEFAULTZONE}
```

**Step 4: Register with API Gateway**
Add route in `api-gateway/src/main/resources/application.yml`:
```yaml
- id: new-service
  uri: lb://NEW-SERVICE
  predicates:
    - Path=/api/new/**
  filters:
    - AuthFilter
```

**Step 5: Implement layers**
```
src/main/java/com/microsv/new_service/
├── controller/
│   └── NewController.java
├── service/
│   ├── NewService.java
│   └── impl/NewServiceImpl.java
├── repository/
│   └── NewRepository.java
├── entity/
│   └── NewEntity.java
├── dto/
│   ├── request/
│   └── response/
└── exception/
    └── NewException.java
```

**Step 6: Add Dockerfile**
```dockerfile
FROM eclipse-temurin:21-jdk-alternative
COPY target/new-service-*.jar app.jar
ENTRYPOINT ["java", "-jar", "/app.jar"]
```

### 8.3 Adding a New API Endpoint

1. **Controller**: Add method with `@RequestMapping`
2. **Service**: Add business logic
3. **Repository**: Add JPA query if needed
4. **DTO**: Create request/response classes
5. **Security**: Add endpoint to whitelist or ensure JWT handling

### 8.4 Adding a New Entity

1. Create entity class with JPA annotations
2. Create repository interface
3. Create mapper (if using MapStruct)
4. Add DTOs for request/response
5. Update database migration

### 8.5 Common Patterns

**DTO Conversion**: Use MapStruct
```java
@Mapper(componentModel = "spring")
public interface TaskMapper {
    TaskDto toDto(Task task);
    Task toEntity(TaskRequest request);
}
```

**Exception Handling**: Extend `BaseException`
```java
public class TaskNotFoundException extends BaseException {
    public TaskNotFoundException(String message) {
        super(ErrorCode.TASK_NOT_FOUND, message);
    }
}
```

---

## 9. Docker Commands

```bash
# Build all services
docker-compose build

# Start all services
docker-compose up -d

# View logs
docker-compose logs -f [service-name]

# Stop all services
docker-compose down

# Rebuild single service
docker-compose up -d --build [service-name]
```

---

## 10. Testing

```bash
# Run tests for a service
cd <service-name> && mvn test

# Run with coverage
mvn test jacoco:report
```

---

## 11. Security Considerations

- JWT tokens expire after 24 hours
- Passwords are BCrypt encoded (strength 10)
- API Gateway validates all tokens except whitelisted paths
- Sensitive config in `.env` file (not committed)
