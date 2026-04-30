# Intelligent Restaurant Management System

IRMS la mot he thong quan ly nha hang theo mo hinh microservice, gom cac service cho xac thuc, menu, ordering, KDS, gateway va service discovery. Repo hien tai da co them bo `docs/` de mo ta contract va flow theo source code, dong thoi da bat dau bo sung Dockerfile cho mot so service backend.

## Service Map

| Service | Vai tro | Port mac dinh trong source |
| --- | --- | --- |
| `auth-service` | Dang nhap, dang ky nhan vien, phat JWT, JWKS | `${PORT:8081}` |
| `menu-service` | Quan ly category, menu item, promotion | `8082` |
| `ordering-service` | Tao order, luu PostgreSQL, publish/consume RabbitMQ | `8085` |
| `kds-service` | Quan ly kitchen ticket, WebSocket, consume/publish RabbitMQ | `8080` |
| `api-gateway` | Ingress + JWT verification cho route qua gateway | `8080` |
| `service-discovery` | Eureka server | `${PORT:8080}` |
| `frontend-ui` | Giao dien Vite + React | `5173` trong dev mode |

## Current Notes

- `api-gateway`, `kds-service`, va `service-discovery` deu co the dung `8080` neu khong override env.
- Cac service client (`auth-service`, `menu-service`, `ordering-service`, `kds-service`, `api-gateway`) deu mac dinh tro den Eureka tai `http://localhost:8761/eureka/`.
- Vi `service-discovery` hien de `server.port=${PORT:8080}`, khi chay local voi default cua cac client thi can set `PORT=8761` cho service nay, hoac doi `EUREKA_DEFAULT_ZONE` o cac service con lai.
- Da co Dockerfile cho `api-gateway`, `auth-service`, `menu-service`, `ordering-service`, `service-discovery`.
- Chua thay Dockerfile cho `kds-service` va `frontend-ui`.
- `menu-service/Dockerfile` hien dang set `PORT=8761`, trong khi app default la `8082`; neu dung image nay can canh lai env/port mapping truoc khi deploy.

## Gateway Coverage

Gateway hien route cac path sau:

- `/api/auth/**`
- `/api/menu/**`
- `/api/orders/**`
- `/api/kds/**`
- `/ws/kds/**`

Chua co route rieng cho:

- `/api/categories/**`
- `/api/promotions/**`

## Documentation Index

- [API summary](docs/api-summary.md)
- [Service flow](docs/service-flow.md)
- [Sequence flow](docs/sequence-flow.md)
- [Ordering service guide](ordering-service/README.md)

## Development Snapshot

- Backend chay bang Java 21 va Spring Boot/Spring Cloud.
- `ordering-service` va `kds-service` giao tiep bat dong bo qua RabbitMQ exchange `restaurant.events`.
- Cac service verify JWT thong qua JWKS cua `auth-service` bang ten service trong Eureka, khong phai localhost hardcode trong security runtime.
- `docker-compose.yml` hien van rong, nen local setup hien chu yeu dua vao chay tung service rieng le.
