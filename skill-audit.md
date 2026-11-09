# Bảng tự chấm kỹ năng — Theo dõi tiến độ Java Middle

Thang điểm đánh giá:
- **0**: Chưa từng nghe / chưa chạm vào.
- **1**: Biết tên, chưa làm bao giờ.
- **2**: Đã làm theo hướng dẫn / tutorial, chưa làm thật hoặc chưa giải thích được bản chất.
- **3**: Đã làm thật trong project, nhưng cần người khác review hoặc chưa nắm sâu các bẫy (traps).
- **4**: Làm độc lập được, giải thích được cơ chế bên dưới (under the hood) và vì sao chọn cách đó.
- **5**: Dạy lại được cho người khác, xử lý được sự cố production & các case khó.

| Kỹ năng | Điểm GĐ 0 (09/11) | Cuối Tháng 1 (27/12) | Cuối Tháng 2 (24/01) | Trước Tết (31/01) | Mục tiêu | Bằng chứng cụ thể (Link code / PR / Note) |
|---|---:|---:|---:|---:|---:|---|
| Java Core (OOP, SOLID, Collection, Stream) | 3 | | | | 5 | Làm Spring Boot + Angular từ 2022; Mini Order Engine |
| Concurrency / Multithreading / JVM / Virtual Threads | 2 | | | | 4 | Thread dump, Heap dump, Rate Limiter |
| Spring Boot / Spring MVC / AOP Internals | 3 | | | | 5 | Dự án migration FPT + Monolith `shop-order-platform` |
| JPA / Hibernate Internals / SQL tối ưu | 4 | | | | 5 | Tối ưu 10+ query chậm ở FPT + Báo cáo SQL 1M dòng |
| Spring Security / JWT / OAuth2 | 1 | | | | 4 | Auth JWT + Refresh Rotation + RBAC |
| Microservices / Spring Cloud / Resilience4j | 2 | | | | 4 | Tách 3 services + Circuit Breaker + Zipkin |
| API Gateway (Spring Cloud Gateway) | 1 | | | | 4 | Routing, JWT filter, Rate limit Redis |
| Kafka / RabbitMQ / Outbox / Saga | 1 | | | | 4 | Luồng đặt hàng async + Outbox + DLT + Đối soát |
| Redis / Caching / Distributed Lock | 1 | | | | 4 | Cache-aside, Idempotency 3 lớp, Lua Script |
| Docker (Multi-stage, non-root, Compose) | 2 | | | | 4 | Image < 250MB, full stack Docker Compose |
| Kubernetes (Pod, Deployment, Probes) | 1 | | | | 3 | Deploy trên minikube, debug CrashLoopBackOff |
| Linux / Quản trị server | 2 | | | | 4 | VPS thật, systemd, ufw, SSH, backup/restore Postgres |
| Mạng / DNS / TLS / Nginx / Load Balancer | 2 | | | | 4 | Domain HTTPS Let's Encrypt, sơ đồ request flow |
| Monitoring / Alerting (Prometheus, Grafana, Loki) | 2 | | | | 4 | Dashboard 8 panel + Alert Telegram ≤ 2 phút |
| CI/CD / Pipeline (Jenkins, GitLab, GitHub Actions) | 3 | | | | 5 | Pipeline FPT + GitHub Actions CI/CD + Rollback < 5p |
| Testing (JUnit 5, Mockito, Testcontainers) | 3 | | | | 4 | Coverage service ≥ 70%, Testcontainers Postgres |
| DSA / Live Coding | 2 | | | | 4 | ~60 bài LeetCode + Live Refactoring không gợi ý |
| System Design | 2 | | | | 4 | 4 bài tủ (Idempotency, Flash Sale, Saga, URL Shortener) |
| Tiếng Anh kỹ thuật | 3 | | | | 4 | Tự giới thiệu 2 phút + trả lời phỏng vấn cơ bản |

