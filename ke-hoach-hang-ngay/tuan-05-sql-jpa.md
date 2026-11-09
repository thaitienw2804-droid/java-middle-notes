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

**Tự kiểm tra:** N+1 fix mấy cách, ưu nhược? `REQUIRED` vs `REQUIRES_NEW`? Khi nào transaction không rollback?

---

## Ngày 34 — Thứ 7 (12/12): Lab 4–5h — data layer shop-order-platform
- Postgres thật (Docker Compose), Flyway, seed 100.000 sản phẩm + 1.000.000 order items.
- Chức năng: đặt hàng trừ kho chống oversell (optimistic lock + retry), danh sách đơn có filter (Specification) + keyset pagination.
- Viết **báo cáo tối ưu SQL** (`docs/sql-optimization.md`): 3 query chậm, EXPLAIN trước/sau, thời gian trước/sau.
- Connection pool **HikariCP**: `maximumPoolSize`, `connectionTimeout`; pool cạn thì triệu chứng gì.
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

