# Tuần 3 (23/11 – 29/11/2026): Concurrency + JVM

> Skill audit của bạn: Concurrency/JVM = 2 → mục tiêu 4. Đây là tuần nặng nhất tháng 1. Hiểu bản chất trước, API sau.

---

## Ngày 15 — Thứ 2 (23/11): Thread cơ bản
**Kiến thức phải nắm:**
- Process vs Thread; thread chia sẻ heap, riêng stack.
- Tạo thread: `extends Thread`, `Runnable`, `Callable` + `Future`; `start()` vs `run()`.
- **Vòng đời thread:** NEW → RUNNABLE → BLOCKED / WAITING / TIMED_WAITING → TERMINATED.
- `sleep` (giữ lock) vs `wait` (nhả lock, phải trong synchronized) vs `join` vs `yield`; `interrupt()` và cách xử lý `InterruptedException` đúng (khôi phục cờ interrupt).
- Daemon thread.

**Code tay:** in trạng thái thread qua các giai đoạn; viết task dừng được bằng interrupt.

**Tự kiểm tra:** `start()` vs `run()`? `sleep` vs `wait`? Nuốt InterruptedException thì sao?

---

## Ngày 16 — Thứ 3 (24/11): Race condition, synchronized, volatile, Java Memory Model
**Kiến thức phải nắm:**
- Race condition (`count++` không atomic: read-modify-write), critical section.
- `synchronized` method/block, lock trên object vs class (`static synchronized`), reentrant.
- **Java Memory Model**: mỗi thread có cache → vấn đề **visibility**, **reordering**; quan hệ **happens-before** (unlock→lock, volatile write→read, start, join).
- `volatile`: đảm bảo visibility + chặn reordering, **không** đảm bảo atomic.
- Atomic class (`AtomicInteger`, `LongAdder`) dựa trên **CAS**; vấn đề ABA.

**Code tay:** counter 2 thread ×1.000.000 lần: bản lỗi → synchronized → AtomicInteger → LongAdder, đo thời gian; demo cờ `running` không volatile khiến thread không dừng.

**Tự kiểm tra:** synchronized khác volatile? Happens-before là gì? CAS hoạt động thế nào?

**LeetCode (20'):** Climbing Stairs, Binary Search.

---

## Ngày 17 — Thứ 4 (25/11): Lock nâng cao + Deadlock + Concurrent utilities
**Kiến thức phải nắm:**
- `ReentrantLock` (tryLock có timeout, fair lock, lockInterruptibly) vs synchronized; `ReadWriteLock`; `StampedLock` (biết tên).
- `Condition` (await/signal).
- **Deadlock**: 4 điều kiện Coffman; cách phòng (thứ tự lock cố định, tryLock timeout); phát hiện bằng `jstack`/thread dump. Livelock, starvation.
- `CountDownLatch`, `CyclicBarrier`, `Semaphore`.
- `BlockingQueue` (`ArrayBlockingQueue`, `LinkedBlockingQueue`) → producer-consumer.
- `ThreadLocal` — dùng ở đâu (SecurityContext, MDC, transaction) và **memory leak với thread pool** nếu không `remove()`.

**Code tay:** tạo deadlock chuyển tiền 2 tài khoản → lấy thread dump bằng `jstack` → sửa bằng lock theo thứ tự id; producer-consumer bằng BlockingQueue; giới hạn 3 request đồng thời bằng Semaphore.

**Tự kiểm tra:** Deadlock là gì, phòng và phát hiện thế nào? ThreadLocal leak khi nào?

---

## Ngày 18 — Thứ 5 (26/11): ExecutorService, ThreadPool, CompletableFuture
**Kiến thức phải nắm:**
- Vì sao dùng thread pool; `ThreadPoolExecutor` 7 tham số: corePoolSize, maxPoolSize, keepAliveTime, unit, **workQueue**, threadFactory, **RejectedExecutionHandler** (4 policy).
- Luồng nhận task: core → queue → max → reject. Vì sao `Executors.newFixedThreadPool` (queue vô hạn) nguy hiểm OOM.
- Chọn kích thước pool: CPU-bound ≈ số core, I/O-bound lớn hơn.
- `shutdown` vs `shutdownNow`; `ScheduledExecutorService`.
- **CompletableFuture**: `supplyAsync`, `thenApply`, `thenCompose`, `thenCombine`, `allOf`, `exceptionally`/`handle`, timeout `orTimeout`; luôn truyền executor riêng thay vì common pool.
- Liên hệ Spring: `@Async` + cấu hình `ThreadPoolTaskExecutor`.

**Code tay:** giả lập trang chi tiết đơn hàng gọi 3 "service" (user, product, shipping) mỗi cái sleep 300ms: tuần tự (900ms) vs CompletableFuture song song (~300ms), có xử lý lỗi + timeout.

**Tự kiểm tra:** Giải thích 7 tham số ThreadPoolExecutor. Queue đầy thì sao? `thenApply` vs `thenCompose`?

**LeetCode (20'):** Maximum Subarray, Merge Intervals.

---

## Ngày 19 — Thứ 6 (27/11): Virtual Threads + JVM Memory
**Kiến thức phải nắm:**
- **Virtual Thread (Java 21)**: thread nhẹ do JVM quản lý, mount/unmount trên carrier thread; tốt cho I/O-bound, không làm CPU-bound nhanh hơn; **pinning** khi block trong `synchronized` (đã cải thiện ở Java 24); không pool virtual thread; Spring Boot `spring.threads.virtual.enabled=true`.
- **JVM runtime data areas:** Heap (Young: Eden + S0/S1, Old), **Metaspace** (thay PermGen từ Java 8), Stack, PC register, Native method stack.
- Vòng đời object: Eden → Survivor → Old (tuổi), Minor GC vs Major/Full GC.
- ClassLoader: Bootstrap → Platform → Application, **parent delegation**; quá trình load → link (verify, prepare, resolve) → init.

**Code tay:** chạy 10.000 task sleep 1s bằng platform thread pool 200 vs `Executors.newVirtualThreadPerTaskExecutor()`, so thời gian.

**Tự kiểm tra:** Virtual thread khác platform thread? Khi nào không có lợi? Heap chia vùng thế nào? Metaspace là gì?

---

## Ngày 20 — Thứ 7 (28/11): GC, Memory leak, công cụ chẩn đoán — Lab 4–5h
**Kiến thức phải nắm:**
- GC roots, mark-sweep-compact, generational hypothesis.
- Các GC: Serial, Parallel, **G1** (mặc định từ Java 9, chia region, mục tiêu pause time), **ZGC**/Shenandoah (pause rất thấp). Stop-the-world.
- Tham số: `-Xms`, `-Xmx`, `-Xss`, `-XX:+UseG1GC`, `-XX:+HeapDumpOnOutOfMemoryError`, container: `-XX:MaxRAMPercentage`.
- Các loại OOM: `Java heap space`, `Metaspace`, `unable to create native thread`, `GC overhead limit exceeded`; `StackOverflowError`.
- Memory leak hay gặp: static collection lớn dần, cache không giới hạn, ThreadLocal không remove, listener không gỡ, connection không đóng.
- Công cụ: `jps`, `jstat -gc`, `jstack`, `jmap -dump`, `jcmd`, VisualVM, Eclipse MAT, JFR.
- Strong/Soft/Weak/Phantom reference (biết khái niệm, `WeakHashMap`).

**Lab:**
- Viết app cố tình leak (static `Map` thêm mãi) chạy với `-Xmx128m -XX:+HeapDumpOnOutOfMemoryError` → mở heap dump bằng VisualVM/MAT → tìm thủ phạm → sửa.
- Bài tổng hợp tuần: **rate limiter token bucket thread-safe** + **cache thread-safe có TTL** + xử lý 10.000 request giả lập bằng ThreadPool, đo throughput.

**LeetCode (30'):** Linked List Cycle, Reverse Linked List, Number of Islands (BFS/DFS).

---

## Ngày 21 — Chủ nhật (29/11): Review + luyện miệng
**10 câu phỏng vấn của tuần:**
1. synchronized vs volatile vs Atomic.
2. Happens-before / visibility là gì?
3. Deadlock: điều kiện, phòng tránh, cách phát hiện trên production.
4. 7 tham số ThreadPoolExecutor, queue đầy thì sao?
5. CompletableFuture: kết hợp nhiều tác vụ và xử lý lỗi.
6. Virtual Thread là gì, khi nào dùng, pinning là gì?
7. Cấu trúc bộ nhớ JVM, Metaspace.
8. G1 GC hoạt động thế nào? Minor vs Full GC.
9. App bị OOM trên production, bạn xử lý từng bước thế nào?
10. ThreadLocal dùng ở đâu, leak khi nào?

**Checklist:** ☐ Lấy được thread dump & heap dump thật ☐ Demo CompletableFuture ☐ Rate limiter token bucket ☐ Chấm lại skill Concurrency

