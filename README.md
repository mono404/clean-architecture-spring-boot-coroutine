# Clean Architecture Spring Boot Coroutine Project

A comprehensive reactive Spring Boot application built with Clean Architecture principles, Kotlin Coroutines, and modern microservices patterns.

## 🏗️ Architecture Overview

This project implements **Hexagonal Architecture (Clean Architecture)** with clear separation of concerns across multiple modules:

```
┌─────────────────────────────────────────────────────────────┐
│                    backend-bootstrap                         │
│                  (Main Application)                         │
└─────────────────────────────────────────────────────────────┘
                              │
┌─────────────────────────────────────────────────────────────┐
│                   backend-adapter-web                       │
│              (Controllers, Handlers, Routers)              │
└─────────────────────────────────────────────────────────────┘
                              │
┌─────────────────────────────────────────────────────────────┐
│                    backend-port-web                         │
│                 (Web Layer Contracts)                      │
└─────────────────────────────────────────────────────────────┘
                              │
┌─────────────────────────────────────────────────────────────┐
│                   backend-application                       │
│               (Business Logic & Services)                  │
└─────────────────────────────────────────────────────────────┘
                              │
┌─────────────────┬───────────────────────┬─────────────────────┐
│  backend-domain │   backend-port-infra  │  backend-adapter-   │
│   (Core Logic)  │    (Infrastructure    │      infra          │
│                 │     Contracts)        │  (External Services)│
└─────────────────┴───────────────────────┴─────────────────────┘
                              │
┌─────────────────────────────────────────────────────────────┐
│                    backend-common                           │
│              (Shared Utilities & Utils)                    │
└─────────────────────────────────────────────────────────────┘
```

## 🚀 Features

### Core Architecture
- **Clean Architecture** with hexagonal pattern
- **Domain-Driven Design (DDD)** principles
- **Event-Driven Architecture** with outbox pattern
- **CQRS** for read/write operations separation
- **Reactive Programming** with Spring WebFlux and Kotlin Coroutines

### Technology Stack
- **Spring Boot 3.3.7** with WebFlux
- **Kotlin 2.2.0** with Coroutines
- **Java 17** toolchain
- **R2DBC** with MySQL for reactive database access
- **Redis** for caching and distributed locking
- **AWS S3** integration (with LocalStack for development)
- **Firebase Admin SDK** for FCM push notifications

### Business Features
- **Authentication & Authorization**
  - JWT token-based authentication
  - OAuth integration (Google, Kakao, Apple)
  - Refresh token mechanism
  
- **Post Management System**
  - CRUD operations for posts and comments
  - Hierarchical comment structure
  - Post like and view count tracking
  - Hot post ranking algorithm
  
- **Search & Discovery**
  - Full-text search with indexing
  - Hot keyword tracking
  - Geospatial queries for campsite management
  
- **Notification System**
  - FCM push notifications
  - Template-based notification system
  - Event-driven notification triggers
  
- **File Management**
  - S3-based file upload/download
  - Progress tracking for uploads
  - Media file management

### Performance & Scalability
- **Comprehensive Caching Strategy**
  - Redis-based distributed caching
  - Cache TTL management
  - Distributed locking for cache synchronization
  
- **Event-Driven Processing**
  - Outbox pattern for reliable event publishing
  - Dead letter queue for failed events
  - Async event processing with coroutines
  
- **Database Optimization**
  - Reactive database access with R2DBC
  - Connection pooling
  - Transaction management with coroutines

## 📦 Module Structure

### Core Modules

#### `backend-bootstrap`
Main application entry point and global configuration
- Application startup
- Cache configuration
- Transaction configuration

#### `backend-domain`
Pure business logic and domain models
- Domain entities (Post, Comment, Member, etc.)
- Domain events and event payloads
- Business rules and validation

#### `backend-application`
Application services and business orchestration
- Use case implementations
- Business logic coordination
- Domain event handling

### Interface Modules

#### `backend-port-web`
Web layer contracts and DTOs
- Use case interfaces
- Request/Response DTOs
- Web-specific exceptions

#### `backend-port-infra`
Infrastructure contracts
- Repository interfaces
- External service contracts
- Cache and persistence ports

### Adapter Modules

#### `backend-adapter-web`
Web layer implementation
- REST controllers and handlers
- Functional routing with Spring WebFlux
- Authentication filters
- Global error handling

#### `backend-adapter-infra`
Infrastructure implementation
- Database repositories with R2DBC
- Redis cache adapters
- External API clients (OAuth, S3, FCM)
- Event processing and workers

#### `backend-common`
Shared utilities and common functionality
- Coroutine utilities
- Logging utilities
- Common data structures
- Snowflake ID generator

## 🛠️ Development Setup

### Prerequisites
- **Java 17+**
- **Docker & Docker Compose**
- **Gradle 8.0+**
- **IntelliJ IDEA** (recommended)

### Local Development

1. **Clone the repository**
   ```bash
   git clone <repository-url>
   cd clean-architecture-spring-boot-coroutine
   ```

2. **Start infrastructure services**
   ```bash
   docker-compose up -d
   ```
   This starts:
   - Redis (port 6379)
   - LocalStack S3 (port 4566)

3. **Build the project**
   ```bash
   ./gradlew build
   ```

4. **Run the application**
   ```bash
   ./gradlew :backend-bootstrap:bootRun
   ```

The application will start on `http://localhost:3001`

### Environment Configuration

The application uses YAML-based configuration with profiles:

- `application.yml` - Main configuration
- `persistence.yml` - Database and cache configuration  
- `web-client.yml` - External service configuration

## 🧪 Testing

### Running Tests
```bash
# Run all tests
./gradlew test

# Run tests for specific module
./gradlew :backend-application:test

# Run tests with coverage
./gradlew jacocoTestReport
```

### Test Structure
- **Unit Tests**: Business logic testing with MockK
- **Integration Tests**: Database and external service integration
- **Contract Tests**: API contract validation

## 📊 API Documentation

The project includes Insomnia API collection (`Insomnia_2025-08-20.yaml`) with:
- Authentication endpoints
- Post and comment management
- Search and discovery APIs
- File upload/download
- Notification APIs

## 🔧 Development Guidelines

### Code Style
- **Kotlin** conventions with explicit types where needed
- **Immutable** data classes and functional programming patterns
- **Coroutines** for asynchronous operations
- **Clean Code** principles with meaningful names

### Architecture Rules
- **Dependency Direction**: Dependencies point inward toward the domain
- **No Framework Dependencies** in domain layer
- **Ports and Adapters** pattern for external integrations
- **Event-Driven** communication between bounded contexts

### Performance Considerations
- Use **coroutines** for I/O operations
- Implement **caching** strategically
- Apply **database indexing** for query optimization
- Use **connection pooling** for external services

## 🚀 Deployment

### Docker Support
```dockerfile
# Multi-stage build support
FROM gradle:8.0-jdk17 AS build
FROM openjdk:17-jre-slim AS runtime
```

### Production Configuration
- Environment-specific configuration files
- Secrets management with environment variables
- Health checks and monitoring endpoints
- Graceful shutdown handling

## 📈 Monitoring & Observability

### Logging
- Structured logging with Logback
- Correlation IDs for request tracing
- Centralized error handling

### Metrics
- Custom metrics for business operations
- Performance monitoring
- Cache hit/miss ratios

## 🤝 Contributing

1. Fork the repository
2. Create a feature branch (`git checkout -b feature/amazing-feature`)
3. Commit your changes (`git commit -m 'Add amazing feature'`)
4. Push to the branch (`git push origin feature/amazing-feature`)
5. Open a Pull Request

### Development Standards
- Follow existing code style and architecture patterns
- Write comprehensive tests for new features
- Update documentation for API changes
- Ensure all tests pass before submitting PR

## 📄 License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

## 🙏 Acknowledgments

- Spring Boot team for the excellent reactive framework
- Kotlin team for coroutines support
- Clean Architecture principles by Robert C. Martin
- Hexagonal Architecture pattern by Alistair Cockburn

---

**Built with ❤️ using Clean Architecture, Spring Boot, and Kotlin Coroutines**