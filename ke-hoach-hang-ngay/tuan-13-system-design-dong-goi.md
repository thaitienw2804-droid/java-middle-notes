# Tuần 13 (01/02 – 07/02/2027): System Design + Đóng gói đi phỏng vấn — chốt trước Tết

> Tết Nguyên Đán Đinh Mùi rơi vào **Thứ 7 06/02/2027**; công ty thường nghỉ từ khoảng 04–05/02. Tuần này **dồn việc vào Thứ 2 → Thứ 4**, từ Thứ 5 chỉ ôn nhẹ. Nộp CV trước Tết để sau Tết (mùa tuyển dụng cao điểm, nhiều người nhảy việc sau thưởng Tết) có lịch phỏng vấn ngay.

---

## Ngày 85 — Thứ 2 (01/02): System Design nền tảng
**Kiến thức phải nắm:**
- Khung trả lời System Design 45 phút: (1) làm rõ yêu cầu chức năng/phi chức năng, (2) ước lượng (QPS, storage — back-of-the-envelope), (3) API + data model, (4) high-level design, (5) đào sâu bottleneck, (6) trade-off.
- Building blocks: LB, cache (nhiều tầng: client/CDN/app/Redis/DB), DB replication (primary–replica, read-your-writes), **sharding/partitioning** (theo key, hot partition), consistent hashing, message queue, CDN, object storage (S3/MinIO cho file upload), search (Elasticsearch).
- Scale dọc vs scale ngang; stateless service; ID generation (UUID v7, Snowflake).

**Bài luyện (vẽ trên giấy/Excalidraw, tự nói 20 phút):** **URL Shortener** (base62, cache, redirect 301 vs 302, analytics bất đồng bộ).

**Tự kiểm tra:** Replication vs sharding? Consistent hashing để làm gì? Ước lượng QPS 1 triệu user/ngày.

---

## Ngày 86 — Thứ 3 (02/02): System Design thực chiến + CV v1
**Bài luyện:**
- **Chống thanh toán trùng / Idempotency 3 lớp** (`Idempotency-Key` ở Gateway + Redis Distributed Lock `SET NX EX` fail-fast `409` + DB Unique Constraint & State Machine `WHERE status = 'PENDING'`).
- **Rate limiter phân tán** (Redis + token bucket/sliding window bằng Lua Script, đặt ở Gateway, xử lý khi Redis chết — fail open hay fail close).
- **Hệ thống thông báo** (Kafka, nhiều kênh, retry bậc thang `@RetryableTopic`, ưu tiên, chống gửi trùng, template).
- **Flash sale / đặt hàng chịu tải cao (100k user tranh 1.000 sản phẩm)**: CDN + SSE/WebSocket cho Read Path → Rate Limit ở Gateway → **Redis Lua Script** kiểm tra & trừ kho atomic trên RAM → đẩy Kafka (`202 Accepted`) → Worker ghi DB + Job 15 phút hoàn kho đơn treo — gắn trực tiếp với project của bạn.
- **Vẽ trực tiếp kiến trúc dự án (Whiteboard Defense 15 phút)**: Luyện vừa vẽ trên Excalidraw vừa nói miệng kiến trúc dự án ở FPT và dự án `shop-order-platform` (Naver, Zalo, MB Bank, EPAM cực kỳ hay bắt vẽ trực tiếp khi phỏng vấn!). Always thêm hộp **Observability Triad (Prometheus/Grafana + Zipkin Tracing + Loki Logs + Alertmanager)**.

**CV v1 (2h):**
- Project shop-order-platform lên đầu phần project, có link GitHub + demo.
- Mỗi kinh nghiệm FPT viết theo công thức **Bối cảnh + Hành động kỹ thuật + Kết quả đo được** (vd: "Tối ưu 10+ query PostgreSQL bằng composite index + keyset pagination, giảm p95 từ 3.2s xuống 280ms").
- Skills chia nhóm: Backend / Data / Messaging / DevOps / Observability; chỉ ghi thứ bạn nói được ≥ 5 phút.
- Bản tiếng Anh song song (JD EPAM, ngân hàng yêu cầu tiếng Anh).

---

## Ngày 87 — Thứ 4 (03/02): Kể chuyện project + Behavioral + nộp CV
**Kể project (STAR / 2 phút – 5 phút – 15 phút):** vấn đề → kiến trúc & lý do → khó khăn kỹ thuật (oversell, dual write, N+1, circuit breaker) → kết quả có số liệu → nếu làm lại sẽ đổi gì.

**Behavioral (chuẩn bị 6 câu chuyện STAR):**
1. Giới thiệu bản thân 1–2 phút (tiếng Việt + tiếng Anh).
2. Một bug/incident production khó nhất bạn từng xử lý.
3. Bất đồng với đồng nghiệp/lead về kỹ thuật.
4. Deadline gấp, bạn ưu tiên thế nào.
5. Bạn học công nghệ mới thế nào (kể lộ trình 3 tháng này!) + dùng AI coding assistant ra sao mà vẫn kiểm soát chất lượng.
6. Vì sao nghỉ chỗ cũ / vì sao chọn công ty này; mong muốn lương (tìm hiểu mặt bằng Middle trên ITviec/TopDev trước).

**Câu hỏi ngược cho nhà tuyển dụng:** quy trình review/CI-CD, on-call, cách đo hiệu suất, lộ trình lên Senior.

**Nộp CV:** 5–10 vị trí (ITviec, TopDev, LinkedIn, giới thiệu nội bộ), ghi vào bảng theo dõi (công ty, ngày nộp, vòng, phản hồi).

**Video demo 3–5 phút** (tùy thời gian): kiến trúc → đặt hàng → Kafka/Saga → Grafana → alert → pipeline.

---

## Ngày 88 — Thứ 5 (04/02): Mock interview tổng (2h, nhẹ)
- Tự mock (hoặc nhờ bạn/AI hỏi): 45' kỹ thuật ngẫu nhiên từ ngân hàng câu hỏi tuần 1–12 + 15' live coding 1 bài medium + 15' kể project.
- Ghi âm, đánh dấu câu trả lời > 2 phút hoặc ấp úng → đưa vào danh sách ôn sau Tết.

## Ngày 89–91 — Thứ 6 → Chủ nhật (05–07/02): Nghỉ Tết 🧧
- Không học nặng. Nếu muốn: mỗi ngày 30' nghe lại ghi âm / đọc flashcard.

---

## Sau Tết: Giai đoạn cày phỏng vấn chuyên sâu (Tuần 14–17: 15/02 → 15/03/2027)
> Chi tiết từng tuần sau Tết và chiến lược thi theo từng nhóm công ty xem tại: [`tuan-14-17-chien-luoc-phong-van-sau-tet.md`](tuan-14-17-chien-luoc-phong-van-sau-tet.md) và [`../ngan-hang-cau-hoi-phong-van-theo-do-kho.md`](../ngan-hang-cau-hoi-phong-van-theo-do-kho.md).

| Mỗi ngày (2–2.5h) | Mỗi tuần |
|---|---|
| 40' ôn theo độ khó tăng dần (Level 1 → Level 4 trong ngân hàng câu hỏi) | 2 bài System Design (có vẽ Excalidraw + nhớ đưa Observability vào) |
| 35' 1 bài Live Coding Medium (không dùng gợi ý IDE, tự viết test case + đọc Big-O) | 1–2 buổi Mock Interview ghi âm (bao gồm 1 buổi bằng tiếng Anh) |
| 25' ôn lại 3 câu chuyện dự án FPT + dự án `shop-order-platform` | Rải CV theo 3 đợt (Wave 1 thử lửa → Wave 2 mục tiêu → Wave 3 chốt deal) |
| 20' hậu kiểm sau mỗi buổi phỏng vấn thật: ghi lại câu bị hỏi + viết lại đáp án chuẩn | Cập nhật bảng theo dõi phỏng vấn & lương offer |

**Checklist "đủ tự tin" (cuối tuần 13):** xem cuối file lộ trình v3.

