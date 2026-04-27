# Ordering Service

> Part of the **Intelligent Restaurant Management System (IRMS)** microservices platform.

The ordering-service is the central hub for guest orders. It receives orders from front-of-house staff, checks menu availability, persists orders in PostgreSQL, and broadcasts them to the Kitchen Display System (KDS) via RabbitMQ. It also listens for KDS status updates and applies them back to the order.

---

## Architecture

```
com.hcmut.irms.ordering_service
├── controller/            REST API (no business logic)
├── adapter/
│   ├── persistence/       Spring Data JPA (OrderRepositoryAdapter)
│   ├── messaging/         RabbitMQ publisher + listener
│   └── external/          Menu-service REST client
├── usecase/
│   ├── create/            CreateOrderUseCase + CreateOrderService
│   ├── get/               GetOrderUseCase + GetOrderService
│   └── update/            UpdateOrderStatusUseCase + UpdateOrderStatusService
├── domain/                Order, OrderItem, OrderStatus (enum), exceptions
├── port/                  OrderRepositoryPort, MenuAvailabilityPort, OrderEventPublisherPort
├── dto/
│   ├── api/               CreateOrderRequest, OrderResponse (REST layer)
│   ├── event/             KdsOrderCreatedEvent, KdsStatusEvent (RabbitMQ)
│   └── external/          MenuItemResponse (menu-service response)
└── config/                Security, RabbitMQ, RestClient configs
```

### Dependency Direction

```
Controller → UseCase → Domain
                ↓
             Ports
                ↓
           Adapters
```

---

## Configuration

### Environment Variables (`.env`)

Copy and fill in your values:

```env
# PostgreSQL / Supabase
SUPABASE_DB_URL=jdbc:postgresql://<host>:6543/postgres?
SUPABASE_DB_USER=postgres.<project-ref>
SUPABASE_DB_PASSWORD=<your-password>

# CloudAMQP (same credentials as kds-service)
cloudamqp_host=<your>.cloudamqp.com
cloudamqp_username=<username>
cloudamqp_password=<password>
cloudamqp_vhost=<vhost>
```

### Key `application.yml` properties

| Key | Default | Description |
|-----|---------|-------------|
| `server.port` | `8083` | Service port |
| `app.rabbitmq.exchange` | `restaurant.events` | Shared RabbitMQ exchange |
| `app.rabbitmq.order-created-routing-key` | `order.created` | Key for publishing to KDS |
| `app.rabbitmq.order-status-queue` | `ordering.kds.status` | Queue we consume KDS replies on |
| `app.rabbitmq.order-status-routing-key` | `order.status.updated` | Routing key KDS publishes on |
| `app.menu-service.base-url` | `http://localhost:8082` | Menu-service base URL |
| `spring.security.oauth2.resourceserver.jwt.jwk-set-uri` | `http://localhost:8081/api/auth/.well-known/jwks.json` | JWKS endpoint |

---

## RabbitMQ Event Flow

```
ordering-service  ──[order.created]──►  restaurant.events  ──►  kds.order.created  ──►  kds-service
ordering-service  ◄─[order.status.updated]──  restaurant.events  ◄──  kds-service
                                    ordering.kds.status (our queue)
```

### `KdsOrderCreatedEvent` (published by ordering → consumed by KDS)

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

### `KdsStatusEvent` (published by KDS → consumed by ordering)

```json
{
  "eventId": "uuid",
  "orderId": "1",
  "newStatus": "READY",
  "timestamp": "2024-01-01T10:15:00Z",
  "updatedBy": "KDS-Station-HotLine"
}
```

---

## Order Status Flow

```
CREATED  →  COOKING  →  READY  →  SERVED
   └──────────────────►┘
   (KDS sends READY directly; COOKING → READY also valid)
```

Invalid transitions throw `InvalidStatusTransitionException` (HTTP 409).

---

## Security

All endpoints require a valid JWT (issued by `auth-service`, verified via JWKS).  
Role requirements:

| Endpoint | Required Role |
|----------|--------------|
| `POST /api/orders` | `MANAGER` or `SERVER` |
| `GET /api/orders/{id}` | `MANAGER` or `SERVER` |

Include the token in every request:
```
Authorization: Bearer <your-jwt-token>
```

---

## Running Locally

### Prerequisites

- Java 21
- Maven (or use the included `mvnw.cmd`)
- PostgreSQL/Supabase credentials in `.env`
- RabbitMQ/CloudAMQP credentials in `.env`
- `auth-service` running on port 8081
- `menu-service` running on port 8082 (for availability checks)

### Start

```bash
cd ordering-service
.\mvnw.cmd spring-boot:run
```

Service starts on **http://localhost:8083**

---

## API Reference & Testing

### Swagger UI

```
http://localhost:8083/swagger-ui.html
```

### Step-by-Step Test Flow

#### 1. Get a JWT token

Call auth-service to log in as a `MANAGER` or `SERVER`:

```bash
POST http://localhost:8081/api/auth/login
Content-Type: application/json

{
  "username": "your-username",
  "password": "your-password"
}
```

Save the returned `token` value.

---

#### 2. Create an Order

```bash
POST http://localhost:8083/api/orders
Authorization: Bearer <token>
Content-Type: application/json

{
  "tableNumber": "5",
  "staffName": "John Doe",
  "items": [
    {
      "menuItemId": "<uuid-from-menu-service>",
      "name": "Burger",
      "quantity": 2,
      "customizations": ["no onions", "extra cheese"]
    }
  ]
}
```

Expected response `201 Created`:

```json
{
  "id": 1,
  "tableNumber": "5",
  "staffName": "John Doe",
  "status": "CREATED",
  "timestamp": "2024-01-01T10:00:00",
  "items": [
    {
      "id": 1,
      "menuItemId": "<uuid>",
      "name": "Burger",
      "quantity": 2,
      "customizations": ["no onions", "extra cheese"]
    }
  ]
}
```

At this point, a `KdsOrderCreatedEvent` is published to RabbitMQ → KDS picks it up.

---

#### 3. Get an Order

```bash
GET http://localhost:8083/api/orders/1
Authorization: Bearer <token>
```

---

#### 4. Verify RabbitMQ Event (KDS side)

Check CloudAMQP management UI or KDS logs to confirm the `kds.order.created` queue received the message.

---

#### 5. Simulate KDS Status Update

When KDS marks a ticket ready, it publishes to `order.status.updated`. The ordering-service listener picks this up automatically and updates the order status to `READY` in PostgreSQL.

To test manually, publish this message to the `ordering.kds.status` queue (or to exchange `restaurant.events` with routing key `order.status.updated`):

```json
{
  "eventId": "test-uuid",
  "orderId": "1",
  "newStatus": "READY",
  "timestamp": "2024-01-01T10:15:00Z",
  "updatedBy": "KDS-Station-HotLine"
}
```

Then call `GET /api/orders/1` to confirm the status changed to `READY`.

---

## Error Responses

| HTTP | Scenario |
|------|----------|
| `400` | Invalid request (bad enum value, missing fields) |
| `401` | Missing or invalid JWT |
| `403` | Authenticated but wrong role |
| `404` | Order not found |
| `409` | Invalid status transition |
| `422` | One or more menu items are unavailable |

---

## Build

```bash
.\mvnw.cmd clean package -DskipTests
```

The fat JAR is produced at `target/ordering-service-0.0.1-SNAPSHOT.jar`.
