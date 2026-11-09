# Tuần 6 (14/12 – 20/12/2026): Spring Security + JWT + Testing

> Skill audit: Security = 1 → mục tiêu 4. Testing = 3 → 4. Đây là tuần "lấp lỗ hổng lớn".

---

## Ngày 36 — Thứ 2 (14/12): Nền tảng bảo mật web
**Kiến thức phải nắm:**
- Authentication (bạn là ai) vs Authorization (bạn được làm gì).
- Hashing vs Encryption vs Encoding (Base64 không phải mã hóa!); **BCrypt** (salt, cost factor), vì sao không dùng MD5/SHA-256 trần cho password.
- Session-cookie (stateful) vs Token (stateless); cookie flags `HttpOnly`, `Secure`, `SameSite`.
- **OWASP Top 10 cho Backend**: Broken Access Control (IDOR — xem đơn của người khác bằng cách đổi id!), Injection (SQL injection, vì sao PreparedStatement/JPA parameter an toàn), Cryptographic failures, Security misconfiguration, SSRF; XSS & CSRF ở mức hiểu.
- HTTPS bảo vệ gì, không bảo vệ gì.

**Code tay:** demo SQL injection với query nối chuỗi bằng JdbcTemplate → sửa bằng parameter; demo IDOR trên API `GET /orders/{id}` → sửa bằng kiểm tra owner.

**Tự kiểm tra:** Hash vs encrypt? Vì sao BCrypt? IDOR là gì, phòng thế nào?

---

## Ngày 37 — Thứ 3 (15/12): Spring Security kiến trúc
**Kiến thức phải nắm:**
- **Security Filter Chain**: `DelegatingFilterProxy` → `FilterChainProxy` → `SecurityFilterChain` (các filter: CORS, CSRF, authentication, `ExceptionTranslationFilter`, `AuthorizationFilter`).
- `AuthenticationManager` → `AuthenticationProvider` → `UserDetailsService` + `PasswordEncoder`.
- `SecurityContextHolder` (ThreadLocal!) — liên hệ tuần 3; vấn đề với `@Async`.
- Cấu hình Spring Security 6 (lambda DSL, `SecurityFilterChain` bean — không còn `WebSecurityConfigurerAdapter`).
- 401 vs 403; `AuthenticationEntryPoint` vs `AccessDeniedHandler`.
- CSRF: vì sao API stateless dùng token thì thường tắt CSRF; CORS cấu hình ở đâu.

**Code tay:** đăng ký/đăng nhập với `UserDetailsService` + BCrypt + DB; custom 401/403 theo error contract.

> **⚠️ Bẫy `SecurityContextHolder` bị `null` khi gọi `@Async` / `CompletableFuture`:**
> - Mặc định `SecurityContextHolder` lưu thông tin user đăng nhập vào **`ThreadLocal` (`MODE_THREADLOCAL`)**. Khi từ Controller/Service gọi sang hàm `@Async` hoặc `CompletableFuture.supplyAsync()`, luồng con mới không có `SecurityContext` (`getAuthentication() == null`)!
> - **Bẫy sai lầm**: Đổi sang `MODE_INHERITABLETHREADLOCAL` với Thread Pool sẽ gây **lộ thông tin user (Cross-Request Security Leak)** vì thread con trong pool sau khi tạo lần đầu sẽ tái sử dụng mãi và giữ luôn `SecurityContext` cũ của user đầu tiên!
> - **Cách sửa chuẩn**: Bọc Executor bằng **`DelegatingSecurityContextAsyncTaskExecutor`** (hoặc `DelegatingSecurityContextExecutorService`) — nó tự động copy `SecurityContext` sang luồng con khi bắt đầu task và **tự động `clearContext()` trong `finally`** ngay khi task kết thúc.

**Tự kiểm tra:** Kể luồng 1 request qua Security filter chain. 401 vs 403? Vì sao REST API thường disable CSRF?

**LeetCode (20'):** Implement Trie, Word Search.

---

## Ngày 38 — Thứ 4 (16/12): JWT + Refresh Token
**Kiến thức phải nắm:**
- Cấu trúc JWT: header.payload.signature; **payload chỉ Base64 — không để dữ liệu nhạy cảm**; HS256 (secret chung) vs RS256 (public/private key — tốt cho microservices: service chỉ cần public key để verify).
- Claims: `sub`, `exp`, `iat`, `iss`, `aud`, `jti`, roles.
- **Access token ngắn (5–15') + Refresh token dài**; refresh token lưu DB/Redis (hash), **rotation** + phát hiện reuse.
- Revoke JWT: không revoke được trực tiếp → blacklist `jti` trong Redis đến khi hết hạn, hoặc token version theo user.
- Lưu token phía client: localStorage (dễ bị XSS đọc) vs cookie HttpOnly (cần chống CSRF) — trade-off.
- Lỗi hay gặp: không kiểm tra `exp`, chấp nhận `alg: none`, secret yếu/commit lên git, token quá dài hạn.

**Code tay:** `JwtAuthenticationFilter extends OncePerRequestFilter`; endpoint `/auth/login`, `/auth/refresh` (rotation), `/auth/logout` (revoke).

> **⚠️ 2 Bẫy JWT & Refresh Token trong phỏng vấn Bảo mật (Bank / Fintech):**
> 1. **Refresh Token Reuse Detection (Token Family)**:
>    - Khi dùng Refresh Token Rotation, mỗi lần `/auth/refresh` sẽ tạo Refresh Token mới (`RT_2`) và đánh dấu `RT_1` là `REVOKED` (nhưng vẫn giữ bản ghi `RT_1` trong Redis/DB theo cùng `family_id`).
>    - Nếu kẻ tấn công đánh cắp được `RT_1` và đem gọi `/auth/refresh` sau khi người dùng thật đã xoay sang `RT_2` (hoặc ngược lại): Server thấy một token đã `REVOKED` được gửi lên → phát hiện ngay có rò rỉ → **lập tức thu hồi toàn bộ token thuộc `family_id` đó**, bắt đăng nhập lại!
> 2. **Bẫy Algorithm Confusion (`alg: none` và `RS256 → HS256`)**:
>    - Khi parse JWT bằng thư viện `jjwt` / `Nimbus`, phải dùng `parseSignedClaims()` (cấm tuyệt đối `alg: none` — không chữ ký) và cố định thuật toán kỳ vọng (tránh việc hacker lấy Public Key của RS256 làm Secret đối xứng của HS256 để tự ký token giả).

**Tự kiểm tra:** JWT vs session? Refresh token để đâu, rotation là gì? Logout với JWT làm thế nào?

---

## Ngày 39 — Thứ 5 (17/12): Authorization + OAuth2/OIDC
**Kiến thức phải nắm:**
- **RBAC**: Role vs Authority (`ROLE_` prefix), `hasRole` vs `hasAuthority`; `@EnableMethodSecurity` + `@PreAuthorize("hasRole('ADMIN') or #userId == authentication.principal.id")`.
- Kiểm tra quyền sở hữu tài nguyên (ownership) ở service layer.
- **OAuth2** roles: Resource Owner, Client, Authorization Server, Resource Server. Flow: **Authorization Code + PKCE** (web/mobile), Client Credentials (service-to-service). Implicit/Password đã lỗi thời.
- **OIDC** = OAuth2 + ID Token (xác thực). Keycloak là gì; Spring `oauth2-resource-server` verify JWT qua JWKS.
- API key cho đối tác.

**Code tay:** phân quyền USER/ADMIN; USER chỉ xem đơn của mình; (tùy chọn 1h) chạy Keycloak bằng Docker, cấu hình app làm resource server.

**Tự kiểm tra:** OAuth2 vs OIDC? Authorization Code + PKCE hoạt động thế nào? Service gọi service xác thực bằng gì?

**LeetCode (20'):** Daily Temperatures (monotonic stack), Min Stack.

---

## Ngày 40 — Thứ 6 (18/12): Unit Test với JUnit 5 + Mockito
**Kiến thức phải nắm:**
- **Test pyramid**: nhiều unit, vừa integration, ít E2E. Unit vs integration vs E2E.
- JUnit 5: `@Test`, `@BeforeEach`, `@ParameterizedTest` (`@CsvSource`, `@MethodSource`), `@Nested`, `@DisplayName`, `assertThrows`, AssertJ.
- Mockito: `@Mock`, `@InjectMocks`, `when/thenReturn/thenThrow`, `verify(times)`, `ArgumentCaptor`, `@Spy`; mock vs stub vs spy vs fake; đừng mock thứ mình không sở hữu quá đà.
- Pattern Given–When–Then / AAA; test edge case (null, rỗng, biên, exception, concurrency).
- Code coverage (JaCoCo) — coverage cao ≠ test tốt.

**Code tay:** unit test cho `OrderService` (tạo đơn thành công, hết hàng, sản phẩm không tồn tại, áp mã giảm giá biên), `JwtService` (token hết hạn, chữ ký sai).

**Tự kiểm tra:** Mock vs spy? Test pyramid? Unit test của bạn test cái gì, không test cái gì?

---

## Ngày 41 — Thứ 7 (19/12): Integration test + Testcontainers — Lab 4–5h
**Kiến thức phải nắm:**
- Test slice: `@WebMvcTest` (+ `MockMvc`, `@MockitoBean` — thay `@MockBean` từ Boot 3.4), `@DataJpaTest`, `@SpringBootTest` (full context, chậm).
- **Testcontainers**: Postgres/Redis/Kafka thật trong Docker; `@ServiceConnection` (Boot 3.1+); vì sao tốt hơn H2 (khác dialect, khác hành vi lock/JSONB).
- Test security: `@WithMockUser`, test endpoint với JWT.
- Test idempotent, test transaction rollback; dữ liệu test độc lập giữa các test.

**Lab:**
- Hoàn thiện auth JWT + refresh + RBAC trong shop-order-platform.
- Integration test: đăng nhập → tạo đơn → xem đơn người khác bị 403; repository test với Testcontainers Postgres; test chống oversell với 2 thread.
- Bật JaCoCo, chạy `mvn verify`.

> **⚠️ Bẫy gắn `@Transactional` lên hàm `@Test` trong Integration Test (EPAM / KMS hay hỏi):**
> - Khi bạn gắn `@Transactional` lên một hàm `@Test` (hoặc dùng `@DataJpaTest` có sẵn `@Transactional`), toàn bộ hàm test chạy trong **cùng 1 Persistence Context mở xuyên suốt** và tự rollback khi kết thúc.
> - **3 Hậu quả nguy hiểm**:
>   1. **Che giấu lỗi `LazyInitializationException`**: Trong hàm test vẫn gọi được `order.getItems().size()` ngon lành vì Session vẫn mở, nhưng lên Production tắt `open-in-view` là nổ lỗi ngay!
>   2. **Che giấu lỗi thiếu `flush()`**: Nếu không gọi `entityManager.flush(); entityManager.clear();` trong test, câu lệnh `findById()` chỉ đọc lại object từ L1 Cache trên RAM chứ không hề bắn câu `SELECT` xuống Postgres để kiểm tra constraint!
>   3. **Không test được đa luồng / `TestRestTemplate` / `WebTestClient`**: Vì dữ liệu của hàm `@Test` chưa commit xuống DB nên các thread khác (như 2 thread test chống oversell hoặc Tomcat worker thread thật) sẽ **không nhìn thấy dữ liệu bạn vừa `save()` trong hàm test**!

**LeetCode (30'):** Rotting Oranges, Clone Graph.

---

## Ngày 42 — Chủ nhật (20/12): Review + luyện miệng
**10 câu phỏng vấn của tuần:**
1. Authentication vs authorization; luồng Spring Security filter chain.
2. JWT cấu trúc thế nào, HS256 vs RS256?
3. JWT vs session — khi nào dùng cái nào?
4. Refresh token lưu ở đâu, rotation, logout/revoke JWT?
5. OAuth2 vs OIDC; Authorization Code + PKCE.
6. CORS và CSRF khác nhau thế nào?
7. SQL injection & IDOR — phòng tránh trong Spring.
8. BCrypt, vì sao không dùng MD5/SHA?
9. Unit vs integration test; mock vs spy.
10. Testcontainers giải quyết vấn đề gì so với H2?

**Checklist:** ☐ Auth JWT + refresh + RBAC chạy ☐ Integration test với Testcontainers xanh ☐ Note tuần 6 ☐ Chấm lại skill Security

