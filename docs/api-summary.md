# API Summary

## Scope

- Đã đọc toàn bộ source chính của `auth-service`, `menu-service`, `ordering-service`, `kds-service`, `api-gateway`, `service-discovery`, phần test liên quan, và `ordering-service/README.md`.
- Repo hiện không có file OpenAPI YAML/JSON commit sẵn. Contract bên dưới được suy ra từ controller, DTO, security config, exception handler, config, và cách Jackson/Spring Boot serialize dữ liệu trong source hiện tại.
- `README.md` ở root hiện đã được cập nhật để mô tả tổng quan hệ thống, nhưng `docker-compose.yml` vẫn đang rỗng nên chưa có contract orchestration hoàn chỉnh ở mức môi trường.

## Service Map

| Service | Port trong source | Storage | Business API |
| --- | --- | --- | --- |
| `auth-service` | `${PORT:8081}` | PostgreSQL, bảng `users` | REST auth + JWKS |
| `menu-service` | `8082` | PostgreSQL, bảng `categories`, `menu_items`, `promotions`, `menu_item_promotions` | REST menu/category/promotion |
| `ordering-service` | `8085` | PostgreSQL, bảng `orders`, `order_items` | REST order + RabbitMQ event consumer |
| `kds-service` | `8080` | MongoDB, collection `kitchen_tickets` | REST KDS + WebSocket + RabbitMQ event consumer |
| `api-gateway` | `8080` | none | Route/auth layer, không có business controller |
| `service-discovery` | `${PORT:8080}` | none | Eureka server, không có business controller |

Lưu ý:

- `api-gateway`, `kds-service`, và `service-discovery` đều có thể dùng `8080` nếu không override env, nên local setup rất dễ xung đột port.
- Hầu hết service client lại đang mặc định trỏ Eureka sang `http://localhost:8761/eureka/`, vì vậy `service-discovery` cần được chạy với `PORT=8761` hoặc toàn bộ client phải override `EUREKA_DEFAULT_ZONE`.
- `service-discovery` không khai báo controller tùy biến trong source; nếu có endpoint thì là endpoint mặc định của Eureka/Spring.

## Gateway Exposure

Trong source hiện tại, `api-gateway` route các path sau qua Eureka/service name:

| Path qua gateway | Route đích |
| --- | --- |
| `/api/auth/**` | `lb://auth-service` |
| `/api/menu/**` | `lb://menu-service` |
| `/api/orders/**` | `lb://ordering-service` |
| `/api/kds/**` | `lb://kds-service` |
| `/ws/kds/**` | `lb:ws://kds-service` |

Chưa thấy route cho:

- `/api/categories/**`
- `/api/promotions/**`

Vì vậy, không phải toàn bộ business API đều đã được expose qua một ingress thống nhất.

## Auth Service

Base path: `/api/auth`

### Endpoint catalog

| Method | Path | Auth | Request body | Success response |
| --- | --- | --- | --- | --- |
| `POST` | `/api/auth/login` | Public | `AuthRequest` | `200 OK`, body `AuthResponse` |
| `POST` | `/api/auth/register` | JWT role `MANAGER` | `RegisterRequest` | `200 OK`, plain text message |
| `GET` | `/api/auth/.well-known/jwks.json` | Public | none | `200 OK`, JWKS JSON |

### Request/response shapes

`AuthRequest`

```json
{
  "username": "alice",
  "password": "secret"
}
```

`AuthResponse`

```json
{
  "token": "<jwt>"
}
```

`RegisterRequest`

```json
{
  "username": "bob",
  "password": "secret",
  "role": "SERVER"
}
```

`GET /api/auth/.well-known/jwks.json`

```json
{
  "keys": [
    {
      "kty": "RSA",
      "kid": "irms-auth-key-1",
      "n": "...",
      "e": "AQAB"
    }
  ]
}
```

### Contract notes

- JWT do service này phát hành có claim `role`, subject `sub=username`, thuật toán `RS256`, thời hạn 1 giờ.
- `POST /api/auth/register` trả về chuỗi plain text: `Account created successfully. Employee can now log in.`

## Menu Service

Base paths:

- `/api/categories`
- `/api/menu`
- `/api/promotions`

Auth rules:

- `GET /api/menu/**`, `GET /api/promotions/**`, `GET /api/categories/**`: role `MANAGER`, `SERVER`, hoặc `CHEF`
- Các method không phải `GET` trên 3 base path trên: chỉ role `MANAGER`

### Endpoint catalog

| Method | Path | Request body | Success response |
| --- | --- | --- | --- |
| `GET` | `/api/categories` | none | `200 OK`, `CategoryResponseDTO[]` |
| `GET` | `/api/categories/{categoryId}` | none | `200 OK`, `CategoryResponseDTO` |
| `POST` | `/api/categories` | `CategoryRequestDTO` | `201 Created`, `CategoryResponseDTO` |
| `PUT` | `/api/categories/{categoryId}` | `CategoryRequestDTO` | `200 OK`, `CategoryResponseDTO` |
| `DELETE` | `/api/categories/{categoryId}` | none | `204 No Content` |
| `GET` | `/api/menu` | none | `200 OK`, `MenuItemResponseDTO[]` |
| `GET` | `/api/menu/available` | none | `200 OK`, `MenuItemResponseDTO[]` |
| `GET` | `/api/menu/{itemId}` | none | `200 OK`, `MenuItemResponseDTO` |
| `GET` | `/api/menu/{itemId}/availability` | none | `200 OK`, `MenuItemAvailabilityResponseDTO` |
| `POST` | `/api/menu` | `MenuItemRequestDTO` | `201 Created`, `MenuItemResponseDTO` |
| `PUT` | `/api/menu/{itemId}` | `MenuItemRequestDTO` | `200 OK`, `MenuItemResponseDTO` |
| `DELETE` | `/api/menu/{itemId}` | none | `204 No Content` |
| `POST` | `/api/menu/{itemId}/promotions/{promotionId}` | none | `204 No Content` |
| `DELETE` | `/api/menu/{itemId}/promotions/{promotionId}` | none | `204 No Content` |
| `GET` | `/api/promotions` | none | `200 OK`, `PromotionResponseDTO[]` |
| `GET` | `/api/promotions/active` | none | `200 OK`, `PromotionResponseDTO[]` |
| `GET` | `/api/promotions/{promotionId}` | none | `200 OK`, `PromotionResponseDTO` |
| `POST` | `/api/promotions` | `PromotionRequestDTO` | `201 Created`, `PromotionResponseDTO` |
| `PUT` | `/api/promotions/{promotionId}` | `PromotionRequestDTO` | `200 OK`, `PromotionResponseDTO` |
| `DELETE` | `/api/promotions/{promotionId}` | none | `204 No Content` |

### Request/response shapes

`CategoryRequestDTO`

```json
{
  "name": "Main dishes",
  "description": "Hot kitchen",
  "isActive": true
}
```

`CategoryResponseDTO`

```json
{
  "id": "8e03f27d-7c93-4b6c-bff6-499b72a74e9f",
  "name": "Main dishes",
  "description": "Hot kitchen",
  "active": true
}
```

`MenuItemRequestDTO`

```json
{
  "categoryId": "8e03f27d-7c93-4b6c-bff6-499b72a74e9f",
  "name": "Burger",
  "description": "Beef burger",
  "basePrice": 89000,
  "isAvailable": true,
  "imageUrl": "https://example.com/burger.jpg",
  "customizations": [
    {
      "name": "Extra cheese",
      "price": 10000
    }
  ]
}
```

`MenuItemResponseDTO`

```json
{
  "id": "2eaed93b-cd7b-40b7-a3ef-cd6b2fdb2781",
  "categoryId": "8e03f27d-7c93-4b6c-bff6-499b72a74e9f",
  "name": "Burger",
  "description": "Beef burger",
  "originalPrice": 89000,
  "finalCalculatedPrice": 79000,
  "available": true,
  "imageUrl": "https://example.com/burger.jpg",
  "customizations": [
    {
      "name": "Extra cheese",
      "price": 10000
    }
  ],
  "activePromotions": [
    "Lunch combo"
  ]
}
```

`MenuItemAvailabilityResponseDTO`

```json
{
  "itemId": "2eaed93b-cd7b-40b7-a3ef-cd6b2fdb2781",
  "availableForOrder": true
}
```

`PromotionRequestDTO`

```json
{
  "name": "Lunch combo",
  "type": "FIXED_AMOUNT",
  "discountValue": 10000,
  "startTime": "2026-04-29T10:00:00",
  "endTime": "2026-04-29T14:00:00"
}
```

`PromotionResponseDTO`

```json
{
  "id": "14f1ad34-f703-44ba-b95f-83cb95f983bd",
  "name": "Lunch combo",
  "type": "FIXED_AMOUNT",
  "discountValue": 10000,
  "startTime": "2026-04-29T10:00:00",
  "endTime": "2026-04-29T14:00:00",
  "active": true
}
```

### Contract notes

- Boolean field request/response không đồng nhất do naming của getter:
  - Request category dùng `isActive`, response category dùng `active`.
  - Request menu item dùng `isAvailable`, response menu item dùng `available`.
- `GET /api/menu` và `GET /api/menu/available` đều trả `finalCalculatedPrice`, được tính từ `basePrice` sau khi áp dụng toàn bộ promotion đang active.

## Ordering Service

Base path: `/api/orders`

Auth rules:

- `POST /api/orders`: role `MANAGER` hoặc `SERVER`
- `GET /api/orders`: role `MANAGER` hoặc `SERVER`
- `GET /api/orders/{orderId}`: role `MANAGER` hoặc `SERVER`

### Endpoint catalog

| Method | Path | Request body | Success response |
| --- | --- | --- | --- |
| `POST` | `/api/orders` | `CreateOrderRequest` | `201 Created`, `OrderResponse` |
| `GET` | `/api/orders/{orderId}` | none | `200 OK`, `OrderResponse` |
| `GET` | `/api/orders` | none | `200 OK`, `OrderResponse[]` |

### Request/response shapes

`CreateOrderRequest`

```json
{
  "tableNumber": "5",
  "staffName": "John Doe",
  "items": [
    {
      "menuItemId": "2eaed93b-cd7b-40b7-a3ef-cd6b2fdb2781",
      "name": "Burger",
      "quantity": 2,
      "customizations": [
        "No onions",
        "Extra cheese"
      ]
    }
  ]
}
```

`OrderResponse`

```json
{
  "id": 1,
  "tableNumber": "5",
  "staffName": "John Doe",
  "status": "CREATED",
  "timestamp": "2026-04-29T10:00:00",
  "items": [
    {
      "id": 1,
      "menuItemId": "2eaed93b-cd7b-40b7-a3ef-cd6b2fdb2781",
      "name": "Burger",
      "quantity": 2,
      "customizations": [
        "No onions",
        "Extra cheese"
      ]
    }
  ]
}
```

### Contract notes

- `OrderStatus` hợp lệ trong source: `CREATED`, `COOKING`, `READY`, `SERVED`.
- `CreateOrderRequest` không có field `notes`, dù event và entity nội bộ vẫn có khái niệm `notes`.
- Khi tạo order, service forward JWT của caller sang `menu-service` để kiểm tra availability.
- `tableNumber` là string ở REST API, nhưng khi phát event sang KDS service sẽ parse sang integer; nếu parse lỗi thì source hiện tại silently fallback về `0`.

## KDS Service

Base path: `/api/kds/tickets`

Auth rules:

- `/api/kds/**`: role `MANAGER` hoặc `CHEF`
- `/ws/**`: public
- `SecurityConfig` cũng permit `/actuator/health`, `/actuator/health/**`, `/actuator/info`

### Endpoint catalog

| Method | Path | Request body | Success response |
| --- | --- | --- | --- |
| `GET` | `/api/kds/tickets/active` | none | `200 OK`, `KitchenTicket[]` |
| `PUT` | `/api/kds/tickets/{ticketId}/status` | `UpdateOrderStatusRequest` | `204 No Content` |
| `GET` | `/ws/kds` | STOMP handshake | WebSocket endpoint |

### Request/response shapes

`UpdateOrderStatusRequest`

```json
{
  "status": "READY"
}
```

`KitchenTicket`

```json
{
  "id": "1",
  "tableNumber": 5,
  "waiterId": "John Doe",
  "status": "PENDING",
  "receivedAt": "2026-04-29T10:00:00",
  "completedAt": null,
  "items": [
    {
      "menuItemId": "2eaed93b-cd7b-40b7-a3ef-cd6b2fdb2781",
      "itemName": "Burger",
      "quantity": 2,
      "status": "PENDING",
      "customizations": [
        "No onions"
      ],
      "notes": []
    }
  ]
}
```

### WebSocket topics

- Endpoint handshake: `/ws/kds`
- Broker prefix: `/topic`
- Topics đang được publish trong source:
  - `/topic/kitchen/new-tickets`: payload `KitchenTicket`
  - `/topic/kitchen/ticket-updates`: payload `KitchenTicket`
  - `/topic/kitchen/completed-tickets`: payload `ticketId` dạng string

### Contract notes

- `GET /api/kds/tickets/active` chỉ trả ticket có status khác `READY` và `SERVED`, tức trên thực tế chỉ còn `PENDING` hoặc `COOKING`.
- `PUT /api/kds/tickets/{ticketId}/status` chỉ chấp nhận `COOKING`, `READY`, `SERVED`. `PENDING` không được phép set lại qua API này.
- `TicketWriteService` hiện đã có `updateItemStatus(ticketId, itemIndex, status)` để cập nhật trạng thái từng item và tự recalculate `TicketStatus`, nhưng luồng này chưa được expose qua REST API public.

## Async Event Contracts

### RabbitMQ: `order.created`

Producer: `ordering-service`  
Consumer: `kds-service`  
Exchange/routing key: `restaurant.events` / `order.created`

```json
{
  "eventId": "uuid",
  "orderId": "1",
  "tableNumber": 5,
  "waiterId": "John Doe",
  "timestamp": "2026-04-29T10:00:00Z",
  "items": [
    {
      "menuItemId": "2eaed93b-cd7b-40b7-a3ef-cd6b2fdb2781",
      "itemName": "Burger",
      "quantity": 2,
      "customizations": [
        "No onions"
      ],
      "notes": []
    }
  ]
}
```

### RabbitMQ: `order.status.updated`

Producer: `kds-service`  
Consumer: `ordering-service`  
Exchange/routing key: `restaurant.events` / `order.status.updated`

```json
{
  "eventId": "uuid",
  "orderId": "1",
  "newStatus": "READY",
  "timestamp": "2026-04-29T10:15:00Z",
  "updatedBy": "KDS-Station-HotLine"
}
```

## Những điểm chưa rõ hoặc thiếu trong contract

1. Chưa có OpenAPI contract commit sẵn ở repo. Toàn bộ tài liệu phải suy ra từ code hiện tại.
2. `menu-service` và `kds-service` không có custom error envelope thống nhất cho mọi lỗi business; nhiều lỗi đang dựa vào cách Spring Boot render `ResponseStatusException` hoặc lỗi mặc định.
3. Request DTO của `ordering-service` hiện không có Bean Validation và cũng không có validate tay cho `tableNumber`, `staffName`, `items`, `quantity`; nhiều case thiếu field có thể rơi vào lỗi runtime/DB thay vì `400` rõ ràng.
4. `TicketWriteUseCase.updateItemStatus(...)` đã được implement và có recalculate ticket status, nhưng hiện vẫn là contract nội bộ; source chưa có REST API public nào gọi use case này.
5. Event `OrderCreatedEvent` và model `TicketItem` có field `notes`, nhưng API `POST /api/orders` không nhận `notes`, và luồng map trong `kds-service` cũng không copy `notes` từ event sang ticket. End-to-end contract của `notes` vì vậy chưa hoàn chỉnh.
6. `ordering-service` hiện gọi `menu-service` qua `http://menu-service` trong `RestClientConfig`; base URL này chưa được externalize thành config ứng dụng.
7. `api-gateway` đã route `auth`, `menu`, `orders`, `kds`, và WebSocket KDS, nhưng vẫn chưa route riêng `categories` và `promotions`.
8. Boolean JSON naming ở `menu-service` không đối xứng giữa request và response: request dùng `isActive`/`isAvailable`, response dùng `active`/`available`.
9. `menu-service` cho phép stack nhiều promotion theo thứ tự lặp hiện tại và không giới hạn `PERCENTAGE > 100`, nhưng contract không mô tả rõ rule pricing này.
10. `GET` list endpoints hiện không có pagination, sorting, filtering trong contract.
11. `auth-service` gắn `SecurityRequirement(name = "bearerAuth")` ở mức OpenAPI global, nên login/JWKS có thể bị hiển thị như endpoint cần auth dù runtime thật sự cho public.
12. Port/runtime convention hiện chưa đồng nhất: `api-gateway`, `kds-service`, và `service-discovery` đều có thể chiếm `8080`, trong khi các Eureka client lại mặc định kỳ vọng server ở `8761`.
13. `menu-service/Dockerfile` đang set `PORT=8761`, khác với `server.port=8082` trong source, nên tài liệu deploy theo container cần đọc kèm caveat này.
