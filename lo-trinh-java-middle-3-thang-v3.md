# Lộ trình Java Middle v3 — Chắc nền trước, hệ thống sau, project đắp dần
**Thời gian: 09/11/2026 → 07/02/2027 (13 tuần, chốt trước Tết Đinh Mùi 06/02/2027), sau Tết (15/02 → 15/03/2027) cày phỏng vấn 4 tuần.**
- **Kế hoạch chi tiết từng ngày:** [`ke-hoach-hang-ngay/`](ke-hoach-hang-ngay/README.md)
- **Chiến lược 4 tuần Sau Tết & Đóng gói kinh nghiệm FPT:** [`ke-hoach-hang-ngay/tuan-14-17-chien-luoc-phong-van-sau-tet.md`](ke-hoach-hang-ngay/tuan-14-17-chien-luoc-phong-van-sau-tet.md)
- **Ngân hàng câu hỏi + Đáp án chi tiết theo 4 mức độ khó (Level 1 → 4):** [`ngan-hang-cau-hoi-phong-van-theo-do-kho.md`](ngan-hang-cau-hoi-phong-van-theo-do-kho.md)
- **Bảng tự chấm kỹ năng:** [`skill-audit.md`](skill-audit.md)

> v3 giữ nguyên tinh thần v2 (Network, DNS, Gateway, Server, Monitoring, CI/CD thật) nhưng **sửa các lỗ hổng kiến thức nền** và **sắp lại thứ tự** để ưu tiên "nắm chắc từng phần".

---

## 1. Review v2 — cái gì ổn, cái gì thiếu

### ✅ Điểm mạnh của v2 (giữ lại)
- Nguyên tắc 3 đầu ra (nói 2 phút + demo + note 1 trang), demo Chủ nhật, ghi âm trả lời.
- Phần Network/DNS, Server/Monitoring/Alert, CI/CD rất kỹ — hơn hầu hết lộ trình tự học.
- Project "Shop Order & Notification Platform" đúng tầm Middle, có phi chức năng (ăn điểm).
- Ngân hàng câu hỏi DNS / Gateway / Server / CI-CD tốt.

### ⚠️ Vấn đề cấu trúc
| Vấn đề | Hậu quả | Sửa trong v3 |
|---|---|---|
| Tuần 1 nhồi Java Core + Concurrency + JVM + Git | Với người "bắt đầu lại", học lướt → phỏng vấn vòng 1 rớt ở câu cơ bản | Tách thành **3 tuần** (Core+OOP / Collections+Patterns / Concurrency+JVM) |
| Tháng 3 viết lại project từ đầu (4 tuần) trong khi tuần 2–9 đã code các phần | Trùng lặp, tốn 3 tuần | **Đắp project dần từ tuần 4**, tuần 12–13 chỉ hardening + đóng gói |
| Không tính lịch lễ: Giáng sinh, Tết Dương 01/01, **Tết Âm 06/02/2027** | Tuần 13 rơi đúng Tết | Ngày lễ để nhẹ, tuần 13 dồn việc vào Thứ 2–4 |
| Không có DSA/live coding | JD Middle (VPBank, EPAM…) ghi rõ DSA/CS fundamentals | **LeetCode 3–5 bài/tuần** xuyên suốt (~60 bài trước Tết) |
| Ngân hàng câu hỏi thiếu Java Core, Spring, JPA, SQL, Kafka, Redis, Security | Đây lại là 60–70% câu hỏi thực tế | Bổ sung (mục 5) + 10 câu/tuần trong file từng ngày |

### ❌ Kiến thức còn thiếu (đã thêm vào v3)
Đối chiếu JD Java Middle trên ITviec (MB Bank, EPAM, VPBank Securities, MSB/SBSI, LG CNS, bbv) và roadmap.sh/java, roadmap.sh/questions/java (tra cứu 11/2026):

| Nhóm | Thiếu ở v2 | Thêm vào tuần |
|---|---|---|
| Java Core | Pass-by-value, String Pool/immutability, Integer cache, BigDecimal cho tiền, static/final, OOP 4 tính chất, abstract vs interface, Reflection, Annotation, Serialization/`transient` | 1, 4 |
| Thiết kế | **SOLID**, **Design Patterns** (Singleton, Factory, Builder, Strategy, Observer, Proxy, Template, Chain of Responsibility, Adapter, Decorator) | 1, 2 |
| Concurrency/JVM | Java Memory Model/happens-before, CAS, ThreadLocal leak, 7 tham số ThreadPoolExecutor, ClassLoader, các loại OOM, heap/thread dump thực hành | 3 |
| Spring | Bean scope, lifecycle, **proxy JDK vs CGLIB, self-invocation `@Transactional`**, auto-configuration, circular dependency, Filter vs Interceptor, `@Async`, `@Scheduled` + ShedLock, file upload, MapStruct/Lombok, logging SLF4J + MDC, Maven | 4 |
| Database | Chuẩn hóa, ACID, **MVCC**, optimistic/pessimistic lock (`@Version`), oversell, HikariCP, NoSQL (MongoDB/Elasticsearch) mức trade-off | 5 |
| Security | OWASP chi tiết (IDOR, SQL injection), HS256 vs RS256, refresh token rotation, revoke JWT, OAuth2 Authorization Code + PKCE, Client Credentials | 6 |
| Redis | Cache penetration/breakdown/avalanche, eviction, RDB vs AOF, Sentinel vs Cluster, distributed lock đúng cách | 7 |
| Docker | **Dockerfile multi-stage, layer cache, non-root, JVM trong container, exit 137** (v2 chỉ dùng compose, không học Docker) | 7 |
| API | WebSocket/SSE, HTTP caching, p95/p99 | 7 |
| Microservices | CAP/PACELC, Strangler Fig, contract test, Bulkhead, retry storm | 8 |
| Kafka | ISR/`acks`/`min.insync.replicas`, KRaft, rebalance, consumer lag, Schema Registry, Inbox, CDC/Debezium | 9 |
| Kubernetes | v2 chỉ nhắc "nếu kịp" → thêm 1 ngày K8s cơ bản + debug CrashLoopBackOff/OOMKilled | 11 |
| Vận hành | SLI/SLO, RED/USE, cardinality, graceful shutdown, postmortem blameless | 11 |
| CI/CD | Trivy/Dependabot/gitleaks, OIDC, GitOps (khái niệm), Git nâng cao (`bisect`, `revert` vs `reset`) | 12 |
| Phỏng vấn | System Design có khung trả lời, Behavioral STAR, tự giới thiệu tiếng Anh, AI-assisted coding (JD EPAM/bbv yêu cầu) | 13 |

---

## 2. Nguyên tắc (giữ từ v2, thêm 2 điều)
1. **Mỗi chủ đề = 3 đầu ra:** giải thích miệng 2 phút + demo chạy được + ghi chú 1 trang.
2. **Không học chay.** Đọc xong phải gõ tay, phá thử, sửa lỗi.
3. **Một repo ghi chú + một repo project duy nhất** (`java-middle-notes`, `shop-order-platform`).
4. **Chủ nhật nào cũng demo + ghi âm 10 câu phỏng vấn của tuần.**
5. **(Mới) Chưa nói được thì chưa qua bài:** mỗi ngày có 3 câu "Tự kiểm tra" — trả lời to không nhìn tài liệu, không được thì ôn lại trước khi sang ngày sau.
6. **(Mới) DSA đều đặn, không dồn:** 20–30 phút, 3–5 bài/tuần, ưu tiên pattern (HashMap, two pointers, sliding window, stack, BFS/DFS, binary search, heap, DP cơ bản).

### Lịch chuẩn (đi làm ban ngày)
- Thứ 2–6: 2.5h/tối = 45' lý thuyết + 75' code + 30' ghi chú/tự hỏi đáp (+20' LeetCode các ngày có ghi).
- Thứ 7: 4–5h lab/project. Chủ nhật: 2–3h review, README, luyện miệng.
- ~25h/tuần. Tuần có lễ: ngày lễ chỉ 1–1.5h ôn nhẹ.

---

## 3. Lộ trình 13 tuần (v3)

| Tuần | Ngày | Chủ đề | Đầu ra kiểm chứng | File chi tiết |
|---|---|---|---|---|
| 0 | (đã xong) | Reset + setup, skill audit, CV v0 | compose Postgres + Redis, PR flow | [`skill-audit.md`](skill-audit.md) |
| 1 | 09–15/11 | Java Core nền + OOP + SOLID | Mini Order Engine thuần Java + 10 câu | [tuần 1](ke-hoach-hang-ngay/tuan-01-java-core-oop.md) |
| 2 | 16–22/11 | Collections (HashMap internals), Generics, Stream, Java 17/21, Design Patterns | LRU cache, 7 pattern có code + Live Refactoring | [tuần 2](ke-hoach-hang-ngay/tuan-02-collections-generics-patterns.md) |
| 3 | 23–29/11 | Concurrency + JVM + GC + Virtual Threads traps | Thread dump, heap dump thật, rate limiter | [tuần 3](ke-hoach-hang-ngay/tuan-03-concurrency-jvm.md) |
| 4 | 30/11–06/12 | Spring Core/Boot/MVC bên trong (3-level cache, Proxy) | shop-order-platform khung monolith + Chuyện FPT #1 | [tuần 4](ke-hoach-hang-ngay/tuan-04-spring-core-boot-mvc.md) |
| 5 | 07–13/12 | SQL sâu + JPA/Hibernate Internals (ActionQueue, Batch, Locking) | Báo cáo tối ưu SQL + Chuyện FPT #2, chống oversell | [tuần 5](ke-hoach-hang-ngay/tuan-05-sql-jpa.md) |
| 6 | 14–20/12 | Spring Security + JWT + Testing | Auth + RBAC + Testcontainers xanh | [tuần 6](ke-hoach-hang-ngay/tuan-06-security-testing.md) |
| 7 | 21–27/12 | API design, Idempotency 3 lớp, Redis Lua, Performance, Docker sâu | Monolith Docker hóa + bảng performance | [tuần 7](ke-hoach-hang-ngay/tuan-07-api-redis-performance-docker.md) |
| 8 | 28/12–03/01 | Microservices + API Gateway + Resilience + Tracing | Gateway + 3 service, demo circuit breaker | [tuần 8](ke-hoach-hang-ngay/tuan-08-microservices-gateway.md) |
| 9 | 04–10/01 | Kafka Production Traps, RabbitMQ, Outbox, Saga, Đối soát | Luồng đặt hàng async + 4 demo lỗi + Đối soát | [tuần 9](ke-hoach-hang-ngay/tuan-09-kafka-distributed.md) |
| 10 | 11–17/01 | Network, DNS, TLS, Nginx, Linux | Domain HTTPS trên VPS, backup/restore | [tuần 10](ke-hoach-hang-ngay/tuan-10-network-dns-linux.md) |
| 11 | 18–24/01 | Observability Triad, Alert, Incident, K8s cơ bản | Alert Telegram thật, runbook, minikube | [tuần 11](ke-hoach-hang-ngay/tuan-11-observability-k8s.md) |
| 12 | 25–31/01 | CI/CD + Hardening + README | Pipeline xanh + bằng chứng rollback + Chuyện FPT #3 | [tuần 12](ke-hoach-hang-ngay/tuan-12-cicd-hardening.md) |
| 13 | 01–07/02 | System Design, CV v1, Behavioral, nộp job Wave 1, **Tết** | CV v1, đã nộp 5–10 job | [tuần 13](ke-hoach-hang-ngay/tuan-13-system-design-dong-goi.md) |
| **14–17** | **15/02–15/03** | **Cày phỏng vấn Sau Tết (Level 1→4), Live Coding, System Design, Deal Offer** | **Đạt 2–3 Offer Java Middle** | **[tuần 14–17](ke-hoach-hang-ngay/tuan-14-17-chien-luoc-phong-van-sau-tet.md)** |

```mermaid
flowchart LR
  A["T1-3 Java Core, OOP, Concurrency, JVM"] --> B["T4-7 Spring, SQL/JPA, Security, Redis, Docker - monolith"]
  B --> C["T8-9 Microservices, Gateway, Kafka - tách service"]
  C --> D["T10-12 Network, Linux, Monitoring, K8s, CI/CD - lên VPS"]
  D --> E["T13 System Design + CV + nộp job"]
  E --> F["Sau Tết: cày phỏng vấn"]
```

---

## 4. Project: Shop Order & Notification Platform (đắp dần, không viết lại)

| Tuần | Project tiến hóa thế nào |
|---|---|
| 4 | Monolith modular: user/product/order, error contract, validation, MDC log, upload ảnh, `@Scheduled` |
| 5 | Postgres + Flyway, chống oversell, keyset pagination, báo cáo SQL |
| 6 | JWT + refresh + RBAC, unit + integration test (Testcontainers) |
| 7 | Idempotency, rate limit, Redis cache, k6, Dockerfile multi-stage |
| 8 | Tách Auth/Catalog/Order + Gateway + Resilience4j + Zipkin |
| 9 | Inventory, Payment mock, Notification; Kafka + Outbox + Saga + DLT |
| 10 | Lên VPS: domain, Nginx HTTPS, ufw, backup |
| 11 | Prometheus/Grafana/Alertmanager→Telegram, Loki, Uptime Kuma, minikube |
| 12 | GitHub Actions CI/CD, Trivy, approve prod, rollback, README hoàn chỉnh |

**Kiến trúc cuối:**
```
Client → DNS → Nginx (HTTPS) → API Gateway (JWT, rate limit, routing)
  → Auth | Catalog (Postgres + Redis) | Inventory | Order (Postgres + Outbox) | Payment Mock | Notification
  ↕ Kafka: order.created, inventory.reserved/failed, payment.completed/failed, order.confirmed/cancelled
  ↕ Redis: cache, rate limit, idempotency key, distributed lock
  ↕ Observability: Actuator + Prometheus + Grafana + Alertmanager → Telegram, Loki, Zipkin, Uptime Kuma
  ↕ CI/CD: GitHub Actions → staging tự động, production approve tay, rollback theo image tag
```
Yêu cầu chức năng & phi chức năng giữ như v2.

---

## 5. Ngân hàng câu hỏi phỏng vấn (Tổng hợp đầy đủ 12 nhóm)

> 💡 **Bản có đáp án chi tiết phân theo 4 mức độ khó tăng dần (Level 1 → Level 4):** Xem tại [`ngan-hang-cau-hoi-phong-van-theo-do-kho.md`](ngan-hang-cau-hoi-phong-van-theo-do-kho.md).

### Java Core & OOP
1. JDK/JRE/JVM; Java chạy từ source đến mã máy (bytecode, JIT).
2. Stack vs Heap; Java pass-by-value hay reference?
3. String immutable vì sao; String Pool; String vs StringBuilder vs StringBuffer.
4. `==` vs `equals`; hợp đồng equals/hashCode.
5. Abstract class vs interface (sau Java 8); overloading vs overriding.
6. Checked vs unchecked exception; try-with-resources; `finally` có luôn chạy?
7. SOLID với ví dụ thực tế; composition over inheritance.
8. 3–4 design pattern bạn đã dùng thật trong dự án; Singleton thread-safe.
9. Record, sealed class, pattern matching dùng khi nào?
10. Vì sao tiền dùng BigDecimal; `equals` vs `compareTo` của BigDecimal.

### Collections & Stream
1. HashMap hoạt động thế nào (hash, bucket, treeify, resize, load factor).
2. HashMap vs Hashtable vs ConcurrentHashMap (Java 7 vs 8).
3. ArrayList vs LinkedList; fail-fast vs fail-safe.
4. Type erasure, PECS.
5. `map` vs `flatMap`; `orElse` vs `orElseGet`; khi nào tránh parallelStream.

### Concurrency & JVM
1. synchronized vs volatile vs Atomic; happens-before.
2. Deadlock: điều kiện, phòng tránh, phát hiện trên production.
3. 7 tham số ThreadPoolExecutor; queue đầy thì sao; vì sao tránh `Executors.newFixedThreadPool`.
4. CompletableFuture kết hợp & xử lý lỗi; `@Async` cấu hình thế nào.
5. Virtual Thread: là gì, khi nào dùng, pinning.
6. Cấu trúc bộ nhớ JVM; G1 GC; Minor vs Full GC.
7. App OOM trên production — các bước xử lý (heap dump, MAT).
8. ThreadLocal dùng ở đâu, leak khi nào.

### Spring & JPA
1. IoC/DI; vì sao constructor injection; bean scope; singleton bean có thread-safe?
2. Bean lifecycle; BeanPostProcessor tạo proxy ở đâu.
3. AOP, JDK proxy vs CGLIB; **vì sao `@Transactional` không chạy khi self-invocation / method private**.
4. Auto-configuration hoạt động thế nào.
5. Luồng request Spring MVC; Filter vs Interceptor vs AOP.
6. Transaction propagation (`REQUIRED` vs `REQUIRES_NEW`), isolation, rollback checked exception.
7. Entity lifecycle, persistence context, dirty checking; `persist` vs `merge`.
8. N+1: nguyên nhân, 3–4 cách fix; fetch join + phân trang có vấn đề gì.
9. LazyInitializationException; vì sao tắt open-in-view.
10. `@Scheduled` chạy nhiều instance thì sao (ShedLock).

### SQL & Database
1. ACID; 4 mức isolation và hiện tượng; mặc định Postgres/MySQL.
2. B-Tree index, composite index (leftmost prefix), covering index; khi nào index không được dùng.
3. Đọc EXPLAIN ANALYZE — kể 1 lần tối ưu query có số liệu.
4. MVCC là gì; optimistic vs pessimistic lock; chống oversell.
5. Deadlock DB; long transaction.
6. Phân trang dữ liệu lớn (offset vs keyset).
7. SQL vs NoSQL; khi nào MongoDB, khi nào Elasticsearch.
8. Replication vs sharding.

### Security
1. Authentication vs authorization; Spring Security filter chain.
2. JWT cấu trúc; HS256 vs RS256; JWT vs session.
3. Refresh token lưu ở đâu, rotation, revoke/logout.
4. OAuth2 vs OIDC; Authorization Code + PKCE; Client Credentials.
5. CORS vs CSRF; SQL injection; IDOR.
6. Hash vs encrypt vs encode; vì sao BCrypt.

### Redis & Kafka
1. Cache-aside; update DB rồi xóa cache hay ngược lại?
2. Cache penetration / breakdown / avalanche.
3. Distributed lock bằng Redis đúng cách; RDB vs AOF; Sentinel vs Cluster.
4. Kafka: partition, consumer group, offset; giữ thứ tự message.
5. Mất message / trùng message; at-least-once vs exactly-once.
6. Retry + DLQ; Outbox; Saga choreography vs orchestration.
7. Kafka vs RabbitMQ; consumer lag cao xử lý thế nào.

### Behavioral
1. Giới thiệu bản thân 1–2 phút (Việt + Anh).
2. Bug/incident production khó nhất.
3. Bất đồng kỹ thuật với lead/đồng nghiệp.
4. Bạn dùng AI coding assistant thế nào mà vẫn đảm bảo chất lượng?
5. Vì sao chuyển việc; mong muốn 1–2 năm tới.

### DNS & Network (Gộp đầy đủ từ v2)
1. Gõ `api.shop.com` thì chuyện gì xảy ra, theo đúng thứ tự?
2. Record A, CNAME, MX, TXT khác nhau thế nào? TTL để làm gì, đổi DNS sao lâu mới có hiệu lực?
3. DNS cache ở những tầng nào? Vì sao `dig` ra IP mới mà máy mình vẫn ra IP cũ?
4. TCP handshake 3 bước để làm gì? UDP dùng khi nào?
5. HTTP/1.1, HTTP/2, HTTP/3 khác nhau ở điểm nào đáng kể nhất?
6. TLS handshake làm gì? Certificate hết hạn thì user thấy gì, server phải làm gì?
7. Load Balancer L4 và L7 khác nhau thế nào? Sticky session là gì, khi nào cần, khi nào tránh?
8. Reverse Proxy khác Forward Proxy thế nào? Nginx trong hệ thống của bạn đóng vai trò gì?
9. CORS thực chất là ai chặn ai? Vì sao Postman gọi được mà trình duyệt lại chặn?
10. NAT, public IP, private IP, VPC/subnet giải thích trong 1 phút?
11. Timeout, retry, circuit breaker: retry bừa có thể gây thảm họa gì?
12. Khi API chậm, bạn chẩn đoán theo thứ tự nào: network, gateway, app, DB, hay service khác?

### API Gateway
1. Vì sao cần Gateway, client gọi thẳng từng service thì sao?
2. Gateway làm những việc gì: routing, auth, rate limit, CORS, logging, tracing?
3. Gateway chết thì sao? Làm sao để Gateway không thành single point of failure?
4. Rate limiting implement ở Gateway hay ở từng service, hay cả hai?
5. Auth nên đặt ở Gateway hay ở từng service? Token validate ở đâu?
6. API versioning đặt ở Gateway có lợi gì?
7. Gateway khác Nginx và khác Load Balancer ở điểm nào?
8. Request đi qua Gateway bị lỗi 502/504 thì bạn debug từ đâu trước?

### Server, Giám sát và Thông báo trạng thái
1. Server chết lúc 2h sáng thì ai biết, biết bằng cách nào, trong bao lâu?
2. Health check, readiness và liveness khác nhau thế nào? Dùng sai thì hậu quả gì?
3. Disk đầy, RAM đầy, CPU 100%: thứ tự xử lý và lệnh kiểm tra của bạn là gì?
4. Log nên ghi những gì, không nên ghi những gì? Log tập trung để làm gì?
5. Metrics, Logs, Traces khác nhau thế nào? Khi nào xem cái nào?
6. Alert nào đáng gửi Telegram lúc nửa đêm, alert nào chỉ nên gửi email sáng hôm sau?
7. Backup Postgres thế nào, restore thử bao lâu một lần? Backup chưa restore thử thì sao?
8. Deploy không downtime bằng rolling, blue-green, canary — chọn cái nào cho hệ thống nhỏ/lớn?
9. Migration DB không backward-compatible thì deploy sẽ vỡ ở bước nào?
10. Incident P1 xảy ra, bạn viết incident note gồm những mục nào?
11. Status page để làm gì, ai xem, cập nhật bằng tay hay tự động?

### CI/CD & Pipeline
1. Kể toàn bộ pipeline của project bạn, từ lúc tạo PR đến lúc chạy production.
2. Pipeline gồm những stage nào, stage nào fail thì dừng, stage nào chỉ cảnh báo?
3. Secret quản lý thế nào, vì sao không commit secret vào code?
4. Deploy staging tự động, deploy production cần approve tay — vì sao phân biệt vậy?
5. Image Docker tag bằng `latest` có nguy hiểm gì? Bạn tag bằng gì?
6. Pipeline chậm thì tối ưu từ đâu: cache, job song song, hay bỏ test?
7. Test fail trên CI nhưng chạy local lại pass — bạn debug thế nào?
8. Rollback nhanh nhất trong hệ thống của bạn là gì, mất bao lâu?
9. Jenkins, GitHub Actions, GitLab CI khác nhau ở điểm nào, bạn chọn cái nào và vì sao?
10. Database migration chạy trong pipeline ở bước nào, rollback migration thì sao?
11. Monorepo nhiều service thì pipeline chỉ build service đổi code bằng cách nào?
12. Làm sao biết deploy mới có lỗi: xem metric nào, trong bao lâu, ai quyết định rollback?

### System Design & Backend tổng hợp
1. Thiết kế hệ thống đặt hàng / Flash Sale chịu tải cao: bottleneck đầu tiên ở đâu, chống oversell bằng Redis Lua + Kafka thế nào?
2. Đặt hàng trùng, trừ kho trùng, thanh toán trùng — thiết kế Idempotency phòng thủ 3 lớp thế nào? Khi gọi cổng thanh toán bị timeout (không biết đã trừ tiền chưa) thì xử lý và đối soát (Reconciliation) ra sao?
3. Khi nào dùng Kafka, khi nào dùng RabbitMQ, khi nào chỉ cần gọi REST đồng bộ?
4. Cache ở những tầng nào, cache sai thì hậu quả gì, invalidate thế nào?
5. Bảng 1 tỷ dòng đã đánh index nhưng query vẫn chậm 30s — kiểm tra index, selectivity, covering index, keyset pagination, partitioning theo thứ tự nào?

---

## 6. Checklist "đủ tự tin đi phỏng vấn"
- [ ] Trả lời trôi chảy ≥ 80% ngân hàng câu hỏi (v2 + v3) không nhìn tài liệu.
- [ ] Vẽ & giải thích HashMap, bean lifecycle, luồng Spring Security, luồng DNS → TCP → TLS → Nginx → Gateway → Service → DB trong 3 phút mỗi cái.
- [ ] Giải ~60 bài LeetCode (easy + medium), làm được 1 bài medium trong 25 phút vừa code vừa nói.
- [ ] Kể được project ở 3 độ dài (2' / 5' / 15') với số liệu thật.
- [ ] Demo được: đặt hàng → Kafka/Saga → Grafana → alert Telegram → pipeline → rollback.
- [ ] Repo có README, sơ đồ, ADR, pipeline xanh.
- [ ] CV v1 (Việt + Anh) có số liệu; đã nộp ≥ 5 job trước Tết.
- [ ] Đã chuẩn bị 6 câu chuyện STAR.

**Sau Tết (15/02 → 15/03/2027):** mỗi ngày 2h: flashcard Java/Spring/SQL + 1 LeetCode medium + ôn note tuần yếu; mỗi tuần 2 bài System Design + 1–2 mock interview ghi âm; sau mỗi buổi phỏng vấn thật ghi lại câu hỏi trong 30 phút.

