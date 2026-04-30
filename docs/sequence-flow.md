# Sequence Flow

Các sơ đồ dưới đây mô tả flow hiện đang có trong source. Mình dùng Mermaid `sequenceDiagram` để thể hiện cả REST flow và async flow qua RabbitMQ/WebSocket.

## Auth login

```mermaid
sequenceDiagram
    actor User
    participant AuthController
    participant AuthService
    participant UserRepository
    participant PostgreSQL
    participant JwtService

    User->>AuthController: POST /api/auth/login
    AuthController->>AuthService: login(AuthRequest)
    AuthService->>UserRepository: findByUsername(username)
    UserRepository->>PostgreSQL: SELECT users by username
    PostgreSQL-->>UserRepository: user row
    UserRepository-->>AuthService: User
    AuthService->>AuthService: BCrypt matches(password)

    alt invalid credentials
        AuthService-->>AuthController: throw InvalidCredentialsException
        AuthController-->>User: 401 {"error":"Invalid credentials"}
    else valid credentials
        AuthService->>JwtService: generateToken(username, role)
        JwtService-->>AuthService: JWT
        AuthService-->>AuthController: token
        AuthController-->>User: 200 {"token":"<jwt>"}
    end
```

## Create menu item

```mermaid
sequenceDiagram
    actor Manager
    participant MenuController
    participant MenuWriteService
    participant CategoryRepository
    participant MenuItemRepository
    participant PostgreSQL
    participant PriceCalculationService

    Manager->>MenuController: POST /api/menu
    MenuController->>MenuWriteService: createItem(MenuItemRequestDTO)
    MenuWriteService->>CategoryRepository: existsById(categoryId)
    CategoryRepository->>PostgreSQL: SELECT category exists
    PostgreSQL-->>CategoryRepository: true/false

    alt category missing or request invalid
        MenuWriteService-->>MenuController: throw ResponseStatusException
        MenuController-->>Manager: 400/404
    else valid request
        MenuWriteService->>CategoryRepository: getReferenceById(categoryId)
        MenuWriteService->>MenuItemRepository: save(MenuItem)
        MenuItemRepository->>PostgreSQL: INSERT menu_items
        PostgreSQL-->>MenuItemRepository: saved row
        MenuItemRepository-->>MenuWriteService: saved MenuItem
        MenuWriteService->>PriceCalculationService: calculateFinalPrice(saved)
        PriceCalculationService-->>MenuWriteService: finalCalculatedPrice
        MenuWriteService-->>MenuController: MenuItemResponseDTO
        MenuController-->>Manager: 201 Created
    end
```

## Create order and fan out to KDS

```mermaid
sequenceDiagram
    actor Staff
    participant OrderController
    participant CreateOrderService
    participant MenuAdapter as MenuServiceAdapter
    participant MenuService as menu-service
    participant OrderRepo as OrderRepositoryAdapter/JPA
    participant PostgreSQL
    participant Publisher as RabbitMQOrderEventPublisher
    participant RabbitMQ
    participant KdsConsumer as OrderEventConsumer
    participant TicketWriteService
    participant MongoDB
    participant WebSocket as STOMP publisher

    Staff->>OrderController: POST /api/orders + JWT
    OrderController->>CreateOrderService: createOrder(request, bearerToken)

    loop each distinct menuItemId
        CreateOrderService->>MenuAdapter: findUnavailableItemIds(...)
        MenuAdapter->>MenuService: GET /api/menu/{itemId}/availability
        MenuService-->>MenuAdapter: { itemId, availableForOrder }
    end

    alt any item unavailable
        MenuAdapter-->>CreateOrderService: unavailableIds
        CreateOrderService-->>OrderController: throw MenuItemUnavailableException
        OrderController-->>Staff: 422 {"error":"..."}
    else all items available
        MenuAdapter-->>CreateOrderService: []
        CreateOrderService->>OrderRepo: save(Order)
        OrderRepo->>PostgreSQL: INSERT orders + order_items
        PostgreSQL-->>OrderRepo: persisted order
        OrderRepo-->>CreateOrderService: saved Order
        CreateOrderService->>Publisher: publishOrderCreated(event)
        Publisher->>RabbitMQ: routing key order.created
        RabbitMQ-->>KdsConsumer: OrderCreatedEvent
        KdsConsumer->>TicketWriteService: createTicketFromEvent(event)
        TicketWriteService->>MongoDB: save KitchenTicket
        TicketWriteService->>WebSocket: /topic/kitchen/new-tickets
        CreateOrderService-->>OrderController: OrderResponse
        OrderController-->>Staff: 201 Created
    end
```

## KDS status update and sync back to ordering

```mermaid
sequenceDiagram
    actor Chef
    participant KdsController
    participant TicketWriteService
    participant TicketRepo as KitchenTicketRepository
    participant MongoDB
    participant StatusPublisher as RabbitOrderStatusPublisher
    participant RabbitMQ
    participant OrderingListener as KdsOrderStatusListener
    participant UpdateOrderStatusService
    participant OrderRepo as OrderRepositoryAdapter/JPA
    participant PostgreSQL
    participant WebSocket as STOMP publisher

    Chef->>KdsController: PUT /api/kds/tickets/{ticketId}/status
    KdsController->>TicketWriteService: updateOrderStatus(ticketId, status)
    TicketWriteService->>TicketRepo: findById(ticketId)
    TicketRepo->>MongoDB: SELECT ticket by _id
    MongoDB-->>TicketRepo: KitchenTicket

    alt ticket missing or status invalid
        TicketWriteService-->>KdsController: throw TicketNotFoundException / IllegalArgumentException
        KdsController-->>Chef: 404 / 400
    else valid update
        TicketWriteService->>TicketRepo: save(updated ticket)
        TicketRepo->>MongoDB: UPDATE kitchen_tickets
        MongoDB-->>TicketRepo: saved ticket
        TicketWriteService->>StatusPublisher: publishOrderStatusEvent(orderId, status)
        StatusPublisher->>RabbitMQ: routing key order.status.updated
        RabbitMQ-->>OrderingListener: KdsStatusEvent
        OrderingListener->>UpdateOrderStatusService: updateStatus(orderId, newStatus)
        UpdateOrderStatusService->>OrderRepo: findById(orderId)
        OrderRepo->>PostgreSQL: SELECT order by id
        PostgreSQL-->>OrderRepo: Order
        UpdateOrderStatusService->>UpdateOrderStatusService: applyStatusTransition(newStatus)
        UpdateOrderStatusService->>OrderRepo: save(order)
        OrderRepo->>PostgreSQL: UPDATE orders

        alt status == COOKING
            TicketWriteService->>WebSocket: /topic/kitchen/ticket-updates
        else status == READY or SERVED
            TicketWriteService->>WebSocket: /topic/kitchen/completed-tickets
        end

        KdsController-->>Chef: 204 No Content
    end
```

## Read active tickets

```mermaid
sequenceDiagram
    actor Client
    participant KdsController
    participant TicketReadService
    participant TicketRepo as KitchenTicketRepository
    participant MongoDB

    Client->>KdsController: GET /api/kds/tickets/active
    KdsController->>TicketReadService: getActiveTickets()
    TicketReadService->>TicketRepo: findByStatusNotIn([READY, SERVED])
    TicketRepo->>MongoDB: query kitchen_tickets
    MongoDB-->>TicketRepo: active tickets
    TicketRepo-->>TicketReadService: KitchenTicket[]
    TicketReadService-->>KdsController: KitchenTicket[]
    KdsController-->>Client: 200 OK
```
