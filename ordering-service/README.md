# Ordering Service

Part of the **Intelligent Restaurant Management System (IRMS)** microservices platform.

`ordering-service` receives guest orders, validates menu availability through `menu-service`, stores orders in PostgreSQL, publishes `order.created` events to RabbitMQ, and consumes KDS status updates to keep order state in sync.

## Architecture

```text
com.hcmut.irms.ordering_service
├── controller/            REST API
├── adapter/
│   ├── external/          menu-service client
│   ├── messaging/         RabbitMQ publisher + listener
│   └── persistence/       Spring Data JPA adapter
├── usecase/
│   ├── create/            create order flow
│   ├── get/               read order flow
│   └── update/            apply KDS status updates
├── domain/                Order, OrderItem, OrderStatus, exceptions
├── port/                  repository, event, and availability ports
├── dto/                   API, event, and external DTOs
└── config/                security, RabbitMQ, and RestClient setup
```

Dependency direction:

```text
Controller -> UseCase -> Domain
                     -> Ports -> Adapters
```

## Runtime Dependencies

- PostgreSQL for `orders` and `order_items`
- RabbitMQ / CloudAMQP for `restaurant.events`
- `service-discovery` for service-name resolution
- `auth-service` for JWKS lookup
- `menu-service` for item availability checks

Important runtime note:

- The service does **not** call `auth-service` or `menu-service` via localhost URLs.
- JWT verification uses `http://auth-service/api/auth/.well-known/jwks.json`.
- Menu availability checks use a load-balanced `RestClient` with base URL `http://menu-service`.
- Because of that, local runs depend on Eureka registration unless you change code or configuration.

## Configuration

### Environment Variables

```env
# PostgreSQL
SUPABASE_DB_URL=jdbc:postgresql://<host>:6543/postgres
SUPABASE_DB_USER=<username>
SUPABASE_DB_PASSWORD=<password>

# RabbitMQ / CloudAMQP
cloudamqp_host=<host>
cloudamqp_username=<username>
cloudamqp_password=<password>
cloudamqp_vhost=<vhost>

# Optional Eureka overrides
EUREKA_DEFAULT_ZONE=http://localhost:8761/eureka/
EUREKA_INSTANCE_HOSTNAME=localhost
```

### Key `application.yml` Properties

| Key | Current value/default | Notes |
| --- | --- | --- |
| `server.port` | `8085` | Local HTTP port |
| `app.rabbitmq.exchange` | `restaurant.events` | Shared exchange |
| `app.rabbitmq.order-created-routing-key` | `order.created` | Published after order creation |
| `app.rabbitmq.order-status-queue` | `ordering.kds.status` | Queue consumed by ordering-service |
| `app.rabbitmq.order-status-routing-key` | `order.status.updated` | Routing key published by KDS |
| `app.security.jwk-set-uri` | `http://auth-service/api/auth/.well-known/jwks.json` | JWT verification source |
| `eureka.client.service-url.defaultZone` | `${EUREKA_DEFAULT_ZONE:http://localhost:8761/eureka/}` | Eureka endpoint |

Current source does **not** expose a configurable `app.menu-service.base-url`.
The outbound menu client is created in `RestClientConfig` with hardcoded base URL `http://menu-service`.

## RabbitMQ Event Flow

```text
ordering-service --[order.created]--------> restaurant.events ----> kds.order.created ----> kds-service
ordering-service <--[order.status.updated]- restaurant.events <---- kds-service
                                          ordering.kds.status
```

### Published event: `KdsOrderCreatedEvent`

```json
{
  "eventId": "uuid",
  "orderId": "1",
  "tableNumber": 5,
  "waiterId": "John Doe",
  "timestamp": "2024-01-01T10:00:00Z",
  "items": [
    {
      "menuItemId": "uuid-of-menu-item",
      "itemName": "Burger",
      "quantity": 2,
      "customizations": ["no onions"]
    }
  ]
}
```

### Consumed event: `KdsStatusEvent`

```json
{
  "eventId": "uuid",
  "orderId": "1",
  "newStatus": "READY",
  "timestamp": "2024-01-01T10:15:00Z",
  "updatedBy": "KDS-Station-HotLine"
}
```

## Security

The service is a JWT resource server. Swagger endpoints are public; order endpoints require a valid token from `auth-service`.

| Endpoint | Required role |
| --- | --- |
| `POST /api/orders` | `MANAGER` or `SERVER` |
| `GET /api/orders` | `MANAGER` or `SERVER` |
| `GET /api/orders/{id}` | `MANAGER` or `SERVER` |

Send the token as:

```text
Authorization: Bearer <jwt>
```

## Running Locally

### Prerequisites

- Java 21
- RabbitMQ / CloudAMQP credentials
- PostgreSQL credentials
- `service-discovery` reachable at the URL used by `EUREKA_DEFAULT_ZONE`
- `auth-service` registered in Eureka as `auth-service`
- `menu-service` registered in Eureka as `menu-service`

### Recommended startup order

1. Start `service-discovery`
2. Start `auth-service`
3. Start `menu-service`
4. Start `ordering-service`

By default, clients expect Eureka at `http://localhost:8761/eureka/`.
If your `service-discovery` process is running on `8080`, override `EUREKA_DEFAULT_ZONE` accordingly before starting this service.

### Start command

```bash
cd ordering-service
./mvnw spring-boot:run
```

On Windows, use `mvnw.cmd` instead of `./mvnw`.

The service starts on `http://localhost:8085`.

## API Reference

- Swagger UI: `http://localhost:8085/swagger-ui.html`
- Create order: `POST /api/orders`
- Get one order: `GET /api/orders/{orderId}`
- Get all orders: `GET /api/orders`

Example create request:

```json
{
  "tableNumber": "5",
  "staffName": "John Doe",
  "items": [
    {
      "menuItemId": "2eaed93b-cd7b-40b7-a3ef-cd6b2fdb2781",
      "name": "Burger",
      "quantity": 2,
      "customizations": ["No onions", "Extra cheese"]
    }
  ]
}
```

## Error Behavior

| HTTP | Typical scenario |
| --- | --- |
| `400` | Invalid enum or other `IllegalArgumentException` |
| `401` | Missing or invalid JWT |
| `403` | Valid JWT but wrong role |
| `404` | Order not found |
| `409` | Invalid status transition |
| `422` | One or more menu items unavailable |

Request DTOs currently do not use Bean Validation, so incomplete payloads are not guaranteed to fail with a clean `400`.

## Build

```bash
./mvnw clean package -DskipTests
```

The fat JAR is produced under `target/`.
