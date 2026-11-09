# Tuần 2 (16/11 – 22/11/2026): Collections, Generics, Functional, Java 17/21, Design Patterns

> Mục tiêu tuần: hiểu **bên trong** HashMap/ArrayList (câu hỏi gần như chắc chắn có), viết Stream thành thạo, biết các tính năng Java mới và 7 design pattern hay hỏi.

---

## Ngày 8 — Thứ 2 (16/11): List, Set, Queue + Big-O
**Kiến thức phải nắm:**
- Sơ đồ Collection Framework: `Collection` → `List` / `Set` / `Queue`/`Deque`; `Map` tách riêng.
- `ArrayList` (mảng động, grow ×1.5, truy cập O(1), chèn giữa O(n)) vs `LinkedList` (node đôi, thực tế ít khi nhanh hơn vì cache locality).
- `HashSet` (bọc HashMap), `LinkedHashSet` (giữ thứ tự chèn), `TreeSet` (Red-Black tree, O(log n), sorted).
- `ArrayDeque` (dùng làm stack/queue thay `Stack`), `PriorityQueue` (heap).
- Fail-fast iterator + `ConcurrentModificationException`; xóa phần tử khi duyệt: `Iterator.remove()` / `removeIf`.
- Immutable collection: `List.of()` vs `Collections.unmodifiableList()` vs `Arrays.asList()` (fixed-size, ghi được).
- Big-O cơ bản của các thao tác trên.

**Code tay:** tái hiện `ConcurrentModificationException` rồi sửa 3 cách; tự viết `MyArrayList<T>` có `add/get/remove` + grow.

**Tự kiểm tra:** ArrayList vs LinkedList khi nào dùng? `List.of` khác `Arrays.asList`? Vì sao xóa trong for-each lỗi?

**LeetCode (20'):** Best Time to Buy and Sell Stock, Valid Palindrome.

---

## Ngày 9 — Thứ 3 (17/11): HashMap từ bên trong (trọng tâm phỏng vấn)
**Kiến thức phải nắm:**
- Cấu trúc: mảng `Node[]` (bucket), `hash = h ^ (h >>> 16)`, index = `(n-1) & hash` (vì sao capacity là lũy thừa 2).
- Collision: linked list → **treeify thành Red-Black tree** khi bucket ≥ 8 phần tử và capacity ≥ 64 (Java 8).
- Load factor 0.75, resize ×2, rehash; khởi tạo capacity hợp lý để tránh resize.
- Key `null` (HashMap cho 1 key null, Hashtable/ConcurrentHashMap không cho).
- `LinkedHashMap` (access-order → tự làm **LRU cache** với `removeEldestEntry`), `TreeMap` (sorted, `floorKey`, `ceilingKey`).
- HashMap không thread-safe: mất dữ liệu khi put đồng thời (Java 7 còn bị vòng lặp vô hạn khi resize).
- `ConcurrentHashMap` Java 8: CAS + synchronized trên từng bucket (không còn Segment), `computeIfAbsent`, `merge`.

**Code tay:** viết LRU Cache bằng LinkedHashMap; đếm tần suất từ bằng `merge`; demo 2 thread put vào HashMap bị thiếu phần tử vs ConcurrentHashMap.

**Tự kiểm tra:** Mô tả `put()` của HashMap từng bước. Vì sao load factor 0.75? ConcurrentHashMap Java 7 vs 8?

---

## Ngày 10 — Thứ 4 (18/11): Generics
**Kiến thức phải nắm:**
- Generic class/method/interface, bounded type `<T extends Number>`.
- Wildcard `?`, `? extends T` (đọc — producer), `? super T` (ghi — consumer) → **PECS**.
- **Type erasure**: generic chỉ tồn tại lúc compile; hệ quả: không `new T()`, không `instanceof List<String>`, không tạo mảng generic.
- Raw type và vì sao tránh.
- Ví dụ thực tế: `ApiResponse<T>`, `PageResponse<T>`, `Repository<T, ID>`.

**Code tay:** viết `ApiResponse<T>` + `PageResponse<T>`; method `copy(List<? super T> dest, List<? extends T> src)`; method generic `max(Collection<? extends T>, Comparator<? super T>)`.

**Tự kiểm tra:** Type erasure là gì, hệ quả? PECS giải thích bằng ví dụ? `List<Object>` có nhận `List<String>` không?

**LeetCode (20'):** Group Anagrams, Top K Frequent Elements.

---

## Ngày 11 — Thứ 5 (19/11): Lambda, Functional Interface, Stream, Optional
**Kiến thức phải nắm:**
- Functional interface: `Function`, `Predicate`, `Consumer`, `Supplier`, `BiFunction`, `UnaryOperator`; `@FunctionalInterface`; method reference 4 loại.
- Lambda chỉ dùng biến effectively final — vì sao.
- Stream: intermediate (lazy: `filter`, `map`, `flatMap`, `sorted`, `distinct`) vs terminal (`collect`, `reduce`, `forEach`, `count`); stream chỉ dùng 1 lần.
- `Collectors`: `toList`, `toMap` (lỗi duplicate key → merge function), `groupingBy` + downstream (`counting`, `summingInt`, `mapping`), `partitioningBy`, `joining`.
- `parallelStream` — khi nào hại (task nhỏ, I/O, dùng chung ForkJoinPool).
- `Optional`: `map`, `flatMap`, `orElse` vs `orElseGet` (orElse luôn chạy!), `orElseThrow`; không dùng Optional làm field/param.

**Code tay:** từ list 1.000 Order giả: doanh thu theo tháng, top 5 khách chi nhiều nhất, nhóm theo status, sản phẩm bán chạy (flatMap qua OrderItem).

**Tự kiểm tra:** `map` vs `flatMap`? `orElse` vs `orElseGet`? Stream lazy nghĩa là gì? Khi nào không nên dùng parallelStream?

---

## Ngày 12 — Thứ 6 (20/11): Java 17/21 + Design Patterns phần 1
**Kiến thức phải nắm (Java mới):**
- `var` (Java 10), Switch expression (14), Text block (15), **Record** (16 — immutable data carrier, dùng làm DTO), **Sealed class** (17), **Pattern matching** `instanceof` (16) và cho `switch` (21), Record pattern (21), Sequenced Collections (21), **Virtual Threads** (21 — học kỹ tuần 3).
- Vì sao doanh nghiệp chọn LTS: 8 → 11 → 17 → 21 (→ 25).

**Design patterns phần 1 (Creational):**
- **Singleton** (eager, lazy, double-checked locking + `volatile`, enum singleton, Bean Spring là singleton theo container).
- **Factory Method / Simple Factory** (tạo PaymentProcessor theo type).
- **Builder** (Lombok `@Builder`, object nhiều field optional).

**Code tay:** chuyển DTO tuần 1 sang Record; `sealed interface PaymentResult permits Success, Failed, Pending` + switch pattern matching; viết Singleton double-checked locking và giải thích vì sao cần volatile.

**Tự kiểm tra:** Record khác class thường/Lombok `@Data` thế nào? Sealed class giải quyết vấn đề gì? Viết Singleton thread-safe.

---

## Ngày 13 — Thứ 7 (21/11): Design Patterns phần 2 + Lab 4–5h
**Kiến thức phải nắm (Structural + Behavioral hay hỏi nhất):**
- **Strategy** (giảm giá, phí ship), **Observer** (event — tiền thân của Spring `ApplicationEvent`/Kafka), **Template Method** (`JdbcTemplate`), **Proxy** (nền của Spring AOP/@Transactional — JDK dynamic proxy vs CGLIB), **Decorator** (`BufferedInputStream`), **Adapter** (tích hợp cổng thanh toán bên thứ ba), **Chain of Responsibility** (Servlet Filter, Spring Security filter chain).
- Với mỗi pattern: bài toán → cấu trúc → ví dụ trong JDK/Spring.

**Lab:** nâng cấp Mini Order Engine tuần 1:
- Record cho DTO, sealed `PaymentResult`, Stream cho báo cáo.
- Strategy (giảm giá) + Factory (payment) + Observer (OrderCreated → gửi mail/log) + Builder (Order) + Chain of Responsibility (validate đơn: tồn kho → giới hạn số lượng → địa chỉ).
- Viết JDK dynamic proxy đơn giản log thời gian chạy method (hiểu trước cách Spring AOP làm).

**LeetCode (30'):** Product of Array Except Self, Longest Substring Without Repeating Characters.

---

## Ngày 14 — Chủ nhật (22/11): Review + luyện miệng
- Note `tuan-02-collections-patterns.md`; vẽ sơ đồ HashMap `put()` bằng tay.
- Ghi âm 10 câu dưới.

**10 câu phỏng vấn của tuần:**
1. HashMap hoạt động thế nào, khi trùng hash thì sao? Vì sao treeify?
2. HashMap vs Hashtable vs ConcurrentHashMap.
3. ArrayList vs LinkedList.
4. Fail-fast vs fail-safe iterator.
5. Type erasure, PECS.
6. Stream `map` vs `flatMap`, lazy evaluation.
7. Optional dùng đúng thế nào?
8. Record, sealed class, pattern matching — dùng ở đâu trong dự án?
9. Singleton thread-safe, vì sao cần volatile?
10. Proxy pattern và liên quan gì tới Spring?

**Checklist:** ☐ Vẽ được HashMap ☐ LRU cache tự viết ☐ 7 pattern có code ☐ 6 bài LeetCode

