# Tuần 11 (18/01 – 24/01/2027): Observability, Alerting, Incident + Kubernetes cơ bản

> Skill audit: Monitoring = 2, K8s = 1 → mục tiêu 4 và 3. Kết quả tuần: **service chết → Telegram của bạn rung trong ≤ 2 phút**.

---

## Ngày 71 — Thứ 2 (18/01): Health check + Actuator + Metrics
**Kiến thức phải nắm:**
- 3 trụ cột observability: **Metrics** (số liệu theo thời gian), **Logs** (sự kiện chi tiết), **Traces** (đường đi request). Monitoring vs observability.
- Spring **Actuator**: `/health` (+ health group, indicator DB/Redis/Kafka), `/info`, `/metrics`, `/prometheus`; bảo mật endpoint actuator (không expose public).
- **Liveness vs Readiness** (và Startup probe): liveness fail → restart; readiness fail → rút khỏi LB. Sai lầm: đưa DB vào liveness → DB chập chờn làm restart toàn bộ pod.
- **Micrometer**: Counter, Gauge, Timer, Distribution Summary; tag/label và **cardinality** (đừng gắn userId vào label).
- Phương pháp: **RED** (Rate, Errors, Duration) cho service, **USE** (Utilization, Saturation, Errors) cho tài nguyên; **4 Golden Signals**.

**Code tay:** bật actuator + `micrometer-registry-prometheus` cho mọi service; custom metric `orders_created_total`, `order_processing_seconds`.

**Tự kiểm tra:** Liveness vs readiness, dùng sai hậu quả gì? Metrics/logs/traces khi nào xem cái nào? Cardinality là gì?

---

## Ngày 72 — Thứ 3 (19/01): Prometheus + Grafana
**Kiến thức phải nắm:**
- Prometheus **pull model**, scrape config, time series, label; exporter (node_exporter, postgres_exporter, kafka exporter).
- **PromQL** cơ bản: `rate(http_server_requests_seconds_count[5m])`, error rate, `histogram_quantile(0.95, sum by (le) (rate(..._bucket[5m])))`, `up == 0`.
- Grafana: datasource, dashboard, panel, variable; import dashboard JVM (Micrometer) và Spring Boot.
- Dashboard tốt: request rate, error rate, p95/p99 latency, JVM heap/GC, Hikari pool, Kafka consumer lag, CPU/RAM/disk.

**Code tay:** compose thêm Prometheus + Grafana + node_exporter; dashboard "Shop Overview" với 8 panel trên.

**Tự kiểm tra:** Pull vs push model? Viết PromQL tính error rate và p95.

**LeetCode (20'):** Evaluate Reverse Polish Notation, Generate Parentheses.

---

## Ngày 73 — Thứ 4 (20/01): Log tập trung + Alerting + Status page
**Kiến thức phải nắm:**
- Structured log JSON (Logback encoder), field chuẩn: timestamp, level, service, traceId, userId (không log PII/password/token).
- Log tập trung: **Loki + Promtail/Alloy** (nhẹ) vs **ELK/EFK** (Elasticsearch mạnh tìm kiếm, nặng); log level dùng đúng.
- **Alertmanager**: rule trong Prometheus (`for: 2m`), routing, grouping, inhibition, silence; kênh Telegram/Email/Slack.
- **Nguyên tắc alert**: chỉ alert khi cần con người hành động; alert theo triệu chứng (error rate, latency, down) hơn là nguyên nhân (CPU 80%); severity → kênh (P1 gọi/Telegram đêm, P3 email sáng); mỗi alert có **runbook**; tránh alert fatigue.
- SLI / **SLO** / SLA, error budget (khái niệm).
- Status page: **Uptime Kuma** (tự host) / Better Stack; ai xem (khách hàng, nội bộ).

**Code tay:** Alertmanager → bot Telegram: `ServiceDown` (up==0 for 2m), `HighErrorRate` (>5% 5m), `HighLatencyP95`, `DiskAlmostFull`; Uptime Kuma check `https://api.<domain>/actuator/health`.

**Tự kiểm tra:** Alert nào gửi Telegram nửa đêm, alert nào gửi email? SLO là gì? Vì sao alert theo triệu chứng?

---

## Ngày 74 — Thứ 5 (21/01): Incident management + chiến lược deploy
**Kiến thức phải nắm:**
- Severity P1/P2/P3; quy trình: phát hiện → xác nhận → **giảm thiểu trước (rollback/feature flag), tìm nguyên nhân sau** → thông báo status page → postmortem **blameless** (timeline, ảnh hưởng, root cause — 5 Whys, action items).
- On-call, runbook mẫu.
- **Deploy không downtime**: rolling update, **blue-green**, **canary**; feature flag; graceful shutdown (`server.shutdown=graceful`) + readiness để không rớt request.
- Migration DB backward-compatible (expand → migrate → contract) để rollback code không vỡ.

**Code tay:** viết `docs/runbooks/service-down.md` và `docs/incidents/2027-01-21-catalog-down.md`; giả lập: kill catalog-service → nhận alert → khôi phục → viết postmortem.

**Tự kiểm tra:** Incident P1 bạn làm gì 15 phút đầu? Rolling vs blue-green vs canary? Vì sao migration phải backward-compatible?

**LeetCode (20'):** Koko Eating Bananas, Find Minimum in Rotated Sorted Array.

---

## Ngày 75 — Thứ 6 (22/01): Kubernetes cơ bản
**Kiến thức phải nắm:**
- Vì sao cần orchestrator; kiến trúc: control plane (API server, etcd, scheduler, controller manager) + node (kubelet, kube-proxy, container runtime).
- Object: **Pod**, **Deployment** (ReplicaSet, rolling update, `kubectl rollout undo`), **Service** (ClusterIP/NodePort/LoadBalancer), **Ingress**, **ConfigMap**, **Secret** (chỉ base64!), Namespace, StatefulSet (DB), HPA (autoscale), resources requests/limits, probes.
- Pod restart loop: `CrashLoopBackOff`, `OOMKilled`, `ImagePullBackOff` — debug bằng `kubectl describe`, `logs --previous`, `get events`.

**Code tay:** minikube/kind: deploy catalog-service (Deployment 2 replica + Service + ConfigMap + Secret + probes + limits), Ingress; `kubectl rollout` image mới rồi `undo`.

**Tự kiểm tra:** Pod vs Deployment vs Service? Pod bị CrashLoopBackOff debug thế nào? requests vs limits?

---

## Ngày 76 — Thứ 7 (23/01): Lab 4–5h — monitoring hoàn chỉnh + chaos nhỏ
- Hoàn thiện stack trên VPS: Prometheus, Grafana, Alertmanager→Telegram, Loki, Uptime Kuma, Zipkin/Tempo (Grafana/Loki chỉ truy cập qua SSH tunnel hoặc có auth).
- Kịch bản "chaos" và chụp ảnh từng bước: (1) tắt service → alert, (2) bắn k6 tải cao → latency alert, (3) consumer Kafka dừng → lag tăng, (4) từ traceId trong log → mở trace.
- (Tùy chọn) đưa toàn bộ lên minikube bằng manifest/Helm.

**LeetCode (30'):** Search a 2D Matrix, Time Based Key-Value Store.

---

## Ngày 77 — Chủ nhật (24/01): Review + luyện miệng
**10 câu phỏng vấn của tuần:**
1. Server chết lúc 2h sáng thì ai biết, biết bằng cách nào, trong bao lâu?
2. Liveness vs readiness vs startup probe.
3. Metrics, logs, traces khác nhau thế nào?
4. Bạn giám sát những chỉ số nào cho 1 service Java (RED, JVM, pool, lag)?
5. PromQL tính error rate / p95.
6. Nguyên tắc thiết kế alert; SLO.
7. Quy trình xử lý incident + postmortem.
8. Rolling, blue-green, canary.
9. Pod, Deployment, Service, Ingress trong K8s.
10. Pod CrashLoopBackOff / OOMKilled — debug thế nào?

**Checklist:** ☐ Ảnh alert Telegram thật ☐ Dashboard Grafana ☐ Runbook + postmortem mẫu ☐ Deploy được 1 service lên minikube

