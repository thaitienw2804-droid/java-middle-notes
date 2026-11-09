# Tuần 5 (07/12 – 13/12/2026): SQL/Database sâu + JPA/Hibernate

> Skill audit: JPA/SQL = 4 (điểm mạnh nhất của bạn — tối ưu 10+ query ở FPT). Tuần này biến kinh nghiệm thành **lý thuyết nói được + số liệu đo được** để kể trong phỏng vấn.

---

## Ngày 29 — Thứ 2 (07/12): Thiết kế DB, ACID, SQL nâng cao
**Kiến thức phải nắm:**
- Chuẩn hóa 1NF/2NF/3NF, khi nào denormalize; khóa chính (surrogate vs natural key, UUID vs BIGSERIAL — ảnh hưởng index), khóa ngoại, constraint (UNIQUE, CHECK, NOT NULL).
- **ACID** — mỗi chữ 1 ví dụ trong đơn hàng.
- JOIN (INNER/LEFT/RIGHT/FULL/self join), `GROUP BY` + `HAVING`, subquery vs CTE (`WITH`), `EXISTS` vs `IN`, `UNION` vs `UNION ALL`.
- **Window function**: `ROW_NUMBER`, `RANK`, `DENSE_RANK`, `LAG/LEAD`, `SUM() OVER (PARTITION BY ...)`.
- Thứ tự thực thi logic: FROM → WHERE → GROUP BY → HAVING → SELECT → ORDER BY → LIMIT.

**Code tay:** thiết kế schema `users, products, inventory, orders, order_items, payments`; viết 10 query: top 3 sản phẩm mỗi danh mục, doanh thu lũy kế theo ngày, khách hàng chưa từng mua, đơn hàng thứ 2 của mỗi khách, …

**Tự kiểm tra:** WHERE vs HAVING? Lấy top N mỗi nhóm thế nào? UUID làm khóa chính có vấn đề gì?

---

## Ngày 30 — Thứ 3 (08/12): Index và EXPLAIN ANALYZE
**Kiến thức phải nắm:**
- **B-Tree index** hoạt động thế nào; Hash, GIN (JSONB/full-text), BRIN (biết tên).
- Composite index và **leftmost prefix**; thứ tự cột; covering index (`INCLUDE`) / index-only scan; partial index.
- Khi nào index **không được dùng**: hàm trên cột (`LOWER(email)`), `LIKE '%abc'`, ép kiểu ngầm, selectivity thấp, `OR`.
- Index làm chậm ghi, tốn dung lượng; clustered vs non-clustered (MySQL InnoDB vs Postgres heap).
- **EXPLAIN ANALYZE**: Seq Scan, Index Scan, Index Only Scan, Bitmap Heap Scan, Nested Loop / Hash Join / Merge Join, cost vs actual time, rows ước lượng sai → `ANALYZE`.
- Phân trang: OFFSET lớn chậm vì sao → **keyset/seek pagination**.

**Code tay:** seed 1.000.000 dòng `orders` bằng `generate_series`; đo 3 query chậm trước/sau khi thêm index; so OFFSET 900000 vs keyset. Ghi bảng số liệu → dùng làm câu chuyện phỏng vấn.

> **⚠️ 2 Bẫy Index & Bảng 1 Tỷ Dòng cực hay hỏi ở Bank / Big Tech (MB, Viettel, TCB, Grab):**
> 1. **Vì sao dùng `UUIDv4` ngẫu nhiên làm Primary Key lại giết chết hiệu năng Insert & làm phình Index?**
>    - **Kích thước**: `UUID` chuỗi 36 bytes (hoặc 16 bytes binary) lớn hơn nhiều so với `BIGINT` (8 bytes). Trong MySQL InnoDB, mọi **Secondary Index đều lưu kèm giá trị Primary Key ở node lá (leaf node)** để làm Bookmark Lookup → PK to làm mọi Secondary Index phình to, giảm số key trên mỗi trang 16KB (fanout thấp → cây B+ Tree cao hơn → tốn I/O).
>    - **Random Page Split & Fragmentation**: `BIGSERIAL` / `AUTO_INCREMENT` luôn chèn tuần tự vào cuối trang bên phải của B+ Tree. Ngược lại `UUIDv4` sinh ngẫu nhiên nên chèn vào giữa các trang đã đầy → bắt DB phải tách trang (Page Split), di chuyển bản ghi liên tục và gây phân mảnh đĩa nặng nề. **Giải pháp**: dùng `BIGINT`/`BIGSERIAL`, **Snowflake ID**, hoặc **`UUIDv7` (TSID — có thứ tự thời gian ở đầu)**.
> 2. **Kịch bản thực chiến: "Bảng 500 triệu – 1 tỷ dòng đã đánh Index trên `created_at` và `status` nhưng query lọc 6 tháng gần nhất vẫn chạy 30 giây — xử lý thế nào?"**
>    - **Đừng vội hô Sharding ngay!** Phải trả lời theo thứ tự chẩn đoán của kỹ sư thực chiến:
>      1. **Chạy `EXPLAIN (ANALYZE, BUFFERS)`**: Kiểm tra xem DB đang chạy `Index Scan` hay đã tự chuyển sang `Seq Scan` (Full Table Scan).
>      2. **Kiểm tra độ chọn lọc (Selectivity / Cardinality)**: Nếu lọc 6 tháng trên bảng lưu 1 năm (~50% số dòng), đọc qua Secondary Index rồi nhảy ngẫu nhiên vào bảng chính (Random Heap/Clustered Lookup) tốn I/O hơn đọc tuần tự, nên Query Optimizer cố tình bỏ Index!
>      3. **Thứ tự cột trong Composite Index**: Đặt cột so sánh bằng (`status = 'COMPLETED'`) trước cột khoảng (`created_at >= ...`), kết hợp **Covering Index (`INCLUDE (total_amount, customer_id)`)** để đạt `Index Only Scan` (không cần quay về đọc bảng).
>      4. **Đổi phân trang `OFFSET` lớn sang Keyset/Seek Pagination** (`WHERE (created_at, id) < (:lastCreatedAt, :lastId) ORDER BY created_at DESC, id DESC LIMIT 20`).
>      5. **Range Partitioning theo Tháng/Quý (`PARTITION BY RANGE (created_at)`) + Archiving**: Giúp DB thực hiện **Partition Pruning** (chỉ quét đúng phân vùng tháng cần tìm) và cho phép `DETACH / DROP PARTITION` dữ liệu cũ tức thì mà không gây bloat hay treo bảng như lệnh `DELETE` hàng chục triệu dòng.

**Tự kiểm tra:** Composite index (a,b,c), query `WHERE b=?` có dùng index không? Vì sao OFFSET lớn chậm? Đọc 1 execution plan.

**LeetCode/SQL (20'):** LeetCode SQL 50: Second Highest Salary, Department Top Three Salaries.

---

## Ngày 31 — Thứ 4 (09/12): Transaction Isolation, MVCC, Locking
**Kiến thức phải nắm:**
- Hiện tượng: dirty read, non-repeatable read, phantom read, lost update, write skew.
- 4 mức isolation; **mặc định Postgres = Read Committed**, MySQL InnoDB = Repeatable Read.
- **MVCC** trong Postgres (xmin/xmax, snapshot), VACUUM vì sao cần.
- Lock: row lock `SELECT ... FOR UPDATE`, `FOR UPDATE SKIP LOCKED` (làm queue), `NOWAIT`; table lock; advisory lock.
- **Pessimistic vs Optimistic locking** (`version` column) — khi nào dùng cái nào (trừ kho!).
- Deadlock trong DB: ví dụ, Postgres tự phát hiện và kill 1 transaction, cách phòng (thứ tự update cố định, transaction ngắn).
- Long transaction gây hại gì.

**Code tay:** 2 cửa sổ psql tái hiện lost update khi trừ kho → sửa bằng `FOR UPDATE` → sửa bằng `UPDATE inventory SET qty = qty - ? WHERE id=? AND qty >= ?` → sửa bằng optimistic version. Tái hiện deadlock.

> **⚠️ Chi tiết Locking & MVCC phân biệt ứng viên Middle cứng:**
> - **Lỗi TOCTOU (Time-Of-Check to Time-Of-Use)**: Đọc số dư/tồn kho lên biến Java (`if (stock >= qty)`), tính toán rồi mới `save()` dưới mức `READ COMMITTED` sẽ bị 2 luồng cùng đọc thấy đủ hàng và trừ 2 lần (âm kho).
> - **So sánh 3 cách chống Oversell / Trừ tiền đồng thời**:
>   1. **Atomic SQL Update** (`UPDATE inventory SET qty = qty - :n WHERE id = :id AND qty >= :n`): Nhanh nhất, gọn nhất, chỉ giữ row lock đúng lúc chạy lệnh `UPDATE`, kiểm tra `updatedRows == 1`.
>   2. **Optimistic Lock (`@Version`)**: Không giữ lock khi đọc (`UPDATE ... SET version = version + 1 WHERE id = ? AND version = ?`). Phù hợp khi **đọc nhiều, xung đột ghi ít**; nếu `updatedRows == 0` thì ném `OptimisticLockingFailureException` (phải có cơ chế retry hoặc báo thử lại).
>   3. **Pessimistic Lock (`SELECT ... FOR UPDATE`)**: Khóa dòng ngay từ lúc đọc đến hết transaction. Dùng cho **chuyển tiền/tài chính xung đột cao** không muốn retry nhiều lần. *Lưu ý bẫy*: Nếu cột trong `WHERE` của `SELECT ... FOR UPDATE` **không có index**, DB (như MySQL InnoDB) sẽ khóa toàn bộ bảng (Record Lock + Gap Lock = Next-Key Lock trên mọi dòng)!

**Tự kiểm tra:** 4 mức isolation và hiện tượng mỗi mức chặn? MVCC là gì? Trừ kho chống bán vượt (oversell) thế nào?

---

## Ngày 32 — Thứ 5 (10/12): JPA/Hibernate cốt lõi
**Kiến thức phải nắm:**
- JPA (spec) vs Hibernate (impl) vs Spring Data JPA.
- **Entity lifecycle**: Transient → Managed → Detached → Removed; `persist` vs `merge`; **Persistence Context = first-level cache**, dirty checking, flush (khi nào flush: commit, trước query JPQL…).
- Mapping: `@OneToMany`/`@ManyToOne` (owning side, `mappedBy`), `@ManyToMany` (nên thay bằng entity trung gian), cascade, `orphanRemoval`.
- **Lazy vs Eager**: mặc định `@ManyToOne` EAGER (nên đổi LAZY), `@OneToMany` LAZY; `LazyInitializationException`; vì sao `spring.jpa.open-in-view` nên tắt.
- ID generation: IDENTITY (tắt batch insert) vs SEQUENCE (allocationSize).
- Second-level cache (biết khái niệm).

**Code tay (shop-order-platform):** map Entity Order–OrderItem–Product chuẩn, bật log SQL (`show-sql` + `p6spy` hoặc `logging.level.org.hibernate.SQL`), tắt open-in-view, tái hiện LazyInitializationException.

> **⚠️ 2 Bẫy Hibernate Internals làm rớt 80% ứng viên (EPAM / KMS / Bank):**
> 1. **Phân biệt rõ 4 tầng khi gọi `userRepository.findByEmail(...)`**:
>    - **Spring Data JPA**: Tạo **JDK Dynamic Proxy** bọc lấy interface `UserRepository` (cài đặt mặc định là `SimpleJpaRepository`).
>    - **JPA (`jakarta.persistence`)**: Chỉ là bộ **Đặc tả (Specification)** gồm các interface (`EntityManager`) và annotation (`@Entity`), không có code thực thi.
>    - **Hibernate ORM**: **JPA Provider (Implementation)** thực sự — quản lý `SessionImpl`, `PersistenceContext` (L1 Cache), Dirty Checking, `ActionQueue` và sinh câu lệnh SQL.
>    - **JDBC + HikariCP**: Quản lý kết nối TCP vật lý và thực thi `PreparedStatement` xuống DB.
> 2. **Bẫy thứ tự `ActionQueue` khi gọi `delete()` rồi `save()` trong cùng `@Transactional`**:
>    - Giả sử bảng `users` có cột `email UNIQUE`. Trong 1 hàm `@Transactional`, bạn gọi `repo.delete(oldUser)` rồi gọi ngay `repo.save(new User(sameEmail))` → **Nổ lỗi `DataIntegrityViolationException (Duplicate key)`!**
>    - **Nguyên nhân**: Hibernate không bắn SQL ngay khi gọi hàm mà xếp vào `ActionQueue` và khi `flush()` nó luôn thực thi theo thứ tự cứng: **`EntityInsertAction` (INSERT chạy số 1) → `EntityUpdateAction` → ... → `EntityDeleteAction` (DELETE chạy cuối cùng!)**.
>    - **Cách sửa**: Gọi `repo.flush()` (hoặc `entityManager.flush()`) ngay sau dòng `repo.delete(oldUser)`. *Câu hỏi phụ*: `flush()` có commit transaction không? **KHÔNG**, `flush()` chỉ đẩy SQL xuống DB trên transaction đang mở; nếu sau đó hàm ném exception thì cả `DELETE` lẫn `INSERT` vẫn rollback bình thường!

**Tự kiểm tra:** Entity lifecycle? Dirty checking là gì? `persist` vs `merge`? Vì sao tắt open-in-view?

**LeetCode (20'):** Course Schedule (graph), Validate BST.

---

## Ngày 33 — Thứ 6 (11/12): N+1, Transaction trong Spring, Flyway
**Kiến thức phải nắm:**
- **N+1**: nguyên nhân; fix bằng `JOIN FETCH`, `@EntityGraph`, `@BatchSize`/`default_batch_fetch_size`, DTO projection; vấn đề fetch join + phân trang (`HHH90003004` — phân trang trong RAM) và cách tách 2 query.
- Projection (interface/record), `@Query` JPQL vs native, Specification/Criteria cho filter động; `Pageable` vs `Slice`.
- Batch insert/update (`hibernate.jdbc.batch_size`), `saveAll` không tự batch nếu IDENTITY.
- **@Transactional**: propagation (`REQUIRED`, `REQUIRES_NEW`, `NESTED`, `SUPPORTS`, `MANDATORY`, `NEVER`), isolation, `readOnly = true`, timeout, rollbackFor; transaction gọi API ngoài (không nên giữ transaction khi gọi HTTP).
- **Flyway**: `V1__init.sql`, checksum, không sửa migration đã chạy, `baseline`, migration backward-compatible (expand → migrate → contract).

**Code tay:** API danh sách đơn hàng bị N+1 → đo số query → fix 3 cách → bảng so sánh; chuyển schema sang Flyway; ví dụ `REQUIRES_NEW` cho audit log không bị rollback theo.

> **⚠️ 3 Bẫy N+1, Batch Insert & `@Transactional` giữ Connection (Cực kỳ quan trọng):**
> 1. **Vì sao đổi `@OneToMany` sang `FetchType.EAGER` là "Red Flag" khi phỏng vấn? Và lỗi `MultipleBagFetchException` / `HHH90003004`:**
>    - Đổi sang `EAGER` gây bùng nổ tích Đề-các (Cartesian Product), luôn kéo dữ liệu thừa, và khi fetch cùng lúc 2 `List` sẽ nổ **`MultipleBagFetchException: cannot simultaneously fetch multiple bags`** (sửa bằng đổi sang `Set` hoặc tách 2 query trong cùng `@Transactional(readOnly = true)`).
>    - Nếu dùng `JOIN FETCH` trên quan hệ `@OneToMany` đi kèm `Pageable` (`Page<Order>`), Hibernate cảnh báo **`HHH90003004: firstResult/maxResults specified with collection fetch; applying in memory!`** — nó kéo **toàn bộ bảng lên RAM JVM** rồi mới cắt trang → **nổ OOM**!
>    - **Cách sửa chuẩn với phân trang**: Dùng **`@BatchSize(size = 50)`** (hoặc `hibernate.default_batch_fetch_size: 50` để gom N query con thành `WHERE order_id IN (?, ?, ...)`), hoặc **Query 1 lấy danh sách ID có phân trang → Query 2 `JOIN FETCH WHERE id IN (:ids)`**, hoặc dùng **DTO Projection** (nhanh nhất cho API chỉ đọc vì bỏ qua L1 Cache & Dirty Checking).
> 2. **Vì sao `saveAll(10_000_items)` mặc định vẫn bắn 10.000 lệnh INSERT lẻ?**
>    - Nếu Entity dùng `@GeneratedValue(strategy = GenerationType.IDENTITY)`, **Hibernate tự động tắt hoàn toàn JDBC Batch Insert** vì phải chạy từng lệnh INSERT mới lấy được ID sinh ra!
>    - Muốn Batch Insert thật sự: (1) Đổi sang `GenerationType.SEQUENCE` (`allocationSize = 50`), (2) Cấu hình `hibernate.jdbc.batch_size: 50`, `order_inserts: true`, `order_updates: true`, và thêm `reWriteBatchedInserts=true` vào JDBC URL Postgres, (3) Với lô lớn hàng chục nghìn dòng, cứ mỗi 500 dòng phải gọi `entityManager.flush(); entityManager.clear();` để giải phóng L1 Persistence Context, tránh nổ Heap OOM và tránh Dirty Checking $O(n^2)$!
> 3. **Thảm họa gọi HTTP / 3rd-party API bên trong `@Transactional` (HikariCP Starvation)**:
>    - Giữ `@Transactional` trong khi gọi API thanh toán/gửi mail mất 3 giây sẽ **ngâm cứng kết nối DB (và cả row lock)** suốt 3 giây đó. Với `maximumPoolSize = 10`, chỉ cần **10 request đồng thời** là cạn sạch HikariCP pool → toàn bộ hệ thống (kể cả API login/xem hàng) chết đứng với lỗi `Connection is not available, request timed out after 30000ms`!
>    - **Cách sửa**: Tuyệt đối không gọi I/O mạng ngoài trong `@Transactional`. Dùng **`TransactionTemplate`** tách làm 3 bước: (Tx 1: Lưu đơn `PENDING` rồi commit ngay để trả connection) → (Ngoài Tx: Gọi HTTP API bên thứ 3) → (Tx 2: Cập nhật trạng thái đơn `CONFIRMED`/`FAILED` rồi commit).
>    - **Mẹo tối ưu**: Luôn gắn `@Transactional(readOnly = true)` cho các hàm đọc để Hibernate bỏ tạo snapshot và bỏ Dirty Checking (tăng throughput đọc 20–40%).

**Tự kiểm tra:** N+1 fix mấy cách, ưu nhược? `REQUIRED` vs `REQUIRES_NEW`? Khi nào transaction không rollback?

---

## Ngày 34 — Thứ 7 (12/12): Lab 4–5h — data layer shop-order-platform
- Postgres thật (Docker Compose), Flyway, seed 100.000 sản phẩm + 1.000.000 order items.
- Chức năng: đặt hàng trừ kho chống oversell (optimistic lock + retry), danh sách đơn có filter (Specification) + keyset pagination.
- Viết **báo cáo tối ưu SQL** (`docs/sql-optimization.md`): 3 query chậm, EXPLAIN trước/sau, thời gian trước/sau.
- Connection pool **HikariCP**: `maximumPoolSize` (công thức chuẩn PostgreSQL: `Pool Size = (Core Count * 2) + Effective Spindle Count`, thường **20–50 kết nối/instance**, lưu ý nhân với số pod K8s phải nhỏ hơn `max_connections` của DB — đừng bao giờ trả lời "tăng pool lên 200 theo số thread Tomcat"!), `connectionTimeout` (hạ từ 30s xuống 3–5s để fail-fast), `leakDetectionThreshold: 60000`; pool cạn thì triệu chứng gì.
- **Đóng gói câu chuyện FPT #2 (45' — Vũ khí mạnh nhất của bạn)**: Chọn ra **3 câu chuyện tối ưu SQL thật trong số 10+ query ở FPT** và ghi rõ vào `docs/fpt-sql-stories.md`: (1) Bài toán nghiệp vụ & quy mô bảng (bao nhiêu triệu dòng?), (2) Nguyên nhân chậm trên `EXPLAIN ANALYZE` (Seq Scan, thiếu composite index, hàm trên cột, JOIN nổ dòng, hay N+1?), (3) Cách bạn sửa, (4) Con số đo đạc trước/sau (VD: từ 4.5s xuống 120ms, giảm CPU DB từ 85% xuống 25%).
- (Mở rộng 1h) **NoSQL nhập môn**: MongoDB document model, khi nào chọn Mongo thay Postgres; Elasticsearch cho full-text search — chỉ cần nói được trade-off.

**LeetCode (30'):** SQL: Rank Scores, Consecutive Numbers; Algo: Top K Frequent Words.

---

## Ngày 35 — Chủ nhật (13/12): Review + luyện miệng
**10 câu phỏng vấn của tuần:**
1. ACID; 4 mức isolation; Postgres mặc định mức nào?
2. Index B-Tree, composite index, khi nào index không được dùng?
3. Đọc EXPLAIN ANALYZE — kể 1 lần bạn tối ưu query có số liệu.
4. Optimistic vs pessimistic lock — chọn gì để trừ kho?
5. Deadlock trong DB xử lý thế nào?
6. Entity lifecycle, persistence context, dirty checking.
7. N+1 là gì, fix bằng mấy cách?
8. Transaction propagation, `REQUIRES_NEW` dùng khi nào?
9. Phân trang dữ liệu lớn thế nào?
10. SQL vs NoSQL, khi nào dùng MongoDB?

**Checklist:** ☐ Báo cáo tối ưu SQL có số liệu ☐ Tái hiện lost update + deadlock ☐ Flyway ☐ Chấm lại skill

