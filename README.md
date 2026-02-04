# Orders API with OAuth Gateway

This is an enhanced version of the original [Spring Orders API Evented ms](https://github.com/halmber/spring-orders-evented-ms) with added email notification functionality using microservices architecture.

## Features

- ✅ **RESTful API** - Customer and order management with CRUD operations
- ✅ **Google OAuth 2.0** - Secure authentication via API Gateway
- ✅ **Event-Driven Architecture** - Kafka for async email notifications
- ✅ **Advanced Filtering** - Pagination, sorting, and multi-field filters
- ✅ **Report Generation** - Export to CSV/Excel with streaming for large datasets
- ✅ **Bulk Import** - JSON file upload with validation and error tracking
- ✅ **Email Tracking** - Elasticsearch storage with retry mechanism
- ✅ **Service Discovery** - Consul for dynamic service registration
- ✅ **Session Management** - Redis for distributed sessions

## Architecture

```
Frontend (React) → Gateway (OAuth) → Orders API → PostgreSQL
                      ↓                    ↓
                   Redis              Kafka → Email Sender → Elasticsearch
                                                    ↓
                                                 SMTP
```

### Services

| Service | Port | Technology | Purpose |
|---------|------|------------|---------|
| **Gateway** | 1000 | Spring Cloud Gateway | OAuth, routing, sessions |
| **Orders API** | 8080 | Spring Boot | CRUD, reports, validation |
| **Email Sender** | 8081 | Spring Boot | Async email delivery |
| **Frontend** | 3000 | React | User interface |

### Infrastructure

- **PostgreSQL** - Orders and customers data
- **Kafka + Zookeeper** - Event streaming
- **Redis** - Session storage
- **Elasticsearch** - Email tracking
- **Consul** - Service discovery

## Quick Start

### Prerequisites

- Docker & Docker Compose
- Java 21+
- Node.js 18+ (for frontend)
- Maven 3.9+

### 1. Clone All Repositories

```bash
# Backend services
git clone https://github.com/halmber/orders-ms-oauth-gateway.git
cd orders-ms-oauth-gateway

# Frontend (separate repo)
git clone https://github.com/halmber/orders-react-client.git
```

### 2. Configure Environment

Create `.env` file in `orders-ms-oauth-gateway`:

```env
# Email Configuration
MAIL_HOST=smtp.gmail.com
MAIL_PORT=587
MAIL_USERNAME=your-email@gmail.com
MAIL_PASSWORD=your-app-password

# Google OAuth
OAUTH_GOOGLE_CLIENT_ID=your-client-id.apps.googleusercontent.com
OAUTH_GOOGLE_CLIENT_SECRET=GOCSPX-your-secret
OAUTH_GOOGLE_SCOPE=openid email profile

# Frontend URL (for CORS and OAuth redirect)
FRONTEND_URL=http://localhost:3000

# Redis (optional, defaults work with docker-compose)
REDIS_HOST=redis
REDIS_PORT=6379
REDIS_USERNAME=
REDIS_PASSWORD=
```

### 3. Start Infrastructure & Services

```bash
# Start all services with Docker Compose
docker-compose up -d

# Check status
docker-compose ps

# View logs
docker-compose logs -f orders-api
docker-compose logs -f email-sender
docker-compose logs -f gateway
```

Services will be available at:
- Gateway: http://localhost:1000
- Orders API: http://localhost:8080
- Email Sender: http://localhost:8081
- Frontend: http://localhost:3000

### 4. Access the Application

Open http://localhost:3000 and sign in with Google.

## Google OAuth Setup

1. Go to [Google Cloud Console](https://console.cloud.google.com/)
2. Create a new project or select existing
3. Enable **Google+ API**
4. Create OAuth 2.0 credentials:
   - Application type: **Web application**
   - Authorized redirect URIs:
     - `http://localhost:1000/oauth/callback` (local)
     - `http://YOUR_INGRESS_IP/oauth/callback` (production)
5. Copy Client ID and Secret to `.env`

## API Documentation

Once running, access Swagger UI:

**Orders API**: http://localhost:8080/swagger-ui

### Key Endpoints

```http
# Authentication
POST /oauth/authenticate          # Initiate Google OAuth flow
GET  /oauth/callback              # OAuth callback (handled by Gateway)
GET  /api/profile                 # Get current user info

# Customers
GET    /api/customers             # List customers (paginated)
POST   /api/customers             # Create customer
GET    /api/customers/{id}        # Get customer
PUT    /api/customers/{id}        # Update customer
DELETE /api/customers/{id}        # Delete customer

# Orders
GET    /api/orders                # List orders (paginated)
POST   /api/orders                # Create order (triggers email)
GET    /api/orders/{id}           # Get order
PUT    /api/orders/{id}           # Update order
DELETE /api/orders/{id}           # Delete order
POST   /api/orders/_list          # Filtered list (body params)
POST   /api/orders/_report        # Generate CSV/Excel report
POST   /api/orders/upload         # Bulk import from JSON

# Email History
GET    /api/emails/sent           # List sent emails
GET    /api/emails/failed         # List failed emails
```

## Development

### Running Individual Services

```bash
# Orders API
cd orders-api-service
mvn spring-boot:run

# Email Sender
cd email-sender-service
mvn spring-boot:run

# Gateway
cd orders-api-gateway
mvn spring-boot:run
```

### Running Tests

```bash
# All services
mvn test

# Specific service
cd orders-api-service
mvn test
```

### Building Docker Images

```bash
# Build all images
docker-compose build

# Build specific service
docker build -t orders-api-service ./orders-api-service
```

## Production Deployment

For Kubernetes deployment on GKE, see the deployment repository:

👉 **[orders-api-deployment](https://github.com/halmber/orders-api-deployment)**

Includes:
- Kustomize manifests
- GKE configuration
- Nginx Ingress setup
- GitHub Actions CI/CD
- Secrets management

## Project Structure

```
orders-ms-oauth-gateway/
├── orders-api-service/          # Main API service
│   ├── src/main/java/           # Spring Boot application
│   ├── src/main/resources/      # Configuration, Liquibase migrations
│   └── Dockerfile
├── email-sender-service/        # Email notification service
│   ├── src/main/java/           # Kafka consumer, email sender
│   └── Dockerfile
├── orders-api-gateway/          # API Gateway with OAuth
│   ├── src/main/java/           # OAuth flow, routing
│   └── Dockerfile
├── docker-compose.yml           # Local development setup
├── .github/workflows/           # CI/CD pipelines
└── README.md
```

## Tech Stack

### Backend
- **Java 21** - Modern Java features
- **Spring Boot 3.x** - Application framework
- **Spring Cloud Gateway** - API Gateway with OAuth
- **Spring Data JPA** - Database access
- **Spring Kafka** - Event streaming
- **MapStruct** - DTO mapping
- **Liquibase** - Database migrations

### Infrastructure
- **PostgreSQL 15** - Relational database
- **Apache Kafka 7.7** - Message broker
- **Redis 7** - Session storage
- **Elasticsearch 8.6** - Email tracking
- **Consul 1.20** - Service discovery

### DevOps
- **Docker** - Containerization
- **Kubernetes (GKE)** - Orchestration
- **GitHub Actions** - CI/CD
- **Kustomize** - K8s configuration management

## Key Features Explained

### 1. OAuth Authentication Flow

```
User → Frontend → Gateway → Google OAuth
                      ↓
                 Create Session
                      ↓
                  Store in Redis
                      ↓
               Set Secure Cookie
                      ↓
            Redirect to Frontend
```

### 2. Email Notification Flow

```
Create Order → Kafka Event → Email Sender
                                  ↓
                          Send via SMTP
                                  ↓
                       Save to Elasticsearch
                                  ↓
                       Retry on Failure
```

### 3. Report Generation

- **Streaming** - Processes millions of records without OOM
- **Formats** - CSV and Excel (XLSX)
- **Filters** - Customer, status, payment method
- **Performance** - Hibernate streaming + Apache POI SXSSF

### 4. Bulk Import

- **Streaming Parser** - Jackson streaming for large files
- **Validation** - Comprehensive error tracking with line numbers
- **Batch Processing** - 50 records per transaction
- **Partial Success** - Saves valid records even if some fail

## Troubleshooting

### Services Not Starting

```bash
# Check logs
docker-compose logs -f

# Restart specific service
docker-compose restart orders-api

# Rebuild and restart
docker-compose up -d --build orders-api
```

### Database Connection Issues

```bash
# Check PostgreSQL is running
docker-compose ps postgres

# View PostgreSQL logs
docker-compose logs postgres

# Connect to database
docker-compose exec postgres psql -U postgres -d orders
```

### Kafka Connection Issues

```bash
# Check Kafka and Zookeeper
docker-compose ps kafka zookeeper

# View Kafka logs
docker-compose logs kafka

# List topics
docker-compose exec kafka kafka-topics --list --bootstrap-server localhost:9092
```

### OAuth Issues

1. Verify `FRONTEND_URL` in `.env` matches your actual frontend URL
2. Check Google OAuth redirect URIs include: `http://localhost:1000/oauth/callback`
3. Ensure Gateway can reach Redis: `docker-compose logs gateway`

## License

MIT License

## Related Repositories

- **[orders-react-client](https://github.com/halmber/orders-react-client)** - Frontend application
- **[orders-api-deployment](https://github.com/halmber/orders-api-deployment)** - Kubernetes manifests

## Support

- **Issues**: [GitHub Issues](https://github.com/halmber/orders-ms-oauth-gateway/issues)
- **API Docs**: http://localhost:8080/swagger-ui (when running locally)
