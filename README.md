# IRMS (Intelligent Restaurant Management System)

A microservices-based restaurant management system built with Spring Boot, Docker, and Supabase.

## System Architecture
- **API Gateway**: Entry point for all clients (`localhost:8080`)
- **Service Discovery**: Netflix Eureka (`localhost:8761`)
- **Auth Service**: Handles JWT and Authentication (`localhost:8081`)
- **Menu Service**: Manages restaurant menus and items (`localhost:8082`)
- **Ordering Service**: Manages customer orders (`localhost:8085`)
- **KDS Service**: Kitchen Display System handling ticket states (`localhost:8089`)

---

## 🚀 How to Run the Project (Docker Compose)

### 1. Prerequisites
Ensure you have a `.env` file in the root directory (next to `docker-compose.yml`). Without this file, the services will fail to connect to the databases and message broker.

Example `.env` file:
```properties
# Supabase Database URLs *must* include project reference option
ORDER_SUPABASE_URL=jdbc:postgresql://aws-0-xxxx.pooler.supabase.com:6543/postgres?options=project=YOUR_PROJECT_REF

AUTH_SUPABASE_USER=postgres.YOUR_PROJECT_REF
AUTH_SUPABASE_PASSWORD=your_password

MENU_SUPABASE_USER=postgres.YOUR_PROJECT_REF
MENU_SUPABASE_PASSWORD=your_password

ORDER_SUPABASE_USER=postgres.YOUR_PROJECT_REF
ORDER_SUPABASE_PASSWORD=your_password

# Message Broker (RabbitMQ via CloudAMQP)
cloudamqp_host=your_amqp_host
cloudamqp_username=your_username
cloudamqp_password=your_password
cloudamqp_vhost=your_vhost

# JWT configuration
JWT_KEYSTORE_PASSWORD=your_keystore_password
JWT_KEY_PASSWORD=your_key_password
```

### 2. Start the Services
Run the following command in the root folder containing the `docker-compose.yml` file:

```bash
# Build and start all services in the background
docker compose up -d --build
```

You can view the logs for a specific service using:
```bash
docker compose logs -f kds-service
```

### 3. Stop the Services
To stop and remove containers cleanly:
```bash
docker compose down
```

---

## 📡 WebSocket Endpoints & Usage

Both the KDS Service and the Ordering Service use STOMP over WebSockets to provide real-time updates.

You can connect directly to them via the API Gateway using STOMP.js or similar clients. Both services also include built-in test pages to easily test these endpoints.

### KDS Service (Kitchen Display System)
The KDS service broadcasts real-time kitchen tickets to screens/clients.

- **WebSocket URL**: `ws://localhost:8080/ws/kds` (via API gateway) or `ws://localhost:8089/ws/kds` (direct)
- **Topics**:
  - `/topic/kitchen/new-tickets` (Fired when a new ticket arrives in the kitchen)
  - `/topic/kitchen/ticket-updates` (Fired when a ticket state changes, e.g., PREPARING)
  - `/topic/kitchen/completed-tickets` (Fired when a ticket is marked DONE)

👉 **Test UI**: Navigate to `http://localhost:8089/ws-test.html` while the KDS service is running to use the interactive KDS WebSocket client.

### Ordering Service (Order Status)
The Ordering service broadcasts real-time status updates back to customers.

- **WebSocket URL**: `ws://localhost:8080/ws/order` (via API gateway) or `ws://localhost:8085/ws/order` (direct)
- **Topics**:
  - `/topic/orders/status` (Global topic for all order status updates)
  - `/topic/orders/{orderId}/status` (Subscribe passing your specific `orderId` to listen to a single order)

👉 **Test UI**: Navigate to `http://localhost:8085/ws-test.html` while the Ordering service is running to test order topic subscriptions.