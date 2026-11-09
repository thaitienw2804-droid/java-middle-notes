# Tuần 8 (28/12/2026 – 03/01/2027): Microservices + API Gateway + Resilience

> Tuần có Tết Dương lịch (Thứ 6 01/01) — ngày 5 nghỉ hoặc chỉ ôn 1h. Tách monolith shop-order-platform thành services, mọi request đi qua Gateway.

---

## Ngày 50 — Thứ 2 (28/12): Monolith vs Microservices + DDD thực dụng
**Kiến thức phải nắm:**
- Monolith, **modular monolith**, microservices — ưu/nhược; **khi nào KHÔNG nên tách** (team nhỏ, domain chưa rõ, chưa có CI/CD + monitoring).
- Nguyên tắc: mỗi service sở hữu DB riêng (database-per-service), giao tiếp qua API/event, deploy độc lập.
- Vấn đề mới sinh ra: network không tin cậy, distributed transaction, consistency, debug khó, versioning API.
- **DDD**: Ubiquitous Language, **Bounded Context**, Entity vs Value Object, **Aggregate** + Aggregate Root (Order là root của OrderItem), Domain Event; context map.
- **CAP theorem** & PACELC; strong vs eventual consistency.
- Strangler Fig pattern khi migrate (liên hệ dự án migration ở FPT của bạn!).

**Code tay:** vẽ context map shop: Auth, Catalog, Inventory, Order, Payment, Notification; chọn tách trước Auth, Catalog, Order. Viết ADR-001 "Vì sao tách thế này".

**Tự kiểm tra:** Khi nào không nên dùng microservices? Bounded context vs aggregate? CAP là gì, Postgres/Kafka/Cassandra nằm đâu?

---

## Ngày 51 — Thứ 3 (29/12): Giao tiếp giữa services + Service Discovery + Config
**Kiến thức phải nắm:**
- Đồng bộ (REST/gRPC) vs bất đồng bộ (message) — trade-off coupling, latency, consistency.
- HTTP client: `RestClient` (Boot 3.2+), `WebClient`, **OpenFeign** (declarative); luôn có timeout (connect/read).
- **Service Discovery**: client-side (Eureka + Spring Cloud LoadBalancer) vs server-side (K8s Service/DNS) — trên K8s thường không cần Eureka.
- **Config Server** tập trung vs ConfigMap/Secret K8s; refresh config.
- Truyền context giữa service: JWT/user id, **traceId** (header `traceparent` — W3C).

**Code tay:** tách project Maven multi-module hoặc nhiều thư mục: `auth-service`, `catalog-service`, `order-service`; Order gọi Catalog qua OpenFeign để lấy giá.

**Tự kiểm tra:** Đồng bộ vs bất đồng bộ khi nào? Client-side vs server-side discovery? Vì sao bắt buộc timeout?

**LeetCode (20'):** Meeting Rooms II, Task Scheduler.

---

## Ngày 52 — Thứ 4 (30/12): API Gateway (trọng tâm)
**Kiến thức phải nắm:**
- Vì sao cần Gateway: 1 entry point, ẩn topology, auth tập trung, rate limit, CORS, logging/tracing, request transformation, API composition/BFF.
- **Spring Cloud Gateway**: Route = ID + URI + **Predicate** (Path, Method, Header) + **Filter** (StripPrefix, AddRequestHeader, RequestRateLimiter dùng Redis, CircuitBreaker, Retry); chạy trên WebFlux/Netty (non-blocking) — hoặc bản Server MVC mới.
- Auth ở Gateway (verify JWT, chuyển `X-User-Id`) vs ở từng service (zero trust) → thường **cả hai**: Gateway verify, service vẫn kiểm tra quyền nghiệp vụ.
- Gateway vs Load Balancer vs Nginx vs Kong/APISIX vs Service Mesh (Istio — biết tên).
- Gateway không thành **single point of failure**: chạy nhiều instance sau LB, stateless, health check.
- Lỗi **502** (upstream trả lỗi/không kết nối được) vs **504** (upstream timeout) — debug từ log Gateway.

**Code tay:** `api-gateway` với route `/api/auth/**`, `/api/products/**`, `/api/orders/**`; global filter verify JWT + gắn traceId; rate limit Redis theo user; CORS cấu hình tại Gateway.

**Tự kiểm tra:** Gateway làm gì? Auth đặt ở Gateway hay service? Gateway chết thì sao? 502 vs 504 debug thế nào?

---

## Ngày 53 — Thứ 5 (31/12): Resilience4j + Distributed tracing
**Kiến thức phải nắm:**
- **Timeout** → **Retry** (exponential backoff + jitter, chỉ retry thao tác idempotent) → **Circuit Breaker** (CLOSED → OPEN → HALF_OPEN, failure rate threshold, sliding window) → **Bulkhead** (giới hạn đồng thời) → **Rate limiter** → **Fallback**.
- **Retry storm**: retry bừa nhân tải lên service đang chết → sập dây chuyền (cascading failure).
- Graceful degradation (hết hàng cache → trả dữ liệu cũ).
- **Distributed tracing**: trace, span, context propagation; **Micrometer Tracing** + OpenTelemetry → Zipkin/Jaeger/Tempo; traceId trong log (MDC).

**Code tay:** Order → Catalog có `@CircuitBreaker` + `@Retry` + `@TimeLimiter` + fallback; tắt catalog-service và quan sát trạng thái circuit qua Actuator; chạy Zipkin, xem 1 trace đi Gateway → Order → Catalog.

**Tự kiểm tra:** Circuit breaker mở/đóng khi nào? Retry có thể gây thảm họa gì? Trace khác log thế nào?

---

## Ngày 54 — Thứ 6 (01/01 — Tết Dương lịch): Nghỉ / ôn nhẹ 1h
- Đọc lại note tuần 7–8, vẽ lại sơ đồ kiến trúc hiện tại bằng tay.
- (Tùy chọn) đọc bài "Microservices" của Martin Fowler / microservices.io các pattern: API Gateway, Database per Service, Saga, Outbox.

---

## Ngày 55 — Thứ 7 (02/01): Lab 4–5h — microservices chạy bằng Docker Compose
- `docker-compose.yml`: gateway, auth, catalog, order, Postgres (mỗi service 1 database/schema riêng), Redis, Zipkin.
- Kiểm thử luồng: login qua Gateway → lấy sản phẩm (cache) → tạo đơn (Order gọi Catalog) → tắt Catalog → thấy fallback/circuit open.
- Contract giữa service: DTO riêng mỗi service, không share Entity; (biết tên) consumer-driven contract test với Spring Cloud Contract/Pact.
- Cập nhật README + sơ đồ.

**LeetCode (30'):** Network Delay Time (Dijkstra), Longest Increasing Subsequence.

---

## Ngày 56 — Chủ nhật (03/01): Review + luyện miệng
**10 câu phỏng vấn của tuần:**
1. Monolith vs microservices, khi nào không nên tách?
2. Bounded context, aggregate trong dự án của bạn.
3. CAP theorem, eventual consistency.
4. Vì sao cần API Gateway? Gateway vs Load Balancer vs Nginx.
5. Auth đặt ở Gateway hay từng service?
6. Gateway thành SPOF thì giải quyết sao?
7. Service A gọi B mà B chết: timeout, retry, circuit breaker, fallback.
8. Circuit breaker các trạng thái.
9. Distributed tracing hoạt động thế nào?
10. Database per service — vậy query join nhiều service làm sao? (API composition, CQRS read model)

**Checklist:** ☐ Gateway + 3 service chạy compose ☐ Demo circuit breaker ☐ 1 trace trên Zipkin ☐ ADR-001

