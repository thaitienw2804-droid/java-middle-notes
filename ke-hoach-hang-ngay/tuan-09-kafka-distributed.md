# Tuần 9 (04/01 – 10/01/2027): Kafka, RabbitMQ và các bài toán phân tán

> Skill audit: Kafka/MQ = 1 → mục tiêu 4. Đây là chủ đề JD ngân hàng/chứng khoán (MB, MSB, VPBank…) hay yêu cầu nhất sau Spring.

---

## Ngày 57 — Thứ 2 (04/01): Kafka kiến trúc
**Kiến thức phải nắm:**
- Message queue giải quyết gì: decoupling, buffer tải đỉnh, async.
- Kafka = **distributed commit log**: Broker, Topic, **Partition** (đơn vị song song + thứ tự), Offset, Segment, retention (theo thời gian/dung lượng — message không bị xóa khi đọc), log compaction.
- **Replication**: leader/follower, **ISR**, replication factor, `min.insync.replicas`.
- **KRaft** thay ZooKeeper (Kafka 4.0 bỏ hẳn ZooKeeper).
- Vì sao Kafka nhanh: ghi tuần tự, page cache, zero-copy, batch + nén.

**Code tay:** chạy Kafka (KRaft) + Kafka UI bằng Docker Compose; tạo topic 3 partition bằng CLI; produce/consume bằng `kafka-console-*`, quan sát offset.

**Tự kiểm tra:** Partition để làm gì? ISR là gì? Message đọc xong có bị xóa không?

---

## Ngày 58 — Thứ 3 (05/01): Producer, Consumer, Consumer Group, thứ tự
**Kiến thức phải nắm:**
- Producer: key → partition (cùng key cùng partition ⇒ **giữ thứ tự theo key**, vd orderId); `acks=0/1/all`; `retries`; **idempotent producer** (`enable.idempotence=true`, mặc định từ 3.0); batch `linger.ms`, `batch.size`.
- Consumer Group: mỗi partition chỉ 1 consumer trong group xử lý; số consumer > số partition thì thừa; **rebalance** và tác hại; cooperative rebalancing.
- **Offset commit**: auto vs manual; commit trước xử lý → **at-most-once** (mất), xử lý rồi commit → **at-least-once** (trùng) ⇒ consumer phải **idempotent**.
- **Exactly-once**: transactional producer + `read_committed` — chỉ trong phạm vi Kafka→Kafka; với DB vẫn cần idempotency.
- Consumer lag và vì sao phải giám sát.

**Code tay:** Spring Kafka: `order-service` publish `OrderCreated` (key = orderId), `inventory-service` consume với manual ack; chạy 2 instance consumer, quan sát rebalance; tắt consumer giữa chừng, bật lại xem có trùng message.

> **⚠️ 3 Bẫy Kafka Consumer trên Production cực hay bị hỏi (MB Bank / Viettel / MoMo / Grab):**
> 1. **Rebalance Storm (Bão Rebalance làm tê liệt Consumer Group)**:
>    - *Nguyên nhân*: Consumer kéo 1 mẻ lớn (`max.poll.records = 500`), mỗi message gọi DB/HTTP hoặc retry mất 1s → tổng thời gian 500s vượt quá `max.poll.interval.ms` (mặc định 300s). Broker tưởng Consumer chết nên đá khỏi group và kích hoạt **Stop-the-World Rebalance**. Consumer khác nhận lại đúng 500 message chưa commit đó và lại quá hạn tiếp → lặp vô tận, lag tăng dựng đứng!
>    - *Cách sửa*: Hạ `max.poll.records` (VD: `50`), không retry blocking lâu trên luồng poll, dùng `CooperativeStickyAssignor`, và bật **Static Group Membership (`group.instance.id`)** để khi rolling update pod trên K8s không gây rebalance.
> 2. **Bẫy tự tạo `ExecutorService` bên trong `@KafkaListener` làm mất message**:
>    - Nếu nhận message trong `@KafkaListener` rồi đẩy sang thread pool riêng để chạy bất đồng bộ, hàm `@KafkaListener` kết thúc ngay → Spring Kafka **commit offset ngay lập tức** trong khi worker thread chưa xử lý xong! Nếu pod crash lúc đó là mất trắng message.
>    - *Cách sửa*: Tăng tốc độ xử lý bằng cách tăng **số Partition + cấu hình `concurrency`** của `@KafkaListener` (mỗi luồng phụ trách riêng partition).
> 3. **Bẫy đảo thứ tự sự kiện khi Retry (`OrderCancelled` chạy trước `OrderCreated`)**:
>    - Dù đã truyền `key = orderId` để vào cùng 1 partition, nếu 1 message bị đẩy sang `@RetryableTopic` thì message sau có thể được xử lý trước!
>    - *Cách sửa*: Dưới DB luôn kiểm tra **State Machine hợp lệ** (chỉ cho chuyển `PENDING → CONFIRMED`, không cho chuyển ngược từ `CANCELLED → CONFIRMED`) kết hợp kiểm tra `event_version / timestamp`.

**Tự kiểm tra:** Làm sao giữ thứ tự message? At-least-once vs exactly-once? Rebalance là gì, khi nào xảy ra?

**LeetCode (20'):** Find Median from Data Stream, Merge K Sorted Lists.

---

## Ngày 59 — Thứ 4 (06/01): Retry, DLQ, Idempotent consumer, RabbitMQ
**Kiến thức phải nắm:**
- Lỗi khi consume: retry blocking vs **non-blocking retry topics** (`@RetryableTopic`), **Dead Letter Topic/Queue**, phân loại lỗi tạm thời vs lỗi vĩnh viễn (poison message).
- **Idempotent consumer**: bảng `processed_events(event_id)` unique trong cùng transaction với nghiệp vụ.
- Schema & tiến hóa event: JSON vs Avro/Protobuf + Schema Registry (biết khái niệm), backward compatibility.
- **RabbitMQ**: Producer → **Exchange** (direct, topic, fanout, headers) → Binding/Routing key → Queue → Consumer; ack/nack, prefetch, DLX, TTL.
- **Kafka vs RabbitMQ**: log bền vững có replay, throughput cao, streaming/event sourcing vs smart broker, routing linh hoạt, task queue, per-message ack. Chọn sai là trừ điểm.

**Code tay:** `@RetryableTopic` 3 lần backoff → DLT; consumer idempotent bằng bảng processed_events; (1h) chạy RabbitMQ, làm 1 task queue gửi email với DLX.

**Tự kiểm tra:** Poison message xử lý thế nào? Consumer idempotent làm sao? Kafka vs RabbitMQ chọn gì cho gửi email, cho event log đơn hàng?

---

## Ngày 60 — Thứ 5 (07/01): Outbox Pattern + Distributed transaction
**Kiến thức phải nắm:**
- **Dual write problem**: lưu DB xong rồi gửi Kafka — crash ở giữa thì mất event, hoặc gửi event rồi DB rollback.
- Vì sao không dùng **2PC/XA** trong microservices (blocking, coordinator SPOF, không phải broker nào cũng hỗ trợ).
- **Transactional Outbox**: ghi nghiệp vụ + bản ghi `outbox` trong cùng 1 transaction DB → relay đẩy lên Kafka: **polling publisher** (`@Scheduled` + `FOR UPDATE SKIP LOCKED`) hoặc **CDC** (Debezium đọc WAL). Outbox vẫn là at-least-once ⇒ consumer idempotent.
- Inbox pattern (phía consumer).

**Code tay:** `order-service` ghi `orders` + `outbox_events` cùng transaction; publisher định kỳ đẩy lên Kafka và đánh dấu đã gửi; giả lập Kafka chết → đơn vẫn tạo được, Kafka sống lại event được gửi bù.

**Tự kiểm tra:** Outbox giải quyết bài toán gì? Polling vs CDC? Vì sao không dùng 2PC?

**LeetCode (20'):** Word Break, House Robber.

---

## Ngày 61 — Thứ 6 (08/01): Saga, CQRS, Event Sourcing
**Kiến thức phải nắm:**
- **Saga**: chuỗi local transaction + **compensating transaction** khi lỗi.
  - **Choreography** (service phát/nghe event, đơn giản, khó theo dõi khi nhiều bước) vs **Orchestration** (1 orchestrator điều phối, dễ quan sát; Temporal/Camunda biết tên).
  - Ví dụ: OrderCreated → InventoryReserved → PaymentCompleted → OrderConfirmed; Payment failed → ReleaseInventory → OrderCancelled.
  - Trạng thái đơn hàng là **state machine** (PENDING → CONFIRMED / CANCELLED).
- **CQRS**: tách model ghi và model đọc (read model denormalized, có thể ở Elasticsearch/Redis), đồng bộ bằng event → eventual consistency.
- **Event Sourcing**: lưu chuỗi event thay vì state hiện tại; replay; snapshot; độ phức tạp cao — biết khi nào không nên dùng.

**Code tay:** vẽ sequence diagram Saga choreography cho luồng đặt hàng (happy path + payment fail); thêm `payment-mock-service` (random fail 30%) và `notification-service`.

> **⚠️ Bài toán Nghiệp vụ Thanh toán Timeout & Đối soát (Reconciliation) — Câu hỏi "Tủ" của Ngân hàng & Ví điện tử:**
> - *Câu hỏi*: Khi `Order/Payment Service` gọi sang cổng thanh toán (Napas / VNPAY / MoMo) mà bị **`SocketTimeoutException` (Timeout sau 10s)** — lúc này bạn **không biết bên kia đã trừ tiền khách hàng hay chưa**. Bạn xử lý thế nào?
> - *Đáp án chuẩn Middle*:
>   1. **Tuyệt đối KHÔNG đánh dấu đơn là `FAILED` hay tự động hoàn tiền/hủy đơn ngay!** (Vì nếu bên kia đã trừ tiền thật thì công ty mất tiền).
>   2. Chuyển trạng thái giao dịch sang **`UNKNOWN` / `PROCESSING`**.
>   3. Đẩy vào **Job truy vấn trạng thái (Query Transaction Status API)** chạy theo lịch backoff (sau 30s, 2m, 5m, 15m) gọi sang cổng thanh toán kèm `transactionRef` để hỏi kết quả thật sự, rồi mới chốt `SUCCESS` hoặc `FAILED`.
>   4. **Đối soát cuối ngày (Reconciliation T+1)**: Mỗi đêm hệ thống tải file sao kê/bảng đối soát từ đối tác về so khớp với DB nội bộ để phát hiện các giao dịch lệch trạng thái (Chênh lệch thừa/thiếu) và tự động tạo bản ghi điều chỉnh hoặc cảnh báo vận hành.

**Tự kiểm tra:** Saga choreography vs orchestration? Compensation khác rollback thế nào? CQRS dùng khi nào?

---

## Ngày 62 — Thứ 7 (09/01): Lab 4–5h — luồng đặt hàng bất đồng bộ end-to-end
- Topics: `order.created`, `inventory.reserved`, `inventory.failed`, `payment.completed`, `payment.failed`, `order.confirmed`, `order.cancelled`.
- Order (Outbox) → Inventory (reserve, idempotent) → Payment mock → Order cập nhật state → Notification (log/Telegram).
- Demo bắt buộc: (1) gửi đơn trùng Idempotency-Key không trừ kho 2 lần, (2) payment fail → hoàn kho, (3) message lỗi vào DLT, (4) Kafka chết rồi sống lại không mất event.
- `@Scheduled` relay chạy 2 instance dùng **ShedLock** hoặc `SKIP LOCKED` để không gửi trùng.
- Viết ADR-002 "Vì sao chọn Kafka + Outbox + Saga choreography".

**LeetCode (30'):** Edit Distance, Longest Common Subsequence.

---

## Ngày 63 — Chủ nhật (10/01): Review + luyện miệng
**10 câu phỏng vấn của tuần:**
1. Kafka gồm những thành phần nào; partition và consumer group quan hệ thế nào?
2. Làm sao đảm bảo thứ tự message?
3. Mất message thì sao (`acks`, ISR, commit offset)? Trùng message thì sao?
4. At-most / at-least / exactly-once.
5. Retry + DLQ; poison message.
6. Kafka vs RabbitMQ.
7. Dual write và Outbox pattern.
8. Vì sao không dùng 2PC cho microservices?
9. Saga choreography vs orchestration — ví dụ đặt hàng.
10. Consumer lag tăng cao, bạn xử lý thế nào?

**Checklist:** ☐ 4 demo lỗi chạy được ☐ Sequence diagram Saga ☐ ADR-002 ☐ Note tuần 9

