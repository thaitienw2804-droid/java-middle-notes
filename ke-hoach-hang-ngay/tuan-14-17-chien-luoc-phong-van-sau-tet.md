# Tuần 14–17 (15/02 – 15/03/2027): Chiến Lược Cày Phỏng Vấn Sau Tết & Đóng Gói Kinh Nghiệm Thực Chiến

> **Mục tiêu giai đoạn Sau Tết (Mùa tuyển dụng cao điểm tháng 2–3/2027)**:
> Trước Tết (Tuần 1–13) bạn đã xây xong nền tảng + project `shop-order-platform` + CV v1. Giai đoạn 4 tuần sau Tết này giúp bạn **biến kiến thức thành phản xạ trả lời trong 30 giây**, nắm rõ **luật chơi của từng nhóm công ty tại Việt Nam**, và **đóng gói 4 năm kinh nghiệm ở FPT** thành những câu chuyện đạt chuẩn Middle cứng.

---

## 1. Bản Đồ Phỏng Vấn Java Middle Tại Việt Nam (Phân Loại Theo 3 Nhóm Công Ty)

Dựa trên dữ liệu khảo sát thực tế từ ITviec, Viblo, TopDev và review phỏng vấn của ứng viên Java Middle (2–4 năm kinh nghiệm):

### Nhóm A — Ngân hàng & Fintech Việt Nam (MB Bank, Viettel, TCB, VPBank, MoMo, VNPAY)
- **Cấu trúc vòng thi**:
  1. **Vòng Test (Đặc thù Viettel, một số khối Bank)**: Thi tập trung gồm **IQ Test + Tiếng Anh định dạng TOEIC + Trắc nghiệm chuyên môn Java/Spring/SQL**. *(Cần ôn trước các câu trắc nghiệm bẫy Java Core/SQL để không mất điểm đáng tiếc)*.
  2. **Vòng Kỹ Thuật Chuyên Sâu (45–90 phút với 2 Tech Lead / Solution Architect)**:
     - Hỏi xoáy 60% vào **Dự án thực tế trong CV (FPT + Shop Order)** và 40% vào **Java Concurrency, JPA/Hibernate Internals, SQL Bảng lớn & Locking, Redis, Kafka**.
     - Các bài toán nghiệp vụ "tủ" bắt buộc phải thuộc: **Chống thanh toán trùng (Idempotency 3 lớp)**, **Xử lý khi gọi cổng thanh toán Napas/VNPAY bị Timeout (Trạng thái `UNKNOWN` + Job Query + Đối soát T+1)**, **Chống âm tiền / âm kho (TOCTOU, Pessimistic/Optimistic Lock, Deadlock Lock Ordering)**, **Flash Sale bằng Redis Lua Script + Kafka**.
  3. **Vòng Lãnh Đạo / PGĐ Trung Tâm (20–30 phút)**:
     - Hỏi tình huống chịu tải (*"Khi lưu lượng tăng đột biến gấp 10 lần giờ cao điểm, thiết kế thế nào để user không phải chờ đồng bộ mà vẫn nhất quán dữ liệu?"*) + đánh giá thái độ gắn bó, chịu áp lực và kỹ năng **ứng dụng AI trong phát triển phần mềm (AI Coding có kiểm soát)**.

### Nhóm B — Ngân hàng Quốc tế & Global Outsourcing / Tech Hub (NAB, ANZ, TymeX, EPAM, KMS, NashTech)
- **Cấu trúc vòng thi**:
  1. **Vòng HR Screening bằng Tiếng Anh (10–15 phút)**: Giới thiệu bản thân, dự án từng làm, lý do chuyển việc bằng tiếng Anh.
  2. **Vòng Online Assessment (Codility / HackerRank — 60–120 phút)**:
     - 2 bài thuật toán Easy–Medium + 1–2 câu SQL phức tạp + (ở TymeX/KMS) bài **Refactoring code bẩn theo OOP/SOLID**.
  3. **Vòng Technical & Live Coding (75–120 phút)**:
     - Hỏi theo rubric bài bản từ Java Core → Concurrency → JPA/Hibernate → Spring → Microservices.
     - **Live Coding (Không dùng gợi ý IDE/AI)**: Viết code sạch giải 1 bài DSA Medium hoặc xử lý tập dữ liệu bằng Java Stream API + tự viết Unit Test cho các edge case (`null`, rỗng, biên) + đọc độ phức tạp Big-O.
  4. **Vòng System Design (ANZ / NAB / EPAM)**:
     - **⚠️ Bẫy rớt thực tế tại ANZ**: Thiết kế đầy đủ API, Database, Cache, Scaling nhưng **quên đưa Monitoring, Metrics, Alerting và Distributed Tracing (Prometheus/Grafana/OpenTelemetry)** vào bản vẽ → bị đánh rớt hoặc hạ level!

### Nhóm C — Big Tech / Product (Grab, Shopee, Zalo, Naver)
- **Cấu trúc vòng thi**:
  1. **Online Assessment / Vòng Code (2 bài Medium–Hard + SQL Window Functions)**: Ưu tiên thành thạo Sliding Window, Two Pointers, HashMap/Prefix Sum, Binary Search, PriorityQueue (Min/Max Heap), BFS/DFS, DP cơ bản.
  2. **Vòng Vẽ Kiến Trúc Trực Tiếp (Whiteboard Defense — Naver, Zalo, Grab)**:
     - Yêu cầu mở màn hình/bảng trắng **vẽ lại toàn bộ kiến trúc dự án hiện tại của bạn** và bảo vệ từng thành phần: Tại sao đặt Redis ở đây? Invalidate cache thế nào? Rate limit chạy thuật toán gì? Kafka chết hay Consumer xử lý lỗi thì sao?
     - Câu hỏi vận hành bắt buộc: *"Nếu p99 latency của API chính tăng vọt từ 100ms lên 5.000ms lúc 2h chiều, bạn khoanh vùng nguyên nhân gốc rễ trong 10 phút theo thứ tự nào?"*

---

## 2. Đóng Gói 3 Câu Chuyện "Đinh" Từ 4 Năm Kinh Nghiệm FPT (60% Điểm Phỏng Vấn)

> **Nguyên tắc vàng**: Nhà tuyển dụng tuyển bạn vì **4 năm kinh nghiệm thực tế ở FPT + tư duy hệ thống từ project `shop-order-platform`**. Tuyệt đối không để xảy ra tình trạng hỏi sang dự án FPT thì trả lời chung chung "em chỉ nhận task viết API CRUD". Hãy chuẩn bị sẵn 3 bản thiết kế câu chuyện dưới đây theo cấu trúc **STAR + Số liệu**:

### Câu chuyện FPT #1: Dự án Migration sang Spring Boot + Angular
- **Bối cảnh (Situation)**: Hệ thống cũ gặp vấn đề gì (công nghệ cũ khó bảo trì,耦合 chặt, thiếu chuẩn hóa API/error, thời gian build/deploy lâu)? Quy mô bao nhiêu module/API?
- **Hành động kỹ thuật của bạn (Action)**:
  - Áp dụng tư duy **Strangler Fig Pattern**: tách và chuyển đổi từng cụm chức năng sang Spring Boot mà không làm gián đoạn người dùng.
  - Chuẩn hóa kiến trúc tầng: DTO + MapStruct (không lộ Entity), Bean Validation, `GlobalExceptionHandler` thống nhất mã lỗi, gắn `requestId` vào MDC Log để truy vết.
  - Xử lý các cạm bẫy `@Transactional` (self-invocation, tách lời gọi HTTP ra khỏi transaction bằng `TransactionTemplate`).
- **Kết quả đo được (Result)**: Chuyển đổi thành công $X$ module / $Y$ API, giảm lỗi regression, rút ngắn thời gian phát triển feature mới.

### Câu chuyện FPT #2: Tối ưu 10+ Câu Truy Vấn PostgreSQL Chậm (Vũ khí mạnh nhất của bạn)
- **Chuẩn bị sẵn 3 trường hợp cụ thể nhất**:
  1. **Ca 1 — Thiếu Composite/Covering Index hoặc viết SQL làm mất tác dụng Index**:
     - *Trước*: Câu query lọc theo trạng thái + khoảng thời gian + bọc hàm trên cột khiến PostgreSQL chạy `Seq Scan` trên bảng hàng triệu dòng, mất `3.5s`.
     - *Sau*: Đọc `EXPLAIN (ANALYZE, BUFFERS)`, viết lại điều kiện SARGable (bỏ hàm trên cột), đánh **Composite Index `(status, created_at DESC) INCLUDE (total_amount)`** → chuyển sang `Index Only Scan`, giảm còn `80ms`.
  2. **Ca 2 — Lỗi N+1 Query & Phân trang dữ liệu lớn (`OFFSET` sâu)**:
     - *Trước*: API danh sách load quan hệ con bị N+1 (101 câu query cho trang 100 dòng) hoặc dùng `OFFSET` lớn làm chậm dần.
     - *Sau*: Áp dụng **DTO Projection / `@BatchSize(size = 50)`** + đổi sang **Keyset Pagination** → số query giảm từ 101 xuống 1–2 query, p95 giảm từ `2.8s` xuống `150ms`.
  3. **Ca 3 — Chống Deadlock / Lock Wait hoặc Batch Insert**:
     - Kể cách cấu hình `reWriteBatchedInserts=true` + `SEQUENCE` để tăng tốc độ import/xử lý lô gấp 10 lần, hoặc cách rút ngắn thời gian giữ transaction để giải phóng HikariCP.

### Câu chuyện FPT #3: Vận hành CI/CD với Jenkins & GitLab CI
- **Những điểm phải nói trôi chảy**:
  - Sơ đồ các stage trong Jenkinsfile / `.gitlab-ci.yml` ở dự án FPT (Build → Test → SonarQube → Build Image → Deploy).
  - So sánh trực tiếp với pipeline GitHub Actions bạn tự dựng từ đầu cho `shop-order-platform` (quản lý Secret, quét lỗ hổng Trivy, Manual Approval cho Production, chiến lược Rollback trong 3 phút bằng Image Commit SHA và миграция Flyway Expand/Contract).

---

## 3. Lịch Trình Chi Tiết 4 Tuần Sau Tết (Tuần 14 → Tuần 17)

### Tuần 14 (15/02 – 21/02/2027): Khởi động lại sau Tết + Nắm chắc Level 1 & Level 2 + Rải CV Đợt 1 (Wave 1)
- **Mục tiêu**: Lấy lại 100% phong độ sau kỳ nghỉ Tết, phản xạ tức thì với các câu hỏi nền tảng và bắt đầu đi phỏng vấn thử lửa.
- **Thứ 2 – Thứ 6 (2.5h/tối)**:
  - **60'**: Ôn toàn bộ **Level 1 & Level 2** trong [`ngan-hang-cau-hoi-phong-van-theo-do-kho.md`](../ngan-hang-cau-hoi-phong-van-theo-do-kho.md) — tự bốc câu hỏi ngẫu nhiên, trả lời to trong 90 giây.
  - **45'**: 1 bài Live Coding Medium (tập code trên trình soạn thảo tắt gợi ý hoặc giấy trắng, tự đọc to suy nghĩ bằng tiếng Việt/Anh).
  - **45'**: Luyện vẽ trên Excalidraw kiến trúc dự án FPT + `shop-order-platform` trong 10 phút không nhìn tài liệu.
- **Thứ 7 – Chủ Nhật**:
  - Làm 1 bài thi thử **Online Assessment 90 phút** (2 bài DSA + 2 câu SQL Window Function + 15 câu trắc nghiệm Java/Spring).
  - **Rải CV Đợt 1 (Wave 1 — 5 đến 8 công ty)**: Chọn các công ty vừa sức hoặc phỏng vấn nhanh để lấy cảm giác phòng thi thật và rà soát lỗ hổng.

### Tuần 15 (22/02 – 28/02/2027): Làm chủ Bẫy Thực Chiến (Level 3) + Phỏng vấn Wave 1 + Rải CV Đợt 2 (Wave 2)
- **Mục tiêu**: Thuộc lòng toàn bộ cơ chế bên dưới của các bẫy Level 3 (Hibernate `ActionQueue`, N+1 + `Pageable` OOM, HikariCP Starvation, Virtual Threads Pinning, Redis Lua/Stampede, Kafka Rebalance Storm).
- **Thứ 2 – Thứ 6 (2.5h/tối)**:
  - **60'**: Cày sâu **Level 3** trong ngân hàng câu hỏi + mở code trong `shop-order-platform` ra chạy lại các bản demo lỗi để nhớ cảm giác thực tế.
  - **45'**: Luyện **Live Coding Refactoring** (chuyển code `if-else` sang Stream + Strategy Pattern + viết Unit Test JUnit 5/Mockito).
  - **45'**: Sau mỗi buổi phỏng vấn thật của Wave 1 → ghi chép ngay mọi câu bị hỏi vào sổ tay trong vòng 30 phút, câu nào trả lời chưa mượt thì viết lại đáp án chuẩn.
- **Thứ 7 – Chủ Nhật**:
  - Mock Interview 60 phút bằng **Tiếng Anh** (tự giới thiệu, kể dự án FPT, trả lời 10 câu Java/Spring/Microservices cơ bản bằng tiếng Anh).
  - **Rải CV Đợt 2 (Wave 2 — 8 đến 10 công ty mục tiêu chính)**: Ngân hàng lớn (MB, Viettel, TCB, VPBank), Fintech (MoMo, VNPAY), Global Tech Hub (EPAM, NAB, ANZ, TymeX, KMS).

### Tuần 16 (01/03 – 07/03/2027): Đỉnh cao System Design (Level 4) & Chinh phục Vòng Kỹ Thuật Wave 2
- **Mục tiêu**: Vượt qua vòng Technical Deep-Dive & System Design của các công ty mục tiêu.
- **Thứ 2 – Thứ 6 (2h/tối)**:
  - **60'**: Luyện nói & vẽ 4 bài **System Design Level 4**:
    1. Chống thanh toán trùng (Idempotency 3 lớp) + Xử lý Timeout cổng thanh toán & Đối soát T+1.
    2. Hệ thống Flash Sale 100k user (CDN + Gateway Rate Limit + Redis Lua Script + Kafka + Reconciliation).
    3. Giao dịch phân tán Microservices (Transactional Outbox + Debezium CDC + Saga + Idempotent Consumer).
    4. Tối ưu bảng 1 tỷ dòng + Chẩn đoán sự cố API p99 tăng vọt lên 5s trong 10 phút (**Luôn nhớ vẽ hộp Observability Triad!**).
  - **60'**: Ôn tập theo đúng JD của từng công ty sắp phỏng vấn vào ngày hôm sau.
- **Thứ 7 – Chủ Nhật**:
  - Chuẩn bị cho **Vòng Lãnh Đạo / Hiring Manager (Round 2/3)**: Ôn kỹ 6 câu chuyện STAR Behavioral, cách trả lời về sử dụng AI Coding an toàn/hiệu quả, và bộ câu hỏi ngược thông minh dành cho người phỏng vấn.

### Tuần 17 (08/03 – 15/03/2027): Vòng Cuối (Hiring Manager), So Sánh Offer & Deal Lương
- **Mục tiêu**: Chốt từ 2–3 Offer và đàm phán mức đãi ngộ xứng đáng.
- **Chiến lược Deal Lương (Salary Negotiation)**:
  - Không báo một con số cứng quá sớm ở vòng HR nếu chưa biết rõ gói thu nhập (13 tháng hay 14–16 tháng lương như khối Ngân hàng, thưởng hiệu suất, bảo hiểm, hybrid/remote).
  - Luôn tính theo **Tổng thu nhập năm (Total Annual Compensation = Gross tháng × Số tháng cam kết + Thưởng dự kiến)**.
  - Khi có từ 2 offer trở lên: Dùng thế mạnh kỹ thuật đã thể hiện ở vòng Technical (hiểu sâu cả Code, Database, Kafka/Redis lẫn Hạ tầng/CI-CD/Observability) làm cơ sở đàm phán mức offer tốt nhất.

---

## 4. Checklist Trước Giờ Vào Phòng Phỏng Vấn (In Ra Hoặc Đọc Trước 30 Phút)

- [ ] **Mở sẵn trên máy (nếu phỏng vấn Online)**: Sơ đồ kiến trúc `shop-order-platform`, repo GitHub, trang Excalidraw trắng để sẵn sàng share màn hình vẽ trực tiếp khi được hỏi.
- [ ] **Nhẩm lại 3 con số của dự án FPT** và **3 con số của `shop-order-platform`** (p95 giảm bao nhiêu ms, bảng bao nhiêu triệu dòng, chịu tải bao nhiêu RPS, rollback mất bao nhiêu phút).
- [ ] **Nguyên tắc trả lời câu hỏi kỹ thuật**:
  1. **Định nghĩa trực diện trong 1 câu đầu tiên** (không vòng vo).
  2. **Giải thích cơ chế bên dưới (Under the hood)**.
  3. **Chốt bằng 1 ví dụ thực tế bạn đã làm trong dự án FPT hoặc `shop-order-platform`** (*"Trong dự án của em, em đã áp dụng cái này để giải quyết..."*).
- [ ] **Nguyên tắc khi gặp bài System Design / Tình huống**: Làm rõ yêu cầu → Ước lượng tải → Vẽ luồng chính → Chỉ ra Bottleneck & cách chống lỗi (Lock/Idempotency/Retry/DLQ) → **Luôn chốt bằng Observability (Metrics, Tracing, Logs, Alert)**.

