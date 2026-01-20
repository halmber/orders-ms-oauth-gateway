# Email Sender Service

A microservice for reliable email delivery with retry mechanism and distributed locking.

## Features

- ✅ **Asynchronous Email Processing** via Kafka
- ✅ **Automatic Retry** for failed emails with scheduled job
- ✅ **Elasticsearch Storage** for email tracking and history
- ✅ **Idempotent Processing** - prevents duplicate emails
- ✅ **Distributed Locking** using ShedLock to prevent concurrent retry attempts
- ✅ **Status Tracking** - PENDING, SENT, FAILED with error details

## Tech Stack

- **Java 21**
- **Spring Boot 3.x**
- **Spring Kafka** for message consumption
- **Elasticsearch** for email storage
- **JavaMailSender** for SMTP
- **ShedLock** for distributed locks
- **Testcontainers** for integration tests

## Architecture

```
Kafka (emailSend topic)
    ↓
EmailSendMessageListener
    ↓
EmailService
    ↓
JavaMailSender → SMTP Server
    ↓
Elasticsearch (email_messages index)
```

## Configuration

Required environment variables:

```bash
ES_ADDRESS=elasticsearch:9200
KAFKA_ADDRESS=kafka:9092
MAIL_HOST=smtp.gmail.com
MAIL_PORT=587
MAIL_USERNAME=your-email@gmail.com
MAIL_PASSWORD=your-app-password
```

## Email Message Format

```json
{
  "id": "unique-email-id",
  "recipientEmail": "user@example.com",
  "subject": "Email Subject",
  "content": "Email body content"
}
```

## Retry Mechanism

- Failed emails are automatically retried every 5 minutes (configurable)
- Retry count is incremented on each attempt
- Error messages are stored for debugging
- Distributed lock ensures only one instance processes retries

## Testing

Run all tests:
```bash
mvn test
```

Tests include:
- Email sending success scenarios
- Failure handling and retry logic
- Kafka message consumption
- Elasticsearch integration
- Duplicate message handling

---