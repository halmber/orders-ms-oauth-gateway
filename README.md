# Orders API with Email Notifications

This is an enhanced version of the original [Spring Orders API](https://github.com/halmber/spring-orders-api) with added email notification functionality using microservices architecture.

## What's New

Asynchronous email notifications using Kafka and Elasticsearch

- **Email notifications** sent when customers are created or orders are placed
- **Kafka** for reliable message delivery between services
- **Elasticsearch** for email tracking and history
- **Automatic retry** mechanism for failed emails
- **Distributed processing** with ShedLock

## Architecture

```
orders-api-service (Producer)
       ↓
Kafka (emailSend topic)
       ↓
email-sender-service (Consumer)
       ↓
Elasticsearch (email_messages)
```

## Services

### 1. Orders API Service (Port 8080)
- Customer and order management
- Sends email notifications to Kafka
- PostgreSQL database
- Full REST API with Swagger

### 2. Email Sender Service
- Consumes email messages from Kafka
- Sends emails via SMTP
- Stores email history in Elasticsearch
- Automatic retry for failed emails

## Running the Project

### Prerequisites
- Docker and Docker Compose
- Java 21 (for local development)
- Maven (for local development)

### Option 1: Run with Docker Compose (Recommended)

1. Create `.env` file from `.env.example` in project root:
```bash
MAIL_HOST=smtp.gmail.com
MAIL_PORT=587
MAIL_USERNAME=your-email@gmail.com
MAIL_PASSWORD=your-app-password
```

2. Start all services:
```bash
docker-compose up -d
```

This will start:
- PostgreSQL (port 5433)
- Elasticsearch (port 9200)
- Kibana (port 5601)
- Zookeeper (port 2181)
- Kafka (ports 9092, 9093)
- Orders API Service (port 8080)
- Email Sender Service

3. Access services:
- **Swagger UI**: http://localhost:8080/swagger-ui
- **Kibana**: http://localhost:5601

### Option 2: Run Locally

1. Start infrastructure only:
```bash
docker-compose up -d postgres elasticsearch kafka zookeeper
```

2. Set environment variables:
```bash
export KAFKA_ADDRESS=localhost:9093
export ES_ADDRESS=localhost:9200
export MAIL_HOST=smtp.gmail.com
export MAIL_PORT=587
export MAIL_USERNAME=your-email@gmail.com
export MAIL_PASSWORD=your-app-password
```

3. Run services:
```bash
# Terminal 1 - Orders API
cd orders-api-service
mvn spring-boot:run

# Terminal 2 - Email Sender
cd email-sender-service
mvn spring-boot:run
```

## Running Tests

### Orders API Service
```bash
cd orders-api-service
mvn test
```

### Email Sender Service
```bash
cd email-sender-service
mvn test
```

Tests use:
- **H2** in-memory database (Orders API)
- **Embedded Kafka** for messaging
- **Testcontainers** for Elasticsearch

### Run All Tests
```bash
mvn test -pl orders-api-service,email-sender-service
```

## Testing Email Flow

1. Create a customer:
```bash
curl -X POST http://localhost:8080/api/customers \
  -H "Content-Type: application/json" \
  -d '{
    "firstName": "John",
    "lastName": "Doe",
    "email": "john.doe@example.com",
    "phone": "+380501234567",
    "city": "Kyiv"
  }'
```

2. Check email was sent in Kibana:
- Go to http://localhost:5601
- Create data view for `email_messages` index
- View sent emails

3. Check email was sent to actual email address mentioned in request body.

4. Check logs for obtaining relevant logs on the status of sending messages to Kafka and their processing by the listener.

## Stopping the Project

```bash
docker-compose down

# To remove volumes (data will be lost)
docker-compose down -v
```

## Documentation

- **Orders API**: See [orders-api-service/README.md](orders-api-service/README.md)
- **Email Sender**: See [email-sender-service/README.md](email-sender-service/README.md)

## License

MIT