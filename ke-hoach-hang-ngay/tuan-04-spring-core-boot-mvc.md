# Tuần 4 (30/11 – 06/12/2026): Spring Core + Spring Boot + Spring MVC

> Bạn đã dùng Spring Boot ở FPT (điểm 3). Tuần này không học "cách dùng" mà học **"bên dưới nó chạy thế nào"** — đây là chỗ phân biệt Junior với Middle.
> Từ tuần này bắt đầu code **shop-order-platform** (monolith modular), các tuần sau đắp dần lên, tháng 3 không phải viết lại từ đầu.

---

## Ngày 22 — Thứ 2 (30/11): Reflection, Annotation, Maven/Gradle
**Kiến thức phải nắm:**
- Reflection: `Class.forName`, `getDeclaredMethods`, `setAccessible`, gọi method động — là nền của Spring/Hibernate/Jackson; nhược điểm (chậm, phá encapsulation).
- Annotation: `@Retention` (SOURCE/CLASS/RUNTIME), `@Target`; tự viết annotation + đọc bằng reflection.
- Serialization: `Serializable`, `serialVersionUID`, `transient`; vì sao Java serialization bị coi là rủi ro bảo mật, thay bằng JSON (Jackson).
- **Maven**: `pom.xml`, lifecycle (validate → compile → test → package → verify → install → deploy), scope (compile/provided/runtime/test), dependency tree & xung đột version (`mvn dependency:tree`), BOM, multi-module, wrapper `mvnw`. Gradle: biết so sánh.

**Code tay:** tự viết annotation `@LogExecutionTime` + mini "container" quét class bằng reflection và gọi method có annotation. Tạo project Spring Boot 3 (Java 21, Maven) cho `shop-order-platform`, cấu trúc package theo module (`user`, `product`, `order`, `common`).

**Tự kiểm tra:** Spring dùng reflection để làm gì? `transient` dùng khi nào? Maven scope `provided` là gì? Xung đột dependency xử lý thế nào?

---

## Ngày 23 — Thứ 3 (01/12): IoC, DI, Bean, ApplicationContext
**Kiến thức phải nắm:**
- IoC vs DI; `BeanFactory` vs `ApplicationContext`.
- Khai báo bean: `@Component/@Service/@Repository/@Controller` vs `@Configuration + @Bean`; `@ComponentScan`.
- 3 kiểu inject: constructor (**khuyến nghị** — immutable, dễ test, phát hiện vòng lặp), setter, field — vì sao tránh field injection.
- Nhiều bean cùng kiểu: `@Primary`, `@Qualifier`, inject `List<PaymentProcessor>`/`Map<String, ...>` (áp dụng Strategy).
- **Bean scope**: singleton (mặc định — bean phải stateless!), prototype, request, session; inject prototype vào singleton bị "đóng băng" → `ObjectProvider`/`@Lookup`.
- **Circular dependency**: vì sao Spring Boot 2.6+ cấm mặc định, cách sửa đúng (tách thiết kế) thay vì `@Lazy`.

**Code tay:** `PaymentProcessor` nhiều implementation, chọn theo type bằng `Map<String, PaymentProcessor>`; tạo lỗi circular dependency rồi sửa; demo singleton bean có field state bị race condition khi nhiều request.

**Tự kiểm tra:** IoC là gì? Vì sao constructor injection? Singleton bean có thread-safe không?

**LeetCode (20'):** Search in Rotated Sorted Array, 3Sum.

---

## Ngày 24 — Thứ 4 (02/12): Bean Lifecycle, AOP, Proxy và cạm bẫy @Transactional
**Kiến thức phải nắm:**
- **Bean lifecycle:** instantiate → populate properties → `*Aware` → `BeanPostProcessor.before` → `@PostConstruct` / `afterPropertiesSet` / init-method → `BeanPostProcessor.after` (**chỗ tạo proxy**) → sử dụng → `@PreDestroy`.
- AOP: Aspect, Advice (`@Before`, `@After`, `@AfterReturning`, `@AfterThrowing`, `@Around`), Pointcut, JoinPoint; weaving lúc runtime bằng proxy.
- **JDK dynamic proxy (có interface) vs CGLIB (subclass)** — Spring Boot mặc định CGLIB; hệ quả: method `final`/`private` không bị proxy.
- **Self-invocation**: gọi `this.methodB()` có `@Transactional`/`@Async`/`@Cacheable` trong cùng class → **không đi qua proxy → annotation không có tác dụng**. Cách sửa: tách sang bean khác.
- `@Transactional` trên method private/protected không chạy; checked exception mặc định không rollback (`rollbackFor`).

**Code tay:** viết Aspect `@LogExecutionTime` thật bằng `@Around`; tái hiện lỗi self-invocation với `@Transactional` (log SQL thấy không rollback) rồi sửa.

**Tự kiểm tra:** Kể bean lifecycle. AOP trong Spring hoạt động thế nào? Vì sao gọi nội bộ method `@Transactional` không chạy?

---

## Ngày 25 — Thứ 5 (03/12): Spring Boot internals + cấu hình
**Kiến thức phải nắm:**
- `@SpringBootApplication` = `@Configuration` + `@EnableAutoConfiguration` + `@ComponentScan`.
- **Auto-configuration**: `@Conditional*` (`OnClass`, `OnMissingBean`, `OnProperty`), file `AutoConfiguration.imports`; xem report bằng `--debug`. Starter là gì.
- Quá trình khởi động `SpringApplication.run()` ở mức tổng quan; embedded Tomcat.
- Cấu hình: `application.yml`, **profile** (`dev/staging/prod`), thứ tự ưu tiên (env variable > file), `@Value` vs `@ConfigurationProperties` (type-safe, validate); không để secret trong file.
- `CommandLineRunner`/`ApplicationRunner`, `ApplicationEvent` + `@EventListener` / `@TransactionalEventListener`.
- Logging: SLF4J + Logback, log level theo package, **MDC** để gắn requestId/traceId, không log password/token.

**Code tay:** cấu hình 3 profile; `@ConfigurationProperties` cho `app.order.*` có `@Validated`; Filter gán `requestId` vào MDC và pattern log in ra.

**Tự kiểm tra:** Auto-configuration hoạt động thế nào? `@Value` vs `@ConfigurationProperties`? Thứ tự ưu tiên config?

**LeetCode (20'):** Kth Largest Element (heap), Coin Change (DP nhập môn).

---

## Ngày 26 — Thứ 6 (04/12): Spring MVC + Validation + Exception handling
**Kiến thức phải nắm:**
- Luồng request: Tomcat thread → `Filter` → **DispatcherServlet** → HandlerMapping → `HandlerInterceptor` → Controller → `HttpMessageConverter` (Jackson) → response. Filter vs Interceptor vs AOP.
- Thread-per-request model; giới hạn thread Tomcat (`server.tomcat.threads.max`) — liên hệ virtual threads.
- `@RestController`, `@RequestMapping`, `@PathVariable`, `@RequestParam`, `@RequestBody`, `ResponseEntity`.
- **Bean Validation**: `@Valid`, `@NotNull/@NotBlank/@Size/@Email/@Positive`, validate nested, custom validator, validation group.
- **Global exception handler**: `@RestControllerAdvice` + `@ExceptionHandler`, chuẩn error response (Spring 6 hỗ trợ `ProblemDetail` RFC 7807/9457).
- DTO vs Entity (không trả Entity ra API), **MapStruct** + Lombok (`@Getter`, `@Builder`, cẩn thận `@Data` với Entity).

**Code tay (shop-order-platform):** module Product CRUD: Controller → Service → Repository (tạm dùng in-memory hoặc JPA đơn giản), DTO + MapStruct, validation, `GlobalExceptionHandler` trả error contract thống nhất `{code, message, details, traceId}`.

**Tự kiểm tra:** Kể luồng 1 request trong Spring MVC. Filter khác Interceptor? Vì sao không trả Entity ra ngoài?

---

## Ngày 27 — Thứ 7 (05/12): Lab 4–5h — khung shop-order-platform
- Module `user`, `product`, `order` (CRUD cơ bản, chưa cần DB thật nếu chưa tới tuần 5, nhưng khuyên dùng Postgres + JPA đơn giản qua Docker Compose đã có).
- Áp dụng đủ: constructor injection, profile, `@ConfigurationProperties`, MDC log, AOP log thời gian, error contract, validation, MapStruct.
- `@Scheduled` job đơn giản (hủy đơn PENDING quá 15 phút) — ghi chú vấn đề chạy nhiều instance → cần ShedLock (học lại tuần 9).
- Upload file ảnh sản phẩm (`MultipartFile`, giới hạn size, validate content type, lưu local) — câu hỏi thực tế hay gặp.
- Viết README khung.

**LeetCode (30'):** Lowest Common Ancestor of BST, Binary Tree Level Order Traversal.

---

## Ngày 28 — Chủ nhật (06/12): Review + luyện miệng
**10 câu phỏng vấn của tuần:**
1. IoC/DI là gì, Spring triển khai thế nào?
2. Bean scope; singleton bean có thread-safe không?
3. Bean lifecycle.
4. AOP + proxy JDK vs CGLIB.
5. Vì sao `@Transactional` không chạy khi self-invocation / method private?
6. Spring Boot auto-configuration hoạt động thế nào?
7. Luồng request trong Spring MVC; Filter vs Interceptor.
8. Xử lý exception toàn cục và chuẩn hóa error response.
9. Circular dependency: nguyên nhân và cách xử lý.
10. `@Scheduled` chạy trên 3 instance thì sao?

**Checklist:** ☐ shop-order-platform có module Product chạy được ☐ Demo self-invocation ☐ AOP log ☐ Note tuần 4

