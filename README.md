# FX Deals Importer

A Spring Boot application for importing and managing FX (Foreign Exchange) deals data warehouse for Bloomberg.

## Features

- **Deal Import**: Accept and persist FX deal details into MySQL database
- **Validation**: Comprehensive validation for all deal fields
- **Duplicate Prevention**: System prevents importing the same deal twice
- **No Rollback**: Individual deal persistence without transaction rollback
- **REST API**: RESTful endpoints for deal operations
- **Error Handling**: Proper exception handling and logging
- **Unit Testing**: Comprehensive test coverage
- **Docker Support**: Containerized deployment with Docker Compose

## Technology Stack

- **Java 17**
- **Spring Boot 3.5.7**
- **Spring Data JPA**
- **MySQL 8.0**
- **Maven**
- **Docker & Docker Compose**
- **JUnit 5 & Mockito**
- **Lombok**

## Project Structure

```
fx-deals-importer/
├── src/
│   ├── main/
│   │   ├── java/org/example/fxdealsimporter/
│   │   │   ├── controller/          # REST Controllers
│   │   │   │   └── DealController.java
│   │   │   ├── dto/                 # Data Transfer Objects
│   │   │   │   ├── BatchImportResponse.java
│   │   │   │   ├── DealRequest.java
│   │   │   │   └── DealResponse.java
│   │   │   ├── entity/              # JPA Entities
│   │   │   │   └── Deal.java
│   │   │   ├── exception/           # Custom Exceptions
│   │   │   │   ├── DuplicateDealException.java
│   │   │   │   ├── GlobalExceptionHandler.java
│   │   │   │   ├── InvalidCurrencyException.java
│   │   │   │   └── InvalidDealException.java
│   │   │   ├── mapper/              # MapStruct Mappers
│   │   │   │   └── DealMapper.java
│   │   │   ├── repository/          # Data Repositories
│   │   │   │   └── DealRepository.java
│   │   │   ├── service/             # Business Logic
│   │   │   │   ├── Impl/
│   │   │   │   │   ├── CurrencyValidationService.java
│   │   │   │   │   └── DealService.java
│   │   │   │   ├── ICurrencyValidationService.java
│   │   │   │   └── IDealService.java
│   │   │   └── FxDealsImporterApplication.java
│   │   └── resources/
│   │       ├── db/changelog/        # Liquibase Database Migration
│   │       │   ├── 000-create-database.sql
│   │       │   ├── 001-create-deals-table.sql
│   │       │   └── db.changelog-master.xml
│   │       └── application.properties
│   └── test/                        # Unit Tests
│       └── java/org/example/fxdealsimporter/
│           ├── controller/
│           │   └── DealControllerTest.java
│           ├── service/
│           │   ├── CurrencyValidationServiceTest.java
│           │   └── DealServiceTest.java
│           └── FxDealsImporterApplicationTests.java
├── logs/                            # Application Logs
├── target/                          # Maven Build Output
├── docker-compose.yml               # Docker Compose configuration
├── Dockerfile                       # Docker image configuration
├── Makefile                         # Build automation commands
├── pom.xml                          # Maven configuration
└── README.md
```

## API Endpoints

### Import Deal
```http
POST /api/deals
Content-Type: application/json

{
  "dealUniqueId": "FX001",
  "fromCurrencyIsoCode": "USD",
  "toCurrencyIsoCode": "EUR",
  "dealTimestamp": "2024-01-15T10:30:00",
  "dealAmount": 1500.75
}
```

### Import Multiple Deals (Batch)
```http
POST /api/deals/batch
Content-Type: application/json

[
  {
    "dealUniqueId": "FX001",
    "fromCurrencyIsoCode": "USD",
    "toCurrencyIsoCode": "EUR",
    "dealTimestamp": "2024-01-15T10:30:00",
    "dealAmount": 1500.75
  },
  {
    "dealUniqueId": "FX002",
    "fromCurrencyIsoCode": "GBP",
    "toCurrencyIsoCode": "JPY",
    "dealTimestamp": "2024-01-15T11:00:00",
    "dealAmount": 2000.00
  }
]
```

## Validation Rules

- **dealUniqueId**: Required, must be unique
- **fromCurrencyIsoCode**: Required, exactly 3 uppercase letters (e.g., USD)
- **toCurrencyIsoCode**: Required, exactly 3 uppercase letters (e.g., EUR)
- **dealTimestamp**: Required, valid ISO datetime format
- **dealAmount**: Required, positive decimal number with max 15 digits and 2 decimal places

## Quick Start

### Prerequisites
- Java 17+
- Maven 3.6+
- Docker & Docker Compose

### Setup

1. **Clone and Build**:
   ```bash
   git clone <repository-url>
   cd fx-deals-importer
   mvn clean package -DskipTests
   ```

2. **Start Services**:
   ```bash
   docker-compose up -d
   ```

3. **Access Application**:
   - Application: http://localhost:8080
   - MySQL: localhost:3306 (root/password)

## Testing

### Run Unit Tests
```bash
mvn test
```

### Test Coverage
The project includes comprehensive unit tests for:
- Service layer business logic
- Controller layer REST endpoints
- Exception handling scenarios
- Validation rules

### Manual API Testing

1. **Import a Deal**:
   ```bash
   curl -X POST http://localhost:8080/api/deals \
     -H "Content-Type: application/json" \
     -d '{
       "dealUniqueId": "TEST001",
       "fromCurrencyIsoCode": "USD",
       "toCurrencyIsoCode": "EUR",
       "dealTimestamp": "2024-01-15T10:30:00",
       "dealAmount": 1500.75
     }'
   ```

2. **Import Multiple Deals**:
   ```bash
   curl -X POST http://localhost:8080/api/deals/batch \
     -H "Content-Type: application/json" \
     -d '[
       {
         "dealUniqueId": "BATCH001",
         "fromCurrencyIsoCode": "USD",
         "toCurrencyIsoCode": "EUR",
         "dealTimestamp": "2024-01-15T10:30:00",
         "dealAmount": 1500.75
       },
       {
         "dealUniqueId": "BATCH002",
         "fromCurrencyIsoCode": "GBP",
         "toCurrencyIsoCode": "JPY",
         "dealTimestamp": "2024-01-15T11:00:00",
         "dealAmount": 2000.00
       }
     ]'
   ```

3. **Test Duplicate Prevention**:
   ```bash
   # Import the same deal again - should return 409 Conflict
   curl -X POST http://localhost:8080/api/deals \
     -H "Content-Type: application/json" \
     -d '{
       "dealUniqueId": "TEST001",
       "fromCurrencyIsoCode": "USD",
       "toCurrencyIsoCode": "EUR",
       "dealTimestamp": "2024-01-15T10:30:00",
       "dealAmount": 1500.75
     }'
   ```

## Database Schema

```sql
CREATE TABLE deals (
    deal_unique_id VARCHAR(255) PRIMARY KEY,
    from_currency_iso_code VARCHAR(3) NOT NULL,
    to_currency_iso_code VARCHAR(3) NOT NULL,
    deal_timestamp DATETIME NOT NULL,
    deal_amount DECIMAL(17,2) NOT NULL,
    created_at DATETIME NOT NULL
);
```

## Error Handling

The application provides comprehensive error handling:

- **400 Bad Request**: Validation errors
- **409 Conflict**: Duplicate deal attempts
- **500 Internal Server Error**: Unexpected errors

Example error response:
```json
{
  "timestamp": "2024-01-15T10:30:00",
  "status": 409,
  "error": "Duplicate Deal",
  "message": "Deal with ID TEST001 already exists"
}
```

## Logging

The application uses SLF4J with Logback for comprehensive logging:
- INFO level for business operations
- DEBUG level for detailed flow
- ERROR level for exceptions
- Structured log format with timestamps

## Development

### Local Development Setup
1. Start MySQL locally or use Docker:
   ```bash
   docker run -d --name mysql -e MYSQL_ROOT_PASSWORD=password -e MYSQL_DATABASE=deals -p 3306:3306 mysql:8.0
   ```

2. Run application:
   ```bash
   mvn spring-boot:run
   ```

### Available Commands
- `mvn clean compile` - Compile the application
- `mvn test` - Run unit tests
- `mvn clean package -DskipTests` - Create JAR file
- `mvn spring-boot:run` - Run locally
- `mvn clean` - Clean build artifacts
- `docker-compose up -d` - Start with Docker Compose
- `docker-compose down` - Stop services
- `docker-compose run --rm test` - Run tests in Docker
- `docker-compose logs -f app` - View application logs
- `docker-compose exec mysql mysql -u root deals` - Access database shell

## Production Considerations

- Configure proper database connection pooling
- Set up monitoring and health checks
- Implement proper security measures
- Configure log aggregation
- Set up backup strategies for the database
- Consider horizontal scaling for high throughput

## License

This project is developed as part of a technical assessment for ProgressSoft Corporation.# fx-deals
