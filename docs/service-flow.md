# Service Flow

## Scope

- Tài liệu này mô tả flow thực thi theo source hiện tại: `Controller -> Service/UseCase -> Repository/DB`, cộng thêm outbound HTTP, RabbitMQ, WebSocket và error handling.
- Phần event consumer cũng được ghi lại vì `ordering-service` và `kds-service` không chỉ có REST flow.

## Controller -> Service -> Repository/DB

## Auth Service

| API | Flow nội bộ | DB/Storage | External call |
| --- | --- | --- | --- |
| `POST /api/auth/login` | `AuthController.login -> AuthService.login -> UserRepository.findByUsername -> PasswordEncoder.matches -> JwtService.generateToken` | PostgreSQL `users` | none |
| `POST /api/auth/register` | `AuthController.register -> AuthService.register -> UserRepository.findByUsername -> PasswordEncoder.encode -> UserRepository.save` | PostgreSQL `users` | none |
| `GET /api/auth/.well-known/jwks.json` | `JwksController.getJwks -> KeyPair bean từ JKS keystore -> JWKSet.toJSONObject` | file keystore `irms-keystore.jks` | none |

Chi tiết:

- `login` đọc user từ DB, so password bằng BCrypt, rồi phát JWT có `role` claim.
- `register` chỉ tạo user mới nếu username chưa tồn tại.
- JWKS không đụng DB; key public được load từ keystore qua `RsaKeyConfig`.

## Menu Service

| API | Flow nội bộ | DB/Storage | External call |
| --- | --- | --- | --- |
| `GET /api/categories` | `CategoryController.getAllCategories -> CategoryReadService.getAllCategories -> CategoryRepository.findAll` | PostgreSQL `categories` | none |
| `GET /api/categories/{categoryId}` | `CategoryController.getCategory -> CategoryReadService.getCategoryById -> CategoryRepository.findById` | PostgreSQL `categories` | none |
| `POST /api/categories` | `CategoryController.createCategory -> CategoryWriteService.createCategory -> CategoryRepository.existsByName -> CategoryRepository.save` | PostgreSQL `categories` | none |
| `PUT /api/categories/{categoryId}` | `CategoryController.updateCategory -> CategoryWriteService.updateCategory -> CategoryRepository.findById -> CategoryRepository.existsByNameAndIdNot -> CategoryRepository.save` | PostgreSQL `categories` | none |
| `DELETE /api/categories/{categoryId}` | `CategoryController.deleteCategory -> CategoryWriteService.deleteCategory -> CategoryRepository.findById -> MenuItemRepository.existsByCategory_Id -> CategoryRepository.delete` | PostgreSQL `categories`, `menu_items` | none |
| `GET /api/menu` | `MenuController.getAllMenuItems -> MenuReadService.getAllMenuItems -> MenuItemRepository.findAllWithPromotions -> PriceCalculationService.calculateFinalPrice` | PostgreSQL `menu_items`, `promotions`, join `menu_item_promotions` | none |
| `GET /api/menu/available` | `MenuController.getMenu -> MenuReadService.getAvailableMenu -> MenuItemRepository.findAvailableWithPromotions -> PriceCalculationService.calculateFinalPrice` | PostgreSQL `menu_items`, `promotions`, join `menu_item_promotions` | none |
| `GET /api/menu/{itemId}` | `MenuController.getMenuItem -> MenuReadService.getMenuItemById -> MenuItemRepository.findByIdWithPromotions -> PriceCalculationService.calculateFinalPrice` | PostgreSQL `menu_items`, `promotions`, join `menu_item_promotions` | none |
| `GET /api/menu/{itemId}/availability` | `MenuController.getItemAvailability -> MenuReadService.getItemAvailability -> MenuItemRepository.findById` | PostgreSQL `menu_items` | none |
| `POST /api/menu` | `MenuController.createItem -> MenuWriteService.createItem -> validateRequest -> CategoryRepository.existsById -> CategoryRepository.getReferenceById -> MenuItemRepository.save -> PriceCalculationService.calculateFinalPrice` | PostgreSQL `menu_items`, `categories` | none |
| `PUT /api/menu/{itemId}` | `MenuController.updateItem -> MenuWriteService.updateItem -> validateRequest -> MenuItemRepository.findByIdWithPromotions -> CategoryRepository.existsById -> CategoryRepository.getReferenceById -> MenuItemRepository.save -> PriceCalculationService.calculateFinalPrice` | PostgreSQL `menu_items`, `categories`, `menu_item_promotions` | none |
| `DELETE /api/menu/{itemId}` | `MenuController.deleteItem -> MenuWriteService.deleteItem -> MenuItemRepository.findByIdWithPromotions -> clear promotions -> MenuItemRepository.delete` | PostgreSQL `menu_items`, join `menu_item_promotions` | none |
| `POST /api/menu/{itemId}/promotions/{promotionId}` | `MenuController.applyPromo -> MenuWriteService.applyPromotionToItem -> MenuItemRepository.findByIdWithPromotions -> PromotionRepository.findById -> MenuItemRepository.save` | PostgreSQL `menu_items`, `promotions`, join `menu_item_promotions` | none |
| `DELETE /api/menu/{itemId}/promotions/{promotionId}` | `MenuController.removePromo -> MenuWriteService.removePromotionFromItem -> MenuItemRepository.findByIdWithPromotions -> removeIf -> MenuItemRepository.save` | PostgreSQL `menu_items`, join `menu_item_promotions` | none |
| `GET /api/promotions` | `PromotionController.getAllPromotions -> PromotionReadService.getAllPromotions -> PromotionRepository.findAll` | PostgreSQL `promotions` | none |
| `GET /api/promotions/active` | `PromotionController.getActivePromotions -> PromotionReadService.getActivePromotions -> PromotionRepository.findActivePromotions` | PostgreSQL `promotions` | none |
| `GET /api/promotions/{promotionId}` | `PromotionController.getPromotion -> PromotionReadService.getPromotionById -> PromotionRepository.findById` | PostgreSQL `promotions` | none |
| `POST /api/promotions` | `PromotionController.createPromotion -> PromotionWriteService.createPromotion -> validateRequest -> PromotionRepository.findByName -> PromotionRepository.save` | PostgreSQL `promotions` | none |
| `PUT /api/promotions/{promotionId}` | `PromotionController.updatePromotion -> PromotionWriteService.updatePromotion -> validateRequest -> PromotionRepository.findById -> PromotionRepository.existsByNameAndIdNot -> PromotionRepository.save` | PostgreSQL `promotions` | none |
| `DELETE /api/promotions/{promotionId}` | `PromotionController.deletePromotion -> PromotionWriteService.deletePromotion -> PromotionRepository.findById -> MenuItemRepository.findByPromotions_Id -> MenuItemRepository.saveAll -> PromotionRepository.delete` | PostgreSQL `promotions`, `menu_items`, join `menu_item_promotions` | none |

Chi tiết thêm:

- Giá final của menu item không được lưu riêng trong DB; nó được tính runtime qua `PriceCalculationService`.
- `PriceCalculationService` lặp qua toàn bộ promotion đang active và áp dụng strategy theo `PromotionType`.

## Ordering Service

| API/Event | Flow nội bộ | DB/Storage | External call |
| --- | --- | --- | --- |
| `POST /api/orders` | `OrderController.createOrder -> extractToken -> CreateOrderService.createOrder -> MenuAvailabilityPort.findUnavailableItemIds -> Order.create -> OrderRepositoryPort.save -> buildKdsEvent -> OrderEventPublisherPort.publishOrderCreated` | PostgreSQL `orders`, `order_items` | HTTP sang `menu-service`; RabbitMQ publish `order.created` |
| `GET /api/orders/{orderId}` | `OrderController.getOrder -> GetOrderService.getOrder -> OrderRepositoryPort.findById -> map OrderResponse` | PostgreSQL `orders`, `order_items` | none |
| `GET /api/orders` | `OrderController.getAllOrders -> GetOrderService.getAllOrders -> OrderRepositoryPort.findAll -> map OrderResponse[]` | PostgreSQL `orders`, `order_items` | none |
| RabbitMQ consume `order.status.updated` | `KdsOrderStatusListener.onKdsStatusEvent -> UpdateOrderStatusUseCase.updateStatus -> OrderRepositoryPort.findById -> Order.applyStatusTransition -> OrderRepositoryPort.save` | PostgreSQL `orders`, `order_items` | RabbitMQ consume |

Chi tiết thêm cho `POST /api/orders`:

1. Controller lấy JWT thô từ `Principal` để forward sang `menu-service`.
2. `CreateOrderService` gọi `MenuServiceAdapter` cho từng `menuItemId` distinct.
3. Nếu có item unavailable, service dừng luôn và ném `MenuItemUnavailableException`.
4. Nếu hợp lệ, service map request thành domain `Order` và `OrderItem`.
5. Order được lưu xuống Postgres qua `OrderRepositoryAdapter -> OrderJpaRepository`.
6. Sau khi lưu, service phát `KdsOrderCreatedEvent` sang RabbitMQ để `kds-service` xử lý.
7. Outbound HTTP và JWKS lookup đều đi qua service name trong Eureka, không dựa vào localhost URL hardcode ở runtime.

## KDS Service

| API/Event | Flow nội bộ | DB/Storage | External call |
| --- | --- | --- | --- |
| `GET /api/kds/tickets/active` | `KdsController.getActiveTickets -> TicketReadService.getActiveTickets -> KitchenTicketRepository.findByStatusNotIn` | MongoDB `kitchen_tickets` | none |
| `PUT /api/kds/tickets/{ticketId}/status` | `KdsController.updateOrderStatus -> TicketWriteService.updateOrderStatus -> KitchenTicketRepository.findById -> KitchenTicketRepository.save -> OrderStatusPublisher.publishOrderStatusEvent -> KdsWebSocketPublisher.broadcast...` | MongoDB `kitchen_tickets` | RabbitMQ publish `order.status.updated`; WebSocket publish |
| RabbitMQ consume `order.created` | `OrderEventConsumer.consumeOrderCreatedEvent -> TicketWriteService.createTicketFromEvent -> map OrderCreatedEvent -> KitchenTicketRepository.save -> KdsWebSocketPublisher.broadcastNewTicket` | MongoDB `kitchen_tickets` | RabbitMQ consume; WebSocket publish |

Chi tiết thêm cho `PUT /api/kds/tickets/{ticketId}/status`:

1. Chỉ chấp nhận status `COOKING`, `READY`, `SERVED`.
2. Ticket được đọc từ Mongo theo `ticketId`, vốn đang dùng luôn `orderId` của `ordering-service`.
3. Sau khi save:
   - luôn publish event `order.status.updated` về RabbitMQ
   - nếu status là `COOKING`: broadcast full ticket lên `/topic/kitchen/ticket-updates`
   - nếu status là `READY` hoặc `SERVED`: broadcast `ticketId` lên `/topic/kitchen/completed-tickets`

Chi tiết thêm cho logic item-level trong `TicketWriteService`:

- `updateItemStatus(ticketId, itemIndex, status)` hiện đã được implement trong service layer.
- Luồng này validate `itemIndex`, cập nhật `TicketItem.status`, rồi tự recalculate `TicketStatus` của cả ticket.
- Chỉ khi ticket status thực sự đổi thì service mới publish `order.status.updated`.
- Nếu toàn bộ item đã `READY`, service broadcast removal qua `/topic/kitchen/completed-tickets`; ngược lại sẽ broadcast full ticket update.
- Source hiện chưa có REST endpoint public nào gọi thẳng flow item-level này.

## External Service And Infrastructure Calls

## JWT/JWKS lookup

| Service | Cách verify JWT | Outbound target |
| --- | --- | --- |
| `menu-service` | `NimbusJwtDecoder` + `LoadBalanced RestTemplate` | `http://auth-service/api/auth/.well-known/jwks.json` |
| `ordering-service` | `NimbusJwtDecoder` + `LoadBalanced RestTemplate` | `http://auth-service/api/auth/.well-known/jwks.json` |
| `kds-service` | `NimbusJwtDecoder` + `LoadBalanced RestTemplate` | `http://auth-service/api/auth/.well-known/jwks.json` |
| `api-gateway` | `NimbusReactiveJwtDecoder` + `LoadBalanced WebClient.Builder` | `http://auth-service/api/auth/.well-known/jwks.json` |

`auth-service` không có outbound HTTP call trong business flow.

## Synchronous HTTP calls

| Caller | Callee | Purpose | Contract used |
| --- | --- | --- | --- |
| `ordering-service` | `menu-service` | Kiểm tra availability trước khi tạo order | `GET /api/menu/{itemId}/availability`, forward JWT của caller |

Chi tiết:

- `MenuServiceAdapter` gọi từng item riêng lẻ, không có batch API.
- Nếu `menu-service` trả 404, HTTP lỗi khác, parse lỗi, hoặc network lỗi, `ordering-service` đều coi item đó là unavailable.

## RabbitMQ calls

| Producer | Consumer | Exchange/key or queue | Payload |
| --- | --- | --- | --- |
| `ordering-service` | `kds-service` | `restaurant.events` / `order.created` | `KdsOrderCreatedEvent` |
| `kds-service` | `ordering-service` | `restaurant.events` / `order.status.updated` | `TicketReadyEvent` / `KdsStatusEvent` |

## WebSocket calls

| Producer | Endpoint/topic | Payload |
| --- | --- | --- |
| `kds-service` | `/ws/kds` + `/topic/kitchen/new-tickets` | full `KitchenTicket` |
| `kds-service` | `/ws/kds` + `/topic/kitchen/ticket-updates` | full `KitchenTicket` |
| `kds-service` | `/ws/kds` + `/topic/kitchen/completed-tickets` | `ticketId` string |

## Gateway routing

| Gateway path | Routed to |
| --- | --- |
| `/api/auth/**` | `lb://auth-service` |
| `/api/menu/**` | `lb://menu-service` |
| `/api/orders/**` | `lb://ordering-service` |
| `/api/kds/**` | `lb://kds-service` |
| `/ws/kds/**` | `lb:ws://kds-service` |

Lưu ý:

- `api-gateway` hiện không route riêng `/api/categories/**` và `/api/promotions/**`.
- Do đó category/promotion API của `menu-service` hiện chưa đi qua cùng ingress path với phần `menu`.

## Error Handling

## Auth Service

| Source | HTTP | Body shape | Ghi chú |
| --- | --- | --- | --- |
| `InvalidCredentialsException` | `401` | `{"error":"Invalid credentials"}` | login sai username/password |
| `UsernameAlreadyTakenException` | `409` | `{"error":"Username is already taken"}` | register trùng username |
| `MethodArgumentNotValidException` | `400` | `{"error":"Validation failed","details":{...}}` | thiếu username/password/role trong DTO có `@Valid` |
| `HttpMessageNotReadableException` | `400` | `{"error":"Malformed request body"}` | JSON malformed / enum parse lỗi |
| `JwtAuthenticationFilter` | `401` | `{"error":"Invalid or expired token."}` hoặc `{"error":"Invalid token claims."}` | áp dụng cho request private đi vào auth-service |

## Menu Service

| Source | HTTP | Body shape | Ghi chú |
| --- | --- | --- | --- |
| `ResponseStatusException(HttpStatus.NOT_FOUND, ...)` | `404` | Spring default | category/menu item/promotion không tồn tại |
| `ResponseStatusException(HttpStatus.CONFLICT, ...)` | `409` | Spring default | trùng category/promotion name, category đang được dùng |
| `ResponseStatusException(HttpStatus.BAD_REQUEST, ...)` | `400` | Spring default | thiếu name/categoryId/basePrice/type/time... |
| Spring Security | `401/403` | Spring default | JWT thiếu/sai hoặc sai role |

Nhận xét:

- `menu-service` không có `@RestControllerAdvice` riêng để chuẩn hóa error body.
- Vì vậy schema lỗi chi tiết có thể phụ thuộc vào version Spring Boot và content negotiation hiện tại.

## Ordering Service

| Source | HTTP | Body shape | Ghi chú |
| --- | --- | --- | --- |
| `OrderNotFoundException` | `404` | `{"error":"Order not found: <id>"}` | GET theo ID hoặc event update trỏ tới order không tồn tại |
| `InvalidStatusTransitionException` | `409` | `{"error":"Invalid status transition: ..."} ` | event từ KDS yêu cầu chuyển trạng thái không hợp lệ |
| `MenuItemUnavailableException` | `422` | `{"error":"The following menu items are unavailable: ..."} ` | xảy ra khi check menu fail |
| `IllegalArgumentException` | `400` | `{"error":"..."}` | ví dụ `OrderStatus.fromString()` không parse được |
| Spring Security | `401/403` | Spring default | JWT thiếu/sai hoặc sai role |

Nhận xét:

- `CreateOrderRequest` và `OrderItemRequest` không có Bean Validation.
- Null/thiếu field trong request có thể đi vào layer sâu hơn và gây lỗi runtime/DB ngoài các error đã chuẩn hóa ở trên.
- Với Rabbit listener `KdsOrderStatusListener`, exception không được convert thành HTTP response; hành vi retry/ack phụ thuộc Spring AMQP mặc định và hiện không được mô tả thêm trong source.

## KDS Service

| Source | HTTP | Body shape | Ghi chú |
| --- | --- | --- | --- |
| `TicketNotFoundException` | `404` | `{"error":"Kitchen ticket not found: <ticketId>"}` | update ticket không tồn tại |
| `IllegalArgumentException` | `400` | `{"error":"..."}` | status null hoặc không thuộc `COOKING/READY/SERVED` |
| Spring Security | `401/403` | Spring default | JWT thiếu/sai hoặc sai role |

Nhận xét:

- `UpdateOrderStatusRequest` không có `@Valid`; nếu body rỗng hoàn toàn hoặc JSON sai format thì sẽ đi qua error mặc định của Spring.
- `OrderEventConsumer` không bắt exception khi parse timestamp hay map ticket; nếu có lỗi trong consume path thì đó là lỗi message processing, không phải lỗi HTTP.

## Hạ tầng

| Thành phần | Error handling quan sát được |
| --- | --- |
| `api-gateway` | `/api/auth/**` public; phần còn lại phải qua JWT resource server. Không có custom error body trong source. |
| `kds-service` | Security permit thêm `/actuator/health`, `/actuator/health/**`, `/actuator/info`; actuator dependency đã được thêm vào `pom.xml`. |
| `service-discovery` | Không có business error handler tùy biến. |
