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

> **⚠️ 2 Bẫy `ThreadLocal` & Deadlock thực chiến:**
> 1. **`ThreadLocal` trên Tomcat Thread Pool gây 2 hậu quả**:
>    - **Cross-Request Data Leak (Nguy hiểm nhất)**: Tomcat tái sử dụng worker thread. Nếu Request 1 của User A lưu `TenantContext`/`UserContext` vào `ThreadLocal` mà không xóa, Request 2 của User B dùng lại đúng thread đó sẽ **đọc nhầm dữ liệu/quyền của User A**!
>    - **Memory Leak**: Mỗi `Thread` giữ `ThreadLocalMap` với Entry có key là `WeakReference<ThreadLocal>` nhưng **value là Strong Reference**. Khi thread sống mãi trong pool, value không bao giờ được GC thu hồi nếu không gọi **`threadLocal.remove()` trong khối `finally`** (hoặc `HandlerInterceptor.afterCompletion`).
> 2. **Quy tắc vàng chống Deadlock khi khóa nhiều tài nguyên (Chuyển tiền A ↔ B)**:
>    - Luôn sắp xếp thứ tự khóa cố định theo ID: `long first = Math.min(fromId, toId); long second = Math.max(fromId, toId);` rồi mới lock `first` → `second`.

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

> **⚠️ 3 Bẫy `ThreadPoolExecutor` & `CompletableFuture` hay rớt vòng kỹ thuật (MoMo / Bank / EPAM):**
> 1. **Bài toán đếm số thread chạy**: Cho `corePoolSize = 5`, `maxPoolSize = 10`, `queueCapacity = 100`. Có **15 task** gửi vào cùng lúc thì có mấy thread đang chạy?
>    - **Đáp án**: **Chỉ 5 thread chạy** (10 task còn lại nằm đợi trong `workQueue`!). Vì `ThreadPoolExecutor` chỉ tạo thêm thread mới vượt `corePoolSize` lên `maxPoolSize` **sau khi `workQueue` đã đầy 100% (100/100)**.
> 2. **Bẫy nuốt Exception của `submit()`**: Gọi `executor.execute(runnable)` nếu ném exception thì in ra stack trace; nhưng gọi `executor.submit(runnable)` thì exception bị gói kín bên trong `Future` — nếu không gọi `future.get()` thì **exception biến mất hoàn toàn trong im lặng**!
> 3. **Bẫy `CompletableFuture.supplyAsync()` không truyền Executor riêng**: Mặc định dùng chung `ForkJoinPool.commonPool()` (kích thước = `CPU cores - 1`). Nếu chạy tác vụ I/O blocking (gọi DB/HTTP) trên common pool, chỉ vài request sẽ làm nghẽn toàn bộ `parallelStream` và các `CompletableFuture` khác trên toàn JVM. **Luôn truyền custom `Executor` riêng (Bulkhead pattern)!**

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

> **⚠️ 3 Cạm bẫy Production của Java 21 Virtual Threads (Câu hỏi phân loại Middle/Senior):**
> 1. **CPU-bound không nhanh hơn**: Virtual Thread chỉ phát huy tác dụng khi gặp **Blocking I/O** (JVM tự unmount `Continuation` khỏi OS Carrier Thread để Carrier Thread chạy việc khác). Với tác vụ nặng CPU (mã hóa, nén, parse JSON khổng lồ), Virtual Thread **không nhanh hơn** mà còn tốn thêm chi phí lập lịch.
> 2. **Thread Pinning (`synchronized` trap)**: Trên Java 21 (trước JEP 491 ở Java 24), nếu Virtual Thread thực hiện blocking I/O bên trong khối **`synchronized`** hoặc gọi hàm `native`/JNI, nó bị **ghim chặt (pinned)** vào OS Carrier Thread, không unmount được. Nếu cả 8 Carrier Thread (trên máy 8 core) đều bị pinned chờ DB → toàn bộ ứng dụng treo cứng (sự cố thực tế của Netflix).
>    - **Cách phát hiện & sửa**: Bật `-Djdk.tracePinnedThreads=full` hoặc xem event `jdk.VirtualThreadPinned` trên JFR; thay `synchronized` quanh I/O bằng **`ReentrantLock`**.
> 3. **Tuyệt đối không Pool Virtual Thread — Dùng `Semaphore` để chống DDoS Downstream**:
>    - Mỗi task tạo mới 1 Virtual Thread. Nhưng vì có thể tạo 100.000 Virtual Thread cùng lúc, bạn mất đi "van chặn tự nhiên" của thread pool cũ → 100.000 Virtual Thread cùng bắn xuống DB hoặc 3rd-party API sẽ làm sập downstream ngay lập tức.
>    - **Cách sửa**: Dùng **`Semaphore`** (ví dụ `new Semaphore(50)`) hoặc **Resilience4j Bulkhead / RateLimiter** để giới hạn số lượng truy cập đồng thời vào tài nguyên hữu hạn.

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

