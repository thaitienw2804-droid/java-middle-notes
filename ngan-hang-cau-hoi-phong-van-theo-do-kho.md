# Ngân Hàng Câu Hỏi Phỏng Vấn Java Middle Backend (2–4 Năm Kinh Nghiệm) Theo Độ Khó Tăng Dần

> **Nguồn đối chiếu thực tế (11/2026)**: Viblo.asia (*Backend Interview Series*, *Spring Boot/JPA Internals*, *Kafka Production Lessons*), ITviec, TopDev, và Review phỏng vấn thực tế tại Việt Nam:
> - **Ngân hàng & Fintech VN**: MB Bank, Viettel (Telecom / High Tech / Software / Viettel Money), Techcombank (TCB), VPBank, MoMo, VNPAY.
> - **Ngân hàng Quốc tế & Tech Hub**: NAB, ANZ, TymeX.
> - **Global Outsourcing / Consulting**: EPAM Systems, KMS Technology, NashTech, FPT Software.
> - **Big Tech / Product**: Grab, Shopee, Zalo, Naver, Axon.

---

## Cấu Trúc 4 Cấp Độ Khó (Cách Dùng Để Ôn Tập)

| Cấp độ | Mục đích trong buổi phỏng vấn | Chỉ tiêu cần đạt trước khi đi thi |
|---|---|---|
| **Level 1 — Khởi Động & Nền Tảng Cốt Lõi (Easy–Medium)** | Sàng lọc 10–15 phút đầu. Trả lời sai hoặc ấp úng ở mức này thường bị đánh trượt ngay vòng gửi xe. | Trả lời **100%**, phản xạ trong **30–60 giây/câu**, không cần suy nghĩ lâu. |
| **Level 2 — Đào Sâu Cơ Chế Bên Trong (Medium / Core Middle)** | Phân biệt Junior (chỉ biết gọi API) và Middle (hiểu *How it works under the hood*): JVM, HashMap, ThreadPool, Spring Proxy, JPA Lifecycle, B+ Tree, Network/DNS, CI/CD. | Trả lời trôi chảy **≥ 85%**, vẽ được sơ đồ luồng trong **2 phút/câu**. |
| **Level 3 — Bẫy Thực Chiến & Sự Cố Production (Medium–Hard)** | Các "cạm bẫy" làm rớt 70–80% ứng viên: `delete()+save()` nổ DuplicateKey, N+1 + `Pageable` nổ OOM, HikariCP Starvation, Deadlock, Virtual Threads Pinning, Cache Stampede, Kafka Rebalance Storm. | Nắm chắc **nguyên nhân gốc rễ + cách tái hiện + cách fix chuẩn**. |
| **Level 4 — Thiết Kế Hệ Thống, Tải Cao & Xử Lý Sự Cố (Hard)** | Chốt deal Middle cứng / Senior chớm: Idempotency 3 lớp, Flash Sale 100k user, Outbox + Saga, Đối soát thanh toán, Bảng 1 tỷ dòng, Chẩn đoán API chậm p99 trong 10 phút. | Trình bày theo khung cấu trúc rõ ràng trong **5–10 phút**, luôn có **Trade-off + Observability**. |

---

# 🟢 LEVEL 1: Khởi Động & Nền Tảng Cốt Lõi (Warm-up / Easy–Medium)

### 1.1. Java Core & OOP / SOLID

#### Câu 1: Phân biệt `==` và `.equals()`? Tại sao khi override `equals()` bắt buộc phải override luôn `hashCode()`?
- **Đáp án chuẩn**:
  - `==` so sánh **giá trị** đối với kiểu nguyên thủy (`primitive`) và so sánh **địa chỉ ô nhớ (reference)** đối với `Object`.
  - `.equals()` so sánh **nội dung logic** của 2 đối tượng (mặc định trong class `Object` thì `equals` vẫn dùng `==` nếu chưa override).
  - **Hợp đồng `equals` & `hashCode`**: Trong `HashMap` / `HashSet`, Java dùng `hashCode()` để xác định vị trí **bucket** trước, sau đó mới dùng `equals()` để so sánh các phần tử nằm trong bucket đó.
  - Nếu override `equals()` mà quên override `hashCode()`: hai object bằng nhau về mặt logic (`a.equals(b) == true`) sẽ có 2 mã hash khác nhau (tính theo địa chỉ bộ nhớ) → rơi vào 2 bucket khác nhau → `map.get()` trả về `null` hoặc `HashSet` lưu trùng phần tử.

#### Câu 2: Tại sao `String` trong Java lại là Immutable? Phân biệt `String`, `StringBuilder` và `StringBuffer`?
- **Đáp án chuẩn**:
  - `String` là Immutable vì 4 lý do:
    1. **String Pool**: Nhiều biến có thể trỏ chung vào 1 chuỗi `"abc"` trong Heap mà không sợ biến này sửa làm hỏng biến kia.
    2. **Bảo mật (Security)**: Chuỗi thường dùng làm URL DB, username/password, tên class trong `ClassLoader` — nếu mutable có thể bị thay đổi sau khi đã qua bước kiểm tra quyền.
    3. **Thread-safe tự nhiên**: Nhiều luồng đọc chung không cần đồng bộ.
    4. **Hashcode Caching**: `String` tính `hash` đúng 1 lần rồi cache lại, làm key trong `HashMap` cực nhanh.
  - Cộng chuỗi bằng `+` trong vòng lặp tạo ra nhiều đối tượng trung gian trên Heap → dùng `StringBuilder` (không đồng bộ, nhanh nhất trong 1 luồng) hoặc `StringBuffer` (các hàm có `synchronized`, chậm hơn).

#### Câu 3: Java truyền tham số theo kiểu Pass-by-Value hay Pass-by-Reference?
- **Đáp án chuẩn**:
  - Java **100% là Pass-by-Value** cho cả primitive lẫn Object.
  - Với Object, thứ được copy truyền vào hàm là **giá trị của con trỏ tham chiếu (value of the reference)** cùng trỏ tới object trên Heap.
  - Hệ quả: Gọi `user.setName("B")` trong hàm thì object gốc đổi tên; nhưng gán `user = new User("C")` trong hàm thì biến gốc bên ngoài không hề đổi.

#### Câu 4: Tại sao không dùng `double`/`float` cho tiền tệ? `BigDecimal.equals()` khác `compareTo()` thế nào?
- **Đáp án chuẩn**:
  - `double` biểu diễn theo hệ nhị phân dấu phẩy động IEEE 754 nên không thể biểu diễn chính xác `0.1` (`0.1 + 0.2 = 0.30000000000000004`), gây lệch tiền khi cộng dồn.
  - Khi khởi tạo phải dùng **`new BigDecimal("0.1")`** hoặc **`BigDecimal.valueOf(0.1)`**, tuyệt đối không dùng `new BigDecimal(0.1)`.
  - **Bẫy so sánh**: `new BigDecimal("2.0").equals(new BigDecimal("2.00"))` trả về **`false`** vì `equals()` so sánh cả `value` lẫn `scale` (số chữ số thập phân). So sánh giá trị tiền bắt buộc dùng **`a.compareTo(b) == 0`**.

#### Câu 5: Phân biệt `abstract class` và `interface` (từ Java 8+)? Khi nào chọn cái nào?
- **Đáp án chuẩn**:
  - Sau Java 8/9, `interface` có thêm `default`, `static`, `private` method nhưng vẫn **không thể có biến trạng thái (instance fields — mọi biến đều là `public static final`)** và **không có constructor**. Một class chỉ `extends` được 1 `abstract class` nhưng `implements` được nhiều `interface`.
  - Chọn **`interface`** khi định nghĩa **hợp đồng hành vi / khả năng (Can-do / Role)** cho các class không cùng phả hệ (VD: `PaymentProcessor`, `Comparable`, `Serializable`).
  - Chọn **`abstract class`** khi các class con có quan hệ **`Is-a` chặt chẽ** và cần **chia sẻ trạng thái (fields) + constructor + khung xử lý chung (Template Method)** (VD: `AbstractBaseEntity` chứa `id, createdAt, updatedAt`).

#### Câu 6: Giải thích 5 nguyên lý SOLID kèm ví dụ thực tế trong dự án Spring Boot?
- **Đáp án chuẩn**:
  - **S (Single Responsibility)**: Mỗi class chỉ có 1 lý do để thay đổi (`OrderService` điều phối tạo đơn, tách riêng `InvoicePdfGenerator` và `EmailNotificationService`).
  - **O (Open/Closed)**: Mở cho mở rộng, đóng cho sửa đổi — thay vì `if (type.equals("MOMO")) ... else if ("VNPAY")` thì tách interface `PaymentStrategy` và inject `Map<String, PaymentStrategy>`.
  - **L (Liskov Substitution)**: Class con thay thế được class cha mà không làm sai lệch hành vi kỳ vọng (không override method của cha rồi ném `UnsupportedOperationException`).
  - **I (Interface Segregation)**: Tách nhiều interface nhỏ theo đúng nhu cầu client thay vì 1 fat interface bắt class con implement các hàm rỗng.
  - **D (Dependency Inversion)**: Module cấp cao không phụ thuộc trực tiếp module cấp thấp mà cả hai phụ thuộc vào Abstraction (`interface`) — nền tảng của Spring DI.

---

### 1.2. Spring Boot, REST API, SQL & Git Cơ Bản

#### Câu 7: Phân biệt IoC và DI? Tại sao Spring khuyến nghị dùng Constructor Injection thay vì `@Autowired` trên Field?
- **Đáp án chuẩn**:
  - **IoC (Inversion of Control)** là nguyên lý đảo ngược quyền điều khiển việc tạo và quản lý vòng đời object cho Container (`ApplicationContext`). **DI (Dependency Injection)** là kỹ thuật cụ thể để thực hiện IoC.
  - **4 ưu điểm của Constructor Injection**:
    1. Khai báo dependency là **`private final`** (bất biến, an toàn đa luồng).
    2. **Fail-fast**: Phát hiện ngay lỗi **Circular Dependency** lúc khởi động app.
    3. **Dễ viết Unit Test**: Khởi tạo trực tiếp `new OrderService(mockRepo)` trong JUnit không cần Reflection hay khởi chạy Spring Context.
    4. Đảm bảo object sinh ra luôn đủ dependency, không bị `NullPointerException`.

#### Câu 8: Các Bean Scope trong Spring là gì? Singleton Bean có Thread-safe không?
- **Đáp án chuẩn**:
  - Các scope: `singleton` (mặc định: 1 instance/ApplicationContext), `prototype` (mới mỗi lần lấy), `request`, `session`, `application`.
  - **Bẫy**: Singleton Bean **KHÔNG tự động thread-safe**! Hàng trăm request Tomcat cùng gọi chung 1 instance Singleton Bean; nó chỉ thread-safe khi được viết **Stateless** (không lưu dữ liệu của từng request vào biến field của bean).

#### Câu 9: Phân biệt `PUT`, `PATCH`, `POST` và tính Idempotent của các HTTP Method? 401 vs 403, 502 vs 504?
- **Đáp án chuẩn**:
  - **Idempotent**: Gọi 1 lần hay $N$ lần cùng tham số thì trạng thái tài nguyên trên server như nhau. `GET`, `PUT` (ghi đè toàn bộ), `DELETE` là Idempotent; `POST` **không** Idempotent; `PATCH` (cập nhật 1 phần) tùy cách cài đặt.
  - `401 Unauthorized` (Chưa xác thực / chưa đăng nhập hoặc token hết hạn) vs `403 Forbidden` (Đã xác thực nhưng không đủ quyền).
  - `502 Bad Gateway` (Nginx/Gateway gọi vào service đích nhưng nhận response lỗi hoặc từ chối kết nối) vs `504 Gateway Timeout` (Service đích xử lý quá lâu vượt quá thời gian chờ của Nginx/Gateway).

#### Câu 10: Phân biệt `WHERE` vs `HAVING`? Thứ tự thực thi logic của một câu SQL? ACID là gì?
- **Đáp án chuẩn**:
  - `WHERE` lọc từng dòng **trước khi** `GROUP BY` (dùng được Index); `HAVING` lọc nhóm **sau khi** đã `GROUP BY` và tính hàm gộp (`SUM`, `COUNT`).
  - Thứ tự logic: `FROM / JOIN` → `WHERE` → `GROUP BY` → `HAVING` → `SELECT` → `DISTINCT` → `ORDER BY` → `LIMIT / OFFSET`.
  - **ACID**: **Atomicity** (All-or-nothing, nhờ Undo Log), **Consistency** (Toàn vẹn ràng buộc dữ liệu), **Isolation** (Cô lập giữa các giao dịch đồng thời, nhờ MVCC + Lock), **Durability** (Đã commit là không mất dù sập nguồn, nhờ Redo Log / WAL).

#### Câu 11: Phân biệt `git merge` vs `git rebase`? `git revert` vs `git reset`?
- **Đáp án chuẩn**:
  - `merge` giữ nguyên lịch sử thật và tạo thêm 1 merge commit; `rebase` viết lại lịch sử bằng cách dời các commit sang ngọn mới để tạo lịch sử thẳng (không rebase nhánh chung `main`).
  - `reset` xóa/lùi con trỏ lịch sử (nguy hiểm trên nhánh đã push chung); `revert` **tạo một commit mới đảo ngược** lại thay đổi của commit cũ (an toàn tuyệt đối khi cần hủy code trên nhánh `main` / production).

---

# 🟡 LEVEL 2: Đào Sâu Cơ Chế Bên Trong (Core Middle / Medium)

### 2.1. Collections, Concurrency & JVM Internals

#### Câu 12: Mô tả chi tiết bước `put(key, value)` của `HashMap` (Java 8+)? Khi nào nó chuyển sang Red-Black Tree?
- **Đáp án chuẩn**:
  1. Tính `hash = (h = key.hashCode()) ^ (h >>> 16)` (trộn 16 bit cao xuống 16 bit thấp để giảm va chạm khi bảng còn nhỏ).
  2. Nếu mảng `table` chưa khởi tạo thì `resize()` tạo mảng kích thước mặc định `16`.
  3. Tìm vị trí bucket bằng phép toán bit: `index = (n - 1) & hash` (do `n` luôn là lũy thừa của 2).
  4. Nếu `table[index] == null` → đặt `Node` mới vào ngay.
  5. Nếu có va chạm (collision):
     - Nếu trùng key ngay node đầu → ghi đè `value`.
     - Nếu node đầu là `TreeNode` → chèn vào Red-Black Tree.
     - Nếu là LinkedList → duyệt đến cuối danh sách để nối node mới vào. **Điều kiện kép để Treeify**: Nếu số lượng phần tử trong bucket đạt **`>= 8` (`TREEIFY_THRESHOLD`) VÀ `table.length >= 64` (`MIN_TREEIFY_CAPACITY`)** thì chuyển LinkedList ($O(n)$) thành Red-Black Tree ($O(\log n)$). *(Nếu `table.length < 64` thì nó chỉ `resize()` gấp đôi chứ chưa chuyển thành cây!)*.
  6. Nếu tổng số phần tử `++size > threshold (capacity * 0.75)` → gọi `resize()` nhân đôi mảng.

#### Câu 13: `ConcurrentHashMap` ở Java 8+ khác gì Java 7 và `Hashtable`? Tại sao nó không cho phép `null` key/value?
- **Đáp án chuẩn**:
  - `Hashtable` khóa toàn bộ bảng (`synchronized` trên cả hàm).
  - **Java 7 `ConcurrentHashMap`** dùng mảng 16 `Segment` (mỗi `Segment` kế thừa `ReentrantLock`) → khóa theo vùng.
  - **Java 8+ `ConcurrentHashMap`** bỏ `Segment`, dùng mảng `volatile Node<K,V>[]`:
    - **Đọc (`get`)**: Hoàn toàn không khóa (lock-free) nhờ `volatile`.
    - **Ghi vào bucket trống**: Dùng **CAS (Compare-And-Swap)** không cần lock.
    - **Ghi vào bucket đã có phần tử**: Chỉ **`synchronized` trên đúng Node đầu tiên (bucket head)** của bucket đó → các bucket khác vẫn ghi song song.
  - **Tại sao cấm `null`**: Trong môi trường đa luồng, nếu `get(key)` trả về `null`, không thể biết là "key chưa tồn tại" hay "key tồn tại với giá trị `null`" (không thể kiểm tra bằng `containsKey(key)` vì giữa 2 lệnh `containsKey` và `get` luồng khác đã có thể thêm/xóa key đó — Race Condition).

#### Câu 14: Phân biệt `synchronized`, `volatile`, `ReentrantLock` và `AtomicInteger` (CAS)? Quan hệ `happens-before` là gì?
- **Đáp án chuẩn**:
  - **Happens-before** là cam kết của Java Memory Model (JMM): nếu thao tác A *happens-before* thao tác B thì mọi kết quả ghi bộ nhớ của A bắt buộc phải nhìn thấy được (visible) bởi B.
  - `volatile`: Đảm bảo **Visibility** (đọc/ghi thẳng RAM chính, không dùng cache L1/L2 cũ) và **chặn Reordering**, nhưng **KHÔNG đảm bảo Atomicity** cho `count++` (vì `count++` gồm 3 bước Read–Modify–Write).
  - `AtomicInteger` (CAS): Dùng lệnh nguyên tử của CPU (`cmpxchg`) để cập nhật không khóa (lock-free); rất nhanh khi xung đột vừa phải, có thể gặp vấn đề ABA (sửa bằng `AtomicStampedReference`).
  - `synchronized` vs `ReentrantLock`: Đều đảm bảo cả Visibility lẫn Atomicity (Mutual Exclusion). `ReentrantLock` hơn ở chỗ: có `tryLock(timeout)`, `lockInterruptibly()`, chế độ công bằng (`fair = true`), nhiều `Condition`, và **không gây Pinning cho Virtual Threads trên Java 21**.

#### Câu 15: Cho `ThreadPoolExecutor` với `corePoolSize = 5`, `maxPoolSize = 10`, `queueCapacity = 100`. Khi có 15 task gửi vào cùng lúc thì có mấy thread đang chạy?
- **Đáp án chuẩn (Bẫy rớt nhiều nhất)**:
  - Đáp án: **Chỉ có 5 thread đang chạy** (10 task còn lại nằm đợi trong `workQueue`)!
  - Thứ tự chuẩn của `ThreadPoolExecutor`:
    1. Số thread < `corePoolSize` (5) → tạo Core Thread chạy ngay.
    2. Đã đủ 5 Core Threads → **xếp task vào `workQueue`** cho đến khi đầy 100/100.
    3. **Chỉ khi `workQueue` đã đầy 100%** → mới tạo thêm thread mới lên tới `maxPoolSize` (10).
    4. Khi `workQueue` đầy VÀ số thread đạt `maxPoolSize` (10) → kích hoạt **RejectedExecutionHandler** (`AbortPolicy`, `CallerRunsPolicy`, `DiscardPolicy`, `DiscardOldestPolicy`).
  - **Vì sao cấm dùng `Executors.newFixedThreadPool()` trên Production?** Vì nó dùng `LinkedBlockingQueue` có sức chứa mặc định `Integer.MAX_VALUE` (hàng đợi vô hạn) → khi nghẽn tải, queue phình to làm nổ `OutOfMemoryError`.

#### Câu 16: Cấu trúc bộ nhớ JVM gồm những vùng nào? Vòng đời 1 Object từ lúc `new` đến khi vào Old Generation và cơ chế G1 GC?
- **Đáp án chuẩn**:
  - **Dùng riêng mỗi Thread**: `Stack` (lưu stack frame, biến local primitive, con trỏ tham chiếu), `PC Register`, `Native Method Stack`.
  - **Dùng chung toàn JVM**:
    - `Heap`: Chứa toàn bộ Object và String Pool. Chia làm **Young Generation** (`Eden` + 2 vùng Survivor `S0`, `S1`) và **Old (Tenured) Generation**.
    - `Metaspace` (từ Java 8 thay PermGen, nằm ở Native Memory): Lưu metadata của class, bytecode, method info.
  - **Đường đi của Object**: Mới `new` nằm ở `Eden` → khi `Eden` đầy, chạy **Minor GC** gom các object còn sống sang `S0` (tăng `age + 1`) → các lần Minor GC sau đảo qua lại giữa `S0` và `S1` → khi `age` đạt ngưỡng (mặc định 15) hoặc object quá lớn (Humongous) thì được đưa lên **Old Gen**.
  - **G1 GC (Garbage-First — mặc định từ Java 9+)**: Chia Heap thành hàng nghìn **Region** bằng nhau (1MB–32MB) thay vì chia khối vật lý cố định; ưu tiên dọn các Region có nhiều rác nhất trước để đáp ứng mục tiêu thời gian dừng (`-XX:MaxGCPauseMillis=200`).

---

### 2.2. Spring Boot, JPA/Hibernate, SQL & Security Internals

#### Câu 17: Kể vòng đời của 1 Spring Bean? Proxy của `@Transactional` / AOP được tạo ra ở bước nào? Giải thích cơ chế 3-Level Cache xử lý Circular Dependency?
- **Đáp án chuẩn**:
  - **Bean Lifecycle**: Khởi tạo (Constructor) → Populate Properties (Inject dependencies) → các interface `*Aware` → `BeanPostProcessor.postProcessBeforeInitialization` → `@PostConstruct` / `afterPropertiesSet` → **`BeanPostProcessor.postProcessAfterInitialization` (Chính tại đây `AnnotationAwareAspectJAutoProxyCreator` bọc Bean bằng JDK Dynamic Proxy hoặc CGLIB Proxy!)** → Sẵn sàng sử dụng → `@PreDestroy`.
  - **Circular Dependency (`A ↔ B`)**:
    - Nếu dùng **Constructor Injection**: Bước 1 gọi `new A(b)` đã cần `b` ngay lập tức → **bế tắc, ném `BeanCurrentlyInCreationException` ngay lúc khởi động**.
    - Nếu dùng **Setter/Field Injection**: Spring giải được nhờ **3-Level Cache** (`singletonObjects` cấp 1, `earlySingletonObjects` cấp 2, `singletonFactories` cấp 3). Khi `new A()` xong (chưa inject field), Spring cất `ObjectFactory` của A vào cấp 3 → đi tạo B → B cần A → lấy tham chiếu sớm (early reference/proxy) của A từ cấp 3 đưa cho B → B tạo xong → quay lại gán B vào A!

#### Câu 18: Phân biệt rõ 4 tầng: Spring Data JPA, JPA, Hibernate và JDBC? Entity Lifecycle & Dirty Checking là gì?
- **Đáp án chuẩn**:
  - **4 tầng**:
    1. **Spring Data JPA**: Tạo **JDK Dynamic Proxy** (`SimpleJpaRepository`) cho interface `Repository` để tự sinh truy vấn từ tên hàm.
    2. **JPA (`jakarta.persistence`)**: Chỉ là bộ **Đặc tả (Specification)** gồm interface (`EntityManager`) và annotation, không có code thực thi.
    3. **Hibernate**: **JPA Provider (Implementation)** — quản lý `SessionImpl`, `PersistenceContext` (L1 Cache), Dirty Checking, `ActionQueue` và dịch JPQL sang SQL.
    4. **JDBC + HikariCP**: Quản lý kết nối TCP vật lý và chạy `PreparedStatement` xuống DB.
  - **4 trạng thái Entity**: `Transient` (mới `new`) → `Managed` (nằm trong L1 Cache `PersistenceContext`) → `Detached` (khi đóng session/clear) → `Removed` (chờ xóa).
  - **Dirty Checking**: Khi load Entity vào trạng thái `Managed`, Hibernate giữ 1 bản chụp (**hydrated state snapshot**). Lúc `flush` (trước khi commit), Hibernate tự so sánh object hiện tại với bản chụp; nếu có trường thay đổi thì **tự sinh câu lệnh `UPDATE`** mà không cần gọi `save()`.

#### Câu 19: Tại sao Database dùng B+ Tree cho Index? Phân biệt Clustered Index vs Secondary Index? Khi nào đánh Index mà câu SQL vẫn không dùng được Index?
- **Đáp án chuẩn**:
  - **Vì sao B+ Tree**: Node trung gian chỉ chứa Key dẫn đường (không chứa dữ liệu dòng) nên 1 trang đĩa 16KB chứa được hàng nghìn Key (chiều cao cây chỉ 3–4 tầng cho hàng chục triệu dòng = 3–4 I/O). Toàn bộ dữ liệu nằm ở **Leaf Nodes** và nối nhau bằng danh sách liên kết đôi → quét khoảng (`BETWEEN`, `>=`, `ORDER BY`) cực nhanh.
  - **Clustered Index (InnoDB PK)**: Leaf node lưu **nguyên cả dòng dữ liệu**. **Secondary Index**: Leaf node chỉ lưu `(Cột_Index, Primary_Key)` → khi tìm qua Secondary Index phải quay lại cây Clustered Index tìm lần 2 (**Bookmark Lookup**), trừ khi dùng **Covering Index** (chứa đủ các cột trong `SELECT`).
  - **6 trường hợp mất tác dụng Index**:
    1. Vi phạm quy tắc tiền tố trái (**Leftmost Prefix**) của Composite Index `(a, b, c)` (ví dụ query chỉ lọc `WHERE b = 1`).
    2. Bọc hàm hoặc phép toán lên cột index (`WHERE LOWER(email) = ...` hoặc `WHERE YEAR(created_at) = 2026`).
    3. Tìm kiếm `LIKE '%abc'` (ký tự đại diện ở đầu).
    4. Ép kiểu ngầm định (cột `phone` là `VARCHAR` nhưng truyền số `WHERE phone = 0987654321`).
    5. Điều kiện `OR` với một cột không có index.
    6. Độ chọn lọc (**Selectivity**) quá thấp (lọc lấy > 20–30% bảng, Optimizer thấy đọc tuần tự `Seq Scan` rẻ hơn nhảy đọc index ngẫu nhiên).

#### Câu 20: Luồng hoạt động của Spring Security Filter Chain và xác thực JWT + Refresh Token Rotation?
- **Đáp án chuẩn**:
  - Request từ Tomcat đi qua `DelegatingFilterProxy` → `FilterChainProxy` → danh sách các Filter trong `SecurityFilterChain`: `CorsFilter` → `CsrfFilter` → **`JwtAuthenticationFilter` (kế thừa `OncePerRequestFilter`)** → `ExceptionTranslationFilter` → `AuthorizationFilter`.
  - Tại `JwtAuthenticationFilter`: Đọc header `Authorization: Bearer <token>`, kiểm tra chữ ký (**RS256** dùng Public Key cho Microservices tốt hơn **HS256** dùng chung Secret) và hạn `exp`. Nếu hợp lệ, đặt `UsernamePasswordAuthenticationToken` vào `SecurityContextHolder` (lưu trong `ThreadLocal`).
  - **Access Token ngắn (5–15 phút) + Refresh Token dài (7–30 ngày)**:
    - Refresh Token được hash lưu trong Redis/DB.
    - **Refresh Token Rotation**: Mỗi lần gọi `/auth/refresh`, server thu hồi Refresh Token cũ và cấp cặp Access + Refresh Token mới. Nếu phát hiện Refresh Token cũ đã thu hồi mà vẫn được gửi lên lần nữa (**Token Reuse Detection** — dấu hiệu bị đánh cắp), lập tức khóa toàn bộ phiên đăng nhập của user đó!
    - **Logout / Revoke JWT tức thì**: Lưu `jti` (JWT ID) vào **Redis Blacklist** với TTL bằng đúng thời gian sống còn lại của Access Token.

---

### 2.3. Microservices, API Gateway, Network/DNS, Observability & CI/CD

#### Câu 21: Khi người dùng gõ `https://api.shop.com/v1/orders` trên trình duyệt, chuyện gì xảy ra theo đúng thứ tự từ đầu đến cuối?
- **Đáp án chuẩn (Trình bày trong 3 phút)**:
  1. **Phân giải DNS**: Kiểm tra cache trình duyệt → OS cache + `/etc/hosts` → hỏi **Recursive Resolver** (ISP / `1.1.1.1` / `8.8.8.8`) → hỏi tuần tự **Root NS** (`.`) → **TLD NS** (`.com`) → **Authoritative NS** (`shop.com`) để lấy bản ghi **A Record** (IP của Load Balancer/CDN) theo thời gian lưu cache **TTL**.
  2. **TCP 3-Way Handshake**: Client gửi `SYN` → Server trả `SYN-ACK` → Client trả `ACK` để thiết lập kết nối TCP tin cậy tại cổng `443`.
  3. **TLS 1.3 Handshake**: Kiểm tra chứng chỉ số (Certificate Chain của Let's Encrypt/CA), thỏa thuận bộ mã hóa và trao đổi khóa bất đối xứng (Diffie-Hellman) để sinh ra **Session Key đối xứng** mã hóa toàn bộ luồng dữ liệu sau đó.
  4. **CORS Preflight (nếu gọi từ Web App khác domain)**: Trình duyệt gửi request `OPTIONS` trước để kiểm tra header `Access-Control-Allow-Origin`. *(Lưu ý: CORS là cơ chế bảo vệ của trình duyệt chặn JS đọc response, không phải Postman hay server tự chặn!)*.
  5. **Reverse Proxy / Load Balancer (Nginx / AWS ALB)**: Thực hiện **SSL Termination**, kiểm tra giới hạn kích thước body (`client_max_body_size`), rồi chuyển tiếp vào mạng nội bộ.
  6. **API Gateway (Spring Cloud Gateway)**: Xác thực chữ ký JWT, kiểm tra **Rate Limit** trên Redis, gắn mã **`traceId`** (W3C Trace Context), định tuyến tới `order-service`.
  7. **Application & Data Layer**: `order-service` đi qua Security Filter → Controller (`@Valid`) → Service (`@Transactional`) → mượn kết nối từ **HikariCP** → thực thi SQL trên **PostgreSQL** / ghi **Outbox** → đẩy **Kafka** → trả JSON response ngược lại cho Client.

#### Câu 22: Phân biệt Liveness Probe, Readiness Probe và Startup Probe trong Kubernetes / Spring Actuator? Cấu hình sai dẫn đến thảm họa gì?
- **Đáp án chuẩn**:
  - **Startup Probe**: Kiểm tra ứng dụng đã khởi động xong chưa (tránh việc Spring Boot khởi động lâu bị K8s tưởng treo rồi kill liên tục).
  - **Liveness Probe (`/actuator/health/liveness`)**: Kiểm tra tiến trình JVM còn sống hay bị treo cứng (deadlock). Nếu fail → **Kubelet sẽ restart (kill và tạo lại) Pod**.
  - **Readiness Probe (`/actuator/health/readiness`)**: Kiểm tra Pod đã sẵn sàng nhận traffic chưa. Nếu fail → **K8s tạm thời rút IP của Pod khỏi danh sách Endpoint của Service/Load Balancer** (không gửi request mới vào, nhưng **KHÔNG restart Pod**).
  - **Bẫy cấu hình sai**: Đưa kiểm tra kết nối DB/Redis/External API vào **Liveness Probe**! Khi DB chỉ bị chập chờn chậm 5 giây, toàn bộ 20 Pod đều báo Liveness fail và bị K8s đồng loạt kill & restart cùng lúc → gây sập toàn diện hệ thống (**Cascading Restart Loop**).

#### Câu 23: Kể toàn bộ CI/CD Pipeline chuẩn từ lúc tạo Pull Request đến khi lên Production và cách Rollback trong 5 phút?
- **Đáp án chuẩn**:
  1. **Khi mở PR vào `main`**: Chạy **CI Pipeline** (Checkout → Cài JDK 21 + Cache Maven → Chạy Unit Test + Integration Test bằng **Testcontainers** → Kiểm tra Coverage JaCoCo & Sonar/Lint → Quét lộ secret bằng `gitleaks`). Nếu bất kỳ bước nào đỏ → chặn nút Merge (Branch Protection).
  2. **Khi Merge vào `main`**:
     - Build Docker Image bằng **Multi-stage Dockerfile** (chạy dưới quyền **non-root**, image < 250MB), gắn tag bất biến theo **Git Commit SHA (`sha-a1b2c3d`)** (tuyệt đối không dùng tag `:latest` vì không thể biết đang chạy bản nào và không rollback chính xác được).
     - Quét lỗ hổng bảo mật image bằng **Trivy** (chặn nếu có lỗi `CRITICAL`).
     - Push image lên Registry (GHCR / GitLab Registry).
  3. **Deploy Staging (Tự động)**: Cập nhật image mới lên môi trường Staging → chạy **Smoke Test** tự động gọi `/actuator/health` và API chính.
  4. **Deploy Production (Manual Approval)**: Yêu cầu Tech Lead bấm Approve trên GitHub Environment → thực hiện **Rolling Update** (kết hợp `server.shutdown=graceful` + Readiness Probe để không rớt request đang xử lý).
  5. **Rollback trong < 3 phút**:
     - Với App Code: Chỉ cần chạy lệnh trỏ lại về **Image Tag `sha-cũ`** (`kubectl rollout undo` hoặc `rollback.sh <old-sha>`), không bao giờ ngồi sửa code rồi chờ build lại từ đầu khi đang cháy sự cố!
     - Với Database Migration (Flyway): Không dùng `flyway undo` trên production mà bắt buộc tuân thủ quy tắc **Backward-Compatible Migration (Expand → Migrate → Contract)**: ví dụ muốn đổi tên cột thì bước 1 chỉ thêm cột mới (code cũ vẫn chạy được nếu rollback), đợi ổn định mới xóa cột cũ ở bản release sau.

---

# 🟠 LEVEL 3: Bẫy Thực Chiến & Sự Cố Production (Production Traps / Medium–Hard)

### 3.1. Bẫy JPA / Hibernate & Spring Boot Làm Sập Production

#### Câu 24: [Bẫy Hibernate ActionQueue] Trong một hàm `@Transactional`, bảng `users` có cột `email UNIQUE`. Bạn gọi `userRepository.delete(oldUser)` rồi ngay dòng dưới gọi `userRepository.save(new User(sameEmail))`. Tại sao hệ thống nổ lỗi `DuplicateKeyException`?
- **Nguyên nhân gốc rễ**:
  - Hibernate không bắn câu lệnh SQL ngay lúc bạn gọi `delete()` hay `save()`, mà gom vào **`ActionQueue`** của Persistence Context và chỉ thực thi khi `flush()` theo **thứ tự ưu tiên cố định**:
    1. `OrphanRemovalAction`
    2. **`EntityInsertAction` (`INSERT` luôn chạy TRƯỚC!)**
    3. `EntityUpdateAction`
    4. `CollectionRemove / Update / Recreate`
    5. **`EntityDeleteAction` (`DELETE` luôn chạy CUỐI CÙNG!)**
  - Vì lệnh `INSERT` người dùng mới chạy trước khi lệnh `DELETE` người dùng cũ được gửi xuống DB nên vi phạm ràng buộc `UNIQUE(email)`!
- **Cách sửa**: Gọi `userRepository.flush()` (hoặc `entityManager.flush()`) ngay sau dòng `delete(oldUser)`.
- **Câu hỏi xoáy thêm**: *Nếu gọi `flush()` xong mà dòng code phía sau ném RuntimeException thì lệnh `DELETE` đã flush có được rollback không?* → **CÓ rollback hoàn toàn!** Vì `flush()` chỉ đẩy SQL xuống DB trên kết nối transaction đang mở chứ **không phải là `commit()`**.

#### Câu 25: [Bẫy N+1 & Phân Trang OOM] Giải thích bài toán N+1 Query? Tại sao đổi `@OneToMany` sang `FetchType.EAGER` hoặc dùng `JOIN FETCH` kết hợp `Pageable` lại làm sập Production?
- **Phân tích 3 chiếc bẫy**:
  1. **Mặc định của JPA**: `@ManyToOne` và `@OneToOne` mặc định là **`EAGER`** (luôn phải đổi sang `FetchType.LAZY`!). `@OneToMany` mặc định là `LAZY`.
  2. **Tại sao đổi `@OneToMany` sang `EAGER` là "Red Flag"**: Gây tích Đề-các (Cartesian Product) kéo thừa dữ liệu khổng lồ, và nếu một Entity có 2 danh sách `List` cùng `EAGER` (hoặc cùng `JOIN FETCH`) sẽ nổ ngay lỗi **`MultipleBagFetchException: cannot simultaneously fetch multiple bags`**.
  3. **Bẫy chết người `JOIN FETCH` + `Pageable`**: Khi viết `@Query("SELECT o FROM Order o JOIN FETCH o.items") Page<Order> findAll(Pageable page)`, do 1 `Order` nối với nhiều `OrderItem` sinh ra nhiều dòng kết quả, Hibernate **không thể gắn `LIMIT / OFFSET` vào câu SQL dưới DB** (vì `LIMIT 10` sẽ cắt theo số dòng `items` làm thiếu dữ liệu của `Order`). Hậu quả: Hibernate in cảnh báo **`HHH90003004: firstResult/maxResults specified with collection fetch; applying in memory!`**, kéo **TOÀN BỘ bảng `orders` + `order_items` lên RAM của JVM** rồi mới cắt trang trong bộ nhớ → nổ **`OutOfMemoryError`**!
- **4 Cách giải quyết chuẩn Middle/Senior**:
  1. **`@BatchSize(size = 50)`** (hoặc cấu hình `spring.jpa.properties.hibernate.default_batch_fetch_size: 50`): Gom $N$ câu query lẻ thành `WHERE order_id IN (?, ?, ... 50)` ($1 + \lceil N/50 \rceil$ queries). **Chạy an toàn tuyệt đối với `Pageable`** và không bao giờ bị `MultipleBagFetchException`.
  2. **Tách 2 Query (Two-Step Query)**: Query 1 chỉ phân trang lấy `List<Long> orderIds` (`SELECT o.id FROM Order o`); Query 2 dùng `JOIN FETCH WHERE o.id IN (:orderIds)`.
  3. **DTO / Interface Projection**: Dùng cho API danh sách chỉ đọc — chỉ `SELECT` đúng các cột cần thiết, **bỏ qua hoàn toàn Persistence Context (L1 Cache) và Dirty Checking**, giảm 70% RAM.
  4. Nếu cần `JOIN FETCH` 2 collection (không phân trang): Đổi `List` sang `Set` (`LinkedHashSet`) hoặc tách 2 câu query `JOIN FETCH` liên tiếp trong cùng 1 hàm `@Transactional(readOnly = true)` (Hibernate tự ghép trong L1 Cache).

#### Câu 26: [Bẫy Batch Insert] Tại sao gọi `repository.saveAll(10_000_items)` vẫn bắn ra 10.000 câu lệnh `INSERT` đơn lẻ? Làm sao để Batch Insert thật sự?
- **3 nguyên nhân & cách sửa**:
  1. **Bẫy `GenerationType.IDENTITY`**: Nếu khóa chính `@Id` dùng `IDENTITY` (`SERIAL` / `AUTO_INCREMENT`), **Hibernate âm thầm tắt hoàn toàn tính năng JDBC Batch Insert** vì nó buộc phải chạy từng lệnh `INSERT` ngay lập tức để lấy được ID sinh ra từ DB!
     - *Sửa*: Đổi sang **`GenerationType.SEQUENCE`** (kèm `allocationSize = 50`) hoặc dùng `JdbcTemplate.batchUpdate()`.
  2. **Thiếu cấu hình Hibernate & Driver**: Phải bật `hibernate.jdbc.batch_size: 50`, `order_inserts: true`, `order_updates: true`, và thêm tham số **`reWriteBatchedInserts=true`** vào JDBC URL của PostgreSQL (hoặc `rewriteBatchedStatements=true` với MySQL) để gộp thành `INSERT INTO ... VALUES (...), (...), (...)`.
  3. **Bẫy tràn L1 Cache & $O(n^2)$ Dirty Checking**: Giữ 50.000 entity trong `PersistenceContext` của 1 `@Transactional` sẽ làm nổ Heap OOM và mỗi lần flush phải duyệt kiểm tra toàn bộ 50.000 entity.
     - *Sửa*: Cứ mỗi `500` bản ghi phải gọi `entityManager.flush(); entityManager.clear();`.

#### Câu 27: [Bẫy HikariCP Starvation] Trong hàm `@Transactional`, bạn lưu đơn hàng (5ms), sau đó gọi HTTP sang cổng thanh toán/gửi mail (mất 3 giây), rồi cập nhật trạng thái đơn. Chuyện gì xảy ra khi có 20 user bấm thanh toán cùng lúc?
- **Sự cố Production**:
  - Dù thao tác DB chỉ tốn 5ms, nhưng vì hàm gắn `@Transactional`, **kết nối DB của HikariCP (và cả khóa dòng Row Lock)** bị giữ khư khư suốt 3.000ms ngồi chờ HTTP trả về!
  - Với `maximumPoolSize = 10` (mặc định của HikariCP), chỉ cần **10 request đồng thời** là cạn sạch pool. Toàn bộ các API khác của hệ thống (kể cả API đăng nhập, xem sản phẩm) đều chết dây chuyền với lỗi `HikariPool-1 - Connection is not available, request timed out after 30000ms`.
- **Cách giải quyết chuẩn**:
  - **Tuyệt đối không gọi I/O mạng ngoài (HTTP, gRPC, gửi mail) bên trong `@Transactional`**.
  - Dùng **`TransactionTemplate`** tách nhỏ phạm vi giao dịch:
    1. *Tx 1 (5ms)*: Lưu đơn hàng trạng thái `PENDING` rồi commit ngay (trả connection về pool).
    2. *Ngoài Tx (3.000ms)*: Gọi HTTP sang cổng thanh toán (không giữ bất kỳ DB connection nào).
    3. *Tx 2 (5ms)*: Mở transaction mới cập nhật đơn thành `PAID` hoặc `FAILED`.
  - **Công thức tính HikariCP Pool Size chuẩn của PostgreSQL**:
    $$\text{Pool Size} = (\text{Số Core CPU của DB} \times 2) + \text{Effective Spindle Count}$$
    Thường chỉ đặt **20–50 kết nối/instance** (lưu ý: `Số Pod K8s × Pool Size` phải nhỏ hơn `max_connections` của DB; đặt pool = 200 sẽ làm DB nghẽn CPU vì context switching!).

#### Câu 28: Kể 4 trường hợp gắn `@Transactional` nhưng khi ném lỗi dữ liệu vẫn KHÔNG được Rollback?
- **4 nguyên nhân**:
  1. **Self-invocation (Gọi nội bộ trong cùng 1 class)**: `this.createOrder()` không đi qua lớp vỏ **Spring AOP Proxy** nên `@Transactional` hoàn toàn vô tác dụng. (Sửa: tách sang Service Bean khác, hoặc dùng `TransactionTemplate`, hoặc tự inject `@Lazy` chính mình).
  2. **Method là `private` hoặc `final`**: CGLIB Proxy tạo subclass nên không thể override hàm `private`/`final`.
  3. **Ném Checked Exception (`Exception`, `IOException`)**: Mặc định `@Transactional` chỉ rollback với `RuntimeException` và `Error`. Phải khai báo rõ **`@Transactional(rollbackFor = Exception.class)`**.
  4. **Bắt `try-catch` nuốt mất Exception** bên trong hàm mà không `throw` lại.

---

### 3.2. Bẫy Java 21 Virtual Threads, Concurrency & SQL Locking

#### Câu 29: [Bẫy Java 21 Virtual Threads] Khi bật `spring.threads.virtual.enabled=true` trên Spring Boot 3.2+, tại sao có trường hợp hệ thống bị treo cứng (Freeze) hoặc đánh sập Database nhanh hơn trước?
- **3 Cạm bẫy Production của Virtual Threads**:
  1. **Thread Pinning (Bẫy `synchronized`)**: Khi Virtual Thread gặp Blocking I/O, JVM tháo (unmount) nó khỏi OS Carrier Thread để nhường chỗ cho Virtual Thread khác. **NHƯNG trên Java 21**, nếu Blocking I/O nằm bên trong khối **`synchronized`** hoặc hàm `native`/JNI, Virtual Thread bị **ghim chặt (Pinned)** vào Carrier Thread! Nếu máy có 8 core (8 Carrier Threads) mà có 8 request cùng chờ DB bên trong `synchronized` → toàn bộ 8 Carrier Threads bị khóa cứng, cả ứng dụng đóng băng!
     - *Cách phát hiện & sửa*: Bật `-Djdk.tracePinnedThreads=full` hoặc JFR event `jdk.VirtualThreadPinned`; thay `synchronized` quanh I/O bằng **`ReentrantLock`**.
  2. **Không Pool Virtual Thread nhưng phải chặn bằng `Semaphore` / Bulkhead**: Không bao giờ đưa Virtual Thread vào Thread Pool. Nhưng vì có thể sinh ra 100.000 Virtual Threads đồng thời, nếu không có van tiết lưu, cả 100.000 luồng sẽ ập xuống DB/Downstream cùng lúc gây DDoS nội bộ. Phải dùng **`Semaphore`** (VD `new Semaphore(50)`) hoặc **Resilience4j Bulkhead** để giới hạn độ đồng thời.
  3. **Tác vụ CPU-Bound**: Virtual Thread không giúp code tính toán nặng CPU chạy nhanh hơn.

#### Câu 30: [Bẫy `UUIDv4` & Deadlock Chuyển Tiền] (a) Tại sao dùng `UUID.randomUUID()` làm Primary Key lại làm chậm Insert và phình Index? (b) Khi 2 người chuyển tiền chéo `A → B` và `B → A` cùng lúc, làm sao chống âm tiền VÀ chống Deadlock?
- **(a) Bẫy `UUIDv4` làm Primary Key**:
  - `UUID` chuỗi 36 bytes (hoặc 16 bytes) to hơn `BIGINT` (8 bytes). Vì mọi Secondary Index đều lưu kèm PK ở node lá nên toàn bộ Index bị phình to.
  - Nguy hiểm hơn: `UUIDv4` sinh ngẫu nhiên hoàn toàn nên chèn vào vị trí bất kỳ ở giữa cây B+ Tree Clustered Index → buộc DB phải **tách trang (Page Split)** liên tục và gây phân mảnh đĩa nặng. **Giải pháp**: dùng `BIGSERIAL` / `BIGINT`, **Snowflake ID**, hoặc **`UUIDv7` (TSID — có thứ tự thời gian ở đầu)**.
- **(b) Chống Âm Tiền (TOCTOU) & Chống Deadlock khi chuyển chéo `A ↔ B`**:
  - **Chống TOCTOU / Lost Update**: Dùng Atomic SQL (`UPDATE accounts SET balance = balance - :amt WHERE id = :id AND balance >= :amt`) hoặc Pessimistic Lock (`SELECT ... FOR UPDATE`).
  - **Triệt tiêu Deadlock bằng Quy tắc Sắp xếp Thứ tự Khóa (Lock Ordering)**:
    - Nếu Tx 1 khóa A rồi chờ B, cùng lúc Tx 2 khóa B rồi chờ A → **Deadlock**.
    - Sửa bằng cách luôn khóa tài khoản có **ID nhỏ trước, ID lớn sau**:
      ```java
      Long firstId = Math.min(fromAccountId, toAccountId);
      Long secondId = Math.max(fromAccountId, toAccountId);
      accountRepo.findByIdForUpdate(firstId);
      accountRepo.findByIdForUpdate(secondId);
      ```
    - *Lưu ý bẫy*: Cột trong `WHERE` của `SELECT ... FOR UPDATE` bắt buộc phải có **Index**, nếu không DB sẽ khóa toàn bộ bảng (Gap Lock + Record Lock trên mọi dòng)!

---

### 3.3. Bẫy Redis & Kafka Trên Production

#### Câu 31: Phân biệt Cache Penetration, Cache Stampede (Breakdown), Cache Avalanche và tại sao "Update DB rồi Update Cache" lại gây lỗi dữ liệu cũ (Stale Data)?
- **3 sự cố Cache**:
  1. **Cache Penetration (Thủng Cache)**: Gọi liên tục `id = -999` không tồn tại dưới DB → Cache luôn miss, 100% tải đâm thẳng vào DB. *Fix*: **Cache luôn giá trị `"NULL"` với TTL ngắn (60–300s)** hoặc chặn trước bằng **Bloom Filter**.
  2. **Cache Stampede / Breakdown (Đập vỡ Cache)**: Một Hot Key vừa hết hạn đúng lúc 5.000 request ập vào → cả 5.000 luồng cùng chọc xuống DB. *Fix*: Dùng **Redis Distributed Lock (`tryLock`) + Double-Checked Locking** để chỉ 1 luồng xuống DB nạp lại cache, hoặc dùng **Logical Expiration** (làm mới ngầm trước khi hết hạn).
  3. **Cache Avalanche (Tuyết lở Cache)**: Hàng vạn key hết hạn cùng một giây. *Fix*: Cộng thêm **Random Jitter** vào TTL (`TTL = 3600 + random(0, 600)`).
- **Tại sao không "Update DB rồi Update Cache"**: Nếu Thread A ghi DB (`100`), Thread B ghi DB (`200`), nhưng Thread B ghi Redis (`200`) trước rồi Thread A bị trễ mạng mới ghi đè Redis (`100`) → Cache lưu giá trị sai `100` suốt thời gian sống! Phải dùng **Cache-Aside: Update DB trước → XÓA (Delete) Cache sau**.

#### Câu 32: Giải thích 5 bẫy Kafka Production: (1) Poison Pill, (2) Rebalance Storm, (3) Retry Storm, (4) Tự đẩy ra Thread Pool trong `@KafkaListener`, (5) Đảo thứ tự khi Retry?
1. **Poison Pill (Bản tin độc)**: 1 message lỗi JSON/nghiệp vụ vĩnh viễn nếu cứ retry mãi sẽ làm kẹt cứng cả Partition. *Fix*: Cấu hình `DefaultErrorHandler` phân loại lỗi không thể phục hồi (`DeserializationException`, `ValidationException`) để bắn thẳng vào **DLT (Dead Letter Topic)** ngay lập tức.
2. **Rebalance Storm (Bão Rebalance)**: Kéo `max.poll.records = 500` bản tin mà xử lý/retry mất > `max.poll.interval.ms` (300s) → Broker tưởng Consumer chết nên đá khỏi group và Rebalance → Consumer khác nhận lại đúng 500 bản tin đó rồi lại quá hạn → treo toàn bộ group! *Fix*: Giảm `max.poll.records` (VD: `50`), không block luồng poll, bật **Static Group Membership (`group.instance.id`)**.
3. **Retry Storm**: Downstream đang yếu mà Consumer retry dồn dập làm sập hẳn downstream. *Fix*: Dùng **Non-blocking `@RetryableTopic`** (chia các topic trễ `retry-5s`, `retry-30s`, `retry-5m`, `dlt`) kết hợp **Circuit Breaker**.
4. **Bẫy dùng `ExecutorService` trong `@KafkaListener`**: Đẩy message sang thread pool riêng khiến hàm `@KafkaListener` trả về ngay → Spring Kafka **tự commit offset trước khi worker thread xử lý xong** → crash pod là mất message! *Fix*: Tăng độ song song bằng số **Partition + `concurrency`**.
5. **Đảo thứ tự sự kiện**: Đặt `key = orderId` để vào cùng 1 Partition, đồng thời dưới DB luôn kiểm tra **State Machine + `version`** để không cho sự kiện cũ ghi đè trạng thái mới.

---

# 🔴 LEVEL 4: Thiết Kế Hệ Thống & Tải Cao (System Design & Architecture / Hard)

#### Câu 33: [System Design — Fintech/Bank] Thiết kế cơ chế chống thanh toán trùng (Double-Charge) và xử lý khi gọi cổng thanh toán bị Timeout (không biết đã trừ tiền hay chưa)?
- **Phần 1: Phòng thủ 3 lớp chống Thanh Toán Trùng (Idempotency Architecture)**:
  1. **Lớp 1 — `Idempotency-Key` ở Client/Gateway**: Khi mở màn hình thanh toán, Client sinh `UUID` gửi kèm header `Idempotency-Key`. Server kiểm tra Redis: nếu đã `COMPLETED` và khớp hash body → trả lại kết quả cũ ngay.
  2. **Lớp 2 — Redis Distributed Lock (Fail-Fast cho 2 cú click cùng mili-giây)**: Chạy lệnh nguyên tử `SET lock:pay:{idempotencyKey} {reqId} EX 15 NX`. Nếu trả về `false` → từ chối ngay với `HTTP 409 Conflict` ("Giao dịch đang được xử lý").
  3. **Lớp 3 — Database Unique Constraint & Atomic State Update (Chốt chặn cuối cùng)**: Tạo `UNIQUE INDEX (idempotency_key)` trên bảng `payment_transactions` và cập nhật trạng thái đơn bằng SQL nguyên tử: `UPDATE orders SET status = 'PAID' WHERE id = :id AND status = 'PENDING'`.
- **Phần 2: Xử lý khi gọi cổng thanh toán (Napas/VNPAY/MoMo) bị `SocketTimeoutException`**:
  - **Cấm kỵ**: Tuyệt đối không đánh dấu đơn `FAILED` hay tự động hoàn kho/hủy đơn ngay (vì bên đối tác có thể đã trừ tiền khách!).
  - **Quy trình chuẩn**:
    1. Chuyển trạng thái giao dịch sang **`UNKNOWN` / `PROCESSING`**.
    2. Đẩy vào **Worker truy vấn trạng thái (Query Status Job)** hỏi lại API đối tác theo lịch backoff (30s, 2m, 5m, 15m) bằng mã `transactionRef` để đồng bộ trạng thái thật.
    3. **Đối soát tự động cuối ngày (Reconciliation T+1)**: So khớp bảng giao dịch nội bộ với file đối soát của ngân hàng/cổng thanh toán để xử lý mọi giao dịch lệch trạng thái.

#### Câu 34: [System Design — E-Commerce] Thiết kế hệ thống Flash Sale (100.000 người cùng bấm mua 1.000 sản phẩm lúc 12:00:00) đảm bảo Zero Overselling và không sập Database?
- **Kiến trúc 5 bước**:
  1. **Read Path**: Cache trang tĩnh trên CDN; số lượng tồn kho đọc từ Redis và đẩy xuống trình duyệt qua **SSE / WebSocket** (không cho 100k user gọi DB).
  2. **Traffic Shaping tại API Gateway**: Rate Limit theo `userId`/`IP` + chặn bot.
  3. **Trừ kho nguyên tử trên RAM bằng Redis Lua Script (Zero Overselling)**:
     - Nạp trước tồn kho lên Redis (`flashsale:101:stock = 1000`).
     - Dùng **1 Lua Script** thực hiện đồng thời: kiểm tra `SISMEMBER` (mỗi user chỉ được mua 1 cái) → kiểm tra `stock > 0` → `DECR stock` → `SADD user`. Toàn bộ 99.000 request trượt sẽ bị chặn ngay tại RAM của Redis mà **không chạm vào DB**.
  4. **Ghi đơn bất đồng bộ qua Kafka**: Đúng 1.000 request thành công từ Lua Script được bắn sự kiện vào **Kafka** và nhận ngay `HTTP 202 Accepted` ("Đang tạo đơn"). Worker đọc Kafka ghi tuần tự xuống PostgreSQL.
  5. **Hoàn kho đơn treo & Observability**: Job quét các đơn sau 15 phút chưa thanh toán để hủy đơn dưới DB và cộng trả lại `INCR` vào Redis. Giám sát toàn bộ qua **Prometheus/Grafana + Zipkin Tracing + Alertmanager**.

#### Câu 35: [System Design — Microservices] Giải quyết bài toán Dual-Write và Giao dịch phân tán giữa `Order → Inventory → Payment`?
- **1. Chống mất sự kiện (Dual-Write Problem) bằng Transactional Outbox Pattern**:
  - Không thể vừa `commit` DB vừa `kafkaTemplate.send()` an toàn tuyệt đối.
  - Giải pháp: Trong **cùng 1 Local Transaction của PostgreSQL**, ghi đồng thời vào bảng `orders` và bảng `outbox_events(id, aggregate_id, payload, status='NEW')`.
  - Dùng **Polling Publisher (`SELECT ... FOR UPDATE SKIP LOCKED`)** hoặc **Debezium CDC (đọc WAL)** đẩy bản ghi từ `outbox_events` lên Kafka (`acks=all`, `enable.idempotence=true`).
  - Vì Outbox là **At-Least-Once**, phía Consumer phải có bảng **`processed_events(event_id UNIQUE)`** để chống xử lý trùng (**Idempotent Consumer**).
- **2. Điều phối giao dịch liên service bằng Saga Pattern**:
  - Không dùng **2PC/XA** vì khóa tài nguyên đồng bộ và biến Coordinator thành SPOF.
  - Dùng **Saga Choreography** (qua Kafka events cho luồng 2–4 service) hoặc **Saga Orchestration** (1 nhạc trưởng điều phối State Machine cho luồng phức tạp).
  - Khi `PaymentFailed`, kích hoạt **Compensating Transaction (Giao dịch bù trừ)**: phát sự kiện `ReleaseInventory` để hoàn lại tồn kho và chuyển đơn sang `CANCELLED`.

#### Câu 36: [SQL Thực Chiến Ngân Hàng] Một bảng giao dịch có 500 triệu – 1 tỷ dòng, đã đánh Index trên `created_at` và `status`, nhưng câu query lọc dữ liệu 6 tháng gần nhất vẫn chạy mất 30 giây. Bạn chẩn đoán và tối ưu theo thứ tự nào?
- **Quy trình 5 bước của Middle/Senior**:
  1. **Chạy `EXPLAIN (ANALYZE, BUFFERS)`**: Xem kế hoạch thực thi thật sự là `Index Scan`, `Bitmap Heap Scan` hay đã bị chuyển thành `Seq Scan`.
  2. **Phân tích độ chọn lọc (Selectivity)**: Lọc 6 tháng trên bảng 1 năm (~50% số dòng) khiến việc đọc qua Secondary Index rồi nhảy ngẫu nhiên vào bảng chính (Random I/O) đắt hơn đọc tuần tự, nên Optimizer cố tình bỏ Index!
  3. **Tối ưu Composite & Covering Index**: Đặt cột so sánh bằng (`status`) đứng trước cột khoảng (`created_at`), kèm `INCLUDE (amount, account_id)` để đạt **Index-Only Scan** (không cần đọc vào Heap/Clustered Table).
  4. **Thay `OFFSET` lớn bằng Keyset/Cursor Pagination**: `WHERE (created_at, id) < (:lastTime, :lastId) ORDER BY created_at DESC, id DESC LIMIT 20`.
  5. **Range Partitioning theo Tháng/Quý (`PARTITION BY RANGE (created_at)`) + Archiving**: Giúp DB chỉ quét đúng phân vùng cần thiết (**Partition Pruning**), giữ kích thước B+ Tree của từng tháng vừa khít trong RAM Buffer Pool, và cho phép tách/xóa dữ liệu cũ quá hạn tức thì bằng `DETACH/DROP PARTITION`.

#### Câu 37: [Production Troubleshooting] Đang 2h chiều, nhận cảnh báo p99 latency của API `/api/v1/orders` tăng vọt từ 120ms lên 5.000ms. Bạn khoanh vùng nguyên nhân gốc rễ trong 10 phút như thế nào?
- **Quy trình tam giác Observability (Metrics → Traces → Logs/Dumps)**:
  1. **Phút 1–3 — Xem Dashboard Metrics (Grafana / Prometheus)**:
     - Kiểm tra **HikariCP**: `hikaricp_connections_pending` có tăng vọt và `active` chạm trần `maxPoolSize` không?
     - Kiểm tra **JVM**: Heap memory có chạm đỉnh và GC Pause Time có dựng đứng không?
     - Kiểm tra **Hạ tầng & Phụ thuộc**: CPU throttling trên K8s, latency của Redis, Kafka consumer lag, hay response time của 3rd-party API.
  2. **Phút 3–6 — Xem Distributed Tracing (Zipkin / Jaeger / Tempo)**:
     - Lọc 1 request có duration > 5s, mở **Span Waterfall** để nhìn chính xác 4.9s bị ngâm ở bước nào (chờ mượn connection, câu SQL cụ thể nào, hay lệnh gọi HTTP nào).
  3. **Phút 6–10 — Chẩn đoán sâu tại điểm nghẽn & Giảm thiểu (Mitigate)**:
     - Nếu nghẽn ở DB: Kiểm tra `pg_stat_activity` xem có **Lock Wait / Long Transaction** không (kill pid ngâm lock nếu cần) hoặc chạy `EXPLAIN ANALYZE` câu slow query từ `pg_stat_statements`.
     - Nếu nghẽn trong JVM: Lấy **Thread Dump (`jstack <pid>`)** xem hàng loạt thread đang `BLOCKED` ở đâu, hoặc lấy Heap Dump nếu OOM.
     - Nếu xảy ra ngay sau bản deploy mới: **Rollback ngay về Image Tag SHA trước đó** để cứu dịch vụ trước, điều tra nguyên nhân sau!

