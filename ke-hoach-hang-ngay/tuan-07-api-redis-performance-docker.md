# Tuần 7 (21/12 – 27/12/2026): API Design + Redis + Performance + Docker chuyên sâu

> Tuần có Giáng sinh (Thứ 6 25/12) — ngày 5 để nhẹ hơn. Cuối tuần này shop-order-platform (monolith) phải "đóng gói được" bằng Docker trước khi tách microservices tuần 8.

---

## Ngày 43 — Thứ 2 (21/12): REST API design chuẩn
**Kiến thức phải nắm:**
- REST constraints (stateless, resource-based, uniform interface); đặt tên resource (danh từ số nhiều, lồng tối đa 2 cấp).
- HTTP method + **tính idempotent/safe**: GET/PUT/DELETE idempotent, POST không, PATCH tùy.
- Status code dùng đúng: 200/201 (+ `Location`)/204, 400/401/403/404/409/422/429, 500/502/503/504.
- Pagination (offset vs cursor), filter, sort; versioning (URL `/v1` vs header vs media type) — trade-off.
- Error contract thống nhất; `ProblemDetail`.
- **OpenAPI/Swagger** (springdoc) — API-first vs code-first.
- REST vs **gRPC** (HTTP/2, protobuf, nội bộ service) vs **GraphQL** (client chọn field, vấn đề N+1/caching) vs **WebSocket/SSE** (realtime: thông báo trạng thái đơn).
- HATEOAS (biết tên).

**Code tay:** rà soát toàn bộ API shop-order-platform theo checklist trên; thêm springdoc; viết `docs/api-guidelines.md`.

**Tự kiểm tra:** PUT vs PATCH? 401 vs 403, 400 vs 422, 502 vs 504? Versioning bằng URL hay header?

---

## Ngày 44 — Thứ 3 (22/12): Idempotency, Rate limit, API an toàn
**Kiến thức phải nắm:**
- **Idempotency key**: client gửi header `Idempotency-Key` → server lưu (key, request hash, response, trạng thái) → lần sau trả lại response cũ; xử lý 2 request trùng đến cùng lúc (unique constraint/lock); TTL.
- **Rate limiting** thuật toán: fixed window, sliding window log/counter, **token bucket**, leaky bucket; theo IP/user/API key; trả `429` + `Retry-After`.
- Timeout cho mọi lời gọi ra ngoài (RestClient/WebClient), không có timeout = treo thread.
- Giới hạn request size, validation đầu vào, không lộ stack trace.

**Code tay:** filter idempotency cho `POST /orders` (lưu Postgres trước, Redis sau ở ngày 45); rate limit bằng Bucket4j.

**Tự kiểm tra:** Idempotency implement thế nào, 2 request trùng đồng thời thì sao? So sánh các thuật toán rate limit.

**LeetCode (20'):** LRU Cache (tự viết bằng HashMap + doubly linked list), Insert Interval.

---

## Ngày 45 — Thứ 4 (23/12): Redis chuyên sâu
**Kiến thức phải nắm:**
- Redis single-threaded (xử lý lệnh) vì sao vẫn nhanh: in-memory, I/O multiplexing; Redis 6+ có I/O threads.
- Kiểu dữ liệu & use case: String (cache, counter, lock), Hash (object), List, Set, **Sorted Set** (leaderboard, sliding window rate limit), Bitmap/HyperLogLog (biết tên), Stream.
- TTL, chính sách **eviction** (`allkeys-lru`, `volatile-ttl`…), persistence **RDB vs AOF**.
- **Cache strategy**: cache-aside (phổ biến), read-through, write-through, write-behind. Invalidate: xóa cache sau khi update DB (vì sao xóa thay vì update).
- Sự cố cache: **cache penetration** (key không tồn tại → cache null/Bloom filter), **cache breakdown/stampede** (hot key hết hạn → mutex/lock, refresh sớm), **cache avalanche** (nhiều key hết hạn cùng lúc → TTL ngẫu nhiên).
- **Distributed lock**: `SET key value NX PX`, giải phóng bằng Lua kiểm tra value; Redisson (watchdog); hạn chế của Redlock.
- HA: replication, **Sentinel**, **Cluster** (16384 hash slots).
- Spring: `@Cacheable/@CacheEvict` (nhớ self-invocation!), `RedisTemplate`, serializer JSON.

**Code tay:** cache chi tiết sản phẩm (cache-aside + TTL ngẫu nhiên), idempotency key sang Redis, distributed lock cho job `@Scheduled` (hoặc ShedLock), rate limit sliding window bằng Sorted Set.

**Tự kiểm tra:** Penetration vs breakdown vs avalanche? Update DB rồi xóa cache hay ngược lại? Distributed lock bằng Redis làm thế nào cho đúng?

---

## Ngày 46 — Thứ 5 (24/12): Performance & đo đạc
**Kiến thức phải nắm:**
- Chỉ số: latency (**p50/p95/p99** — vì sao không dùng trung bình), throughput (RPS), error rate, saturation.
- **HikariCP**: công thức kích thước pool tham khảo, pool lớn quá cũng hại; `leakDetectionThreshold`.
- Tomcat threads, timeout, keep-alive; virtual threads cho I/O-bound.
- Nguyên nhân API chậm: query/index, N+1, lock chờ, pool cạn, gọi service ngoài, GC pause, serialize payload lớn, log quá nhiều.
- Công cụ: **k6** hoặc JMeter (load test), VisualVM/JFR/async-profiler (profiling), `pg_stat_statements` (slow query).
- Nén response (gzip), HTTP caching (`ETag`, `Cache-Control`).

**Code tay:** viết script k6 cho `GET /products/{id}` và `POST /orders`; đo p95 trước/sau cache Redis; bảng số liệu vào `docs/performance.md`.

**Tự kiểm tra:** p99 là gì, vì sao quan trọng? API chậm bạn chẩn đoán theo thứ tự nào? Pool size chọn thế nào?

---

## Ngày 47 — Thứ 6 (25/12 — Giáng sinh, nhẹ 1.5h): Docker chuyên sâu (lý thuyết)
**Kiến thức phải nắm:**
- Container vs VM; **namespaces** (pid, net, mnt…) + **cgroups** (giới hạn CPU/RAM) — bạn đã note ở day3/day4, giờ nói được sâu hơn.
- Image layers + cache build: thứ tự lệnh trong Dockerfile ảnh hưởng tốc độ build.
- **Multi-stage build** (build bằng Maven image, chạy bằng JRE slim/distroless), hoặc Spring Boot layered jar / Buildpacks / Jib.
- `CMD` vs `ENTRYPOINT`, `COPY` vs `ADD`, `.dockerignore`, chạy bằng **non-root user**, `HEALTHCHECK`.
- Network: bridge, host, DNS nội bộ theo tên service trong compose; port mapping.
- Volume vs bind mount; dữ liệu DB phải nằm ở volume.
- JVM trong container: container-aware (Java 10+), `-XX:MaxRAMPercentage=75`, bị OOMKilled (exit code 137) khác Java OOM.

**Tự kiểm tra:** Vì sao image Java 600MB → giảm còn ~200MB? Exit code 137 là gì? Container gọi nhau qua tên thế nào?

---

## Ngày 48 — Thứ 7 (26/12): Lab 4–5h — Docker hóa toàn bộ
- Dockerfile multi-stage cho shop-order-platform, chạy non-root, có healthcheck, image < 250MB.
- `docker-compose.yml`: app + Postgres + Redis, `depends_on` với `condition: service_healthy`, env tách file `.env` (đã có `env.example` — kiểm tra `.env` **không** bị commit).
- Load test bằng k6 khi chạy trong container, giới hạn `mem_limit` và quan sát.
- Lệnh debug phải thuộc: `docker logs -f`, `exec -it`, `inspect`, `stats`, `system df`, `image prune`.

**LeetCode (30'):** Container With Most Water, Subsets (backtracking).

---

## Ngày 49 — Chủ nhật (27/12): Review tháng 1 (mở rộng) + luyện miệng
- **Chấm lại toàn bộ `skill-audit.md`** (cuối tháng 1).
- Ghi âm 10 câu tuần + chọn ngẫu nhiên 10 câu từ tuần 1–6.

**10 câu phỏng vấn của tuần:**
1. Idempotency là gì, implement cho API thanh toán thế nào?
2. Các thuật toán rate limit, chọn cái nào?
3. Cache-aside; cập nhật DB và cache theo thứ tự nào?
4. Cache penetration / breakdown / avalanche.
5. Distributed lock bằng Redis.
6. Redis persistence RDB vs AOF; Sentinel vs Cluster.
7. REST vs gRPC vs GraphQL vs WebSocket.
8. p95/p99 và cách bạn đo performance.
9. Dockerfile multi-stage, tối ưu image.
10. Container bị kill exit code 137 — nguyên nhân & xử lý.

**Checklist:** ☐ Monolith chạy bằng `docker compose up` ☐ docs/performance.md có số liệu ☐ Idempotency + rate limit ☐ Chấm lại skill-audit

