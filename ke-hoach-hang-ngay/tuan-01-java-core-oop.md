# Tuần 1 (09/11 – 15/11/2026): Java Core nền + OOP + SOLID

> Mục tiêu tuần: nói trôi chảy về "Java chạy thế nào, object nằm ở đâu, OOP/SOLID là gì" — đây là 30% câu hỏi vòng 1 phỏng vấn Middle, và là nơi người "lâu không ôn" hay rớt nhất.
> Format mỗi ngày: **Lý thuyết (45')** → **Code tay (75')** → **Ghi chú + tự hỏi đáp (30')**.

---

## Ngày 1 — Thứ 2 (09/11): Kiểu dữ liệu, bộ nhớ, pass-by-value
**Kiến thức phải nắm:**
- 8 kiểu primitive (byte, short, int, long, float, double, char, boolean) — kích thước, giá trị mặc định; wrapper class (Integer, Long…).
- Autoboxing/unboxing; **Integer cache -128..127** (vì sao `Integer a=127,b=127; a==b` true nhưng 128 lại false); NPE khi unbox `null`.
- Stack vs Heap: biến local + tham chiếu nằm ở stack (mỗi thread 1 stack), object nằm ở heap (chung).
- **Java luôn pass-by-value** — với object thì giá trị được copy là *tham chiếu*. Đổi field thì thấy, gán lại tham chiếu thì không.
- `==` so sánh tham chiếu/giá trị primitive, `equals()` so sánh nội dung.
- Ép kiểu: widening/narrowing, tràn số `int` (Integer.MAX_VALUE + 1), vì sao tiền dùng `BigDecimal` chứ không dùng `double` (0.1 + 0.2).

**Code tay:**
- Viết hàm `swap(Integer a, Integer b)` và `changeName(User u)` rồi giải thích kết quả.
- Demo Integer cache, demo `BigDecimal` với `new BigDecimal("0.1")` vs `new BigDecimal(0.1)` vs `BigDecimal.valueOf(0.1)`, `setScale`, `RoundingMode.HALF_UP`.

> **⚠️ Bẫy phỏng vấn thực chiến (Fintech/Bank cực hay hỏi):**
> - `new BigDecimal(0.1)` nhận vào `double` đã bị sai số nhị phân (`0.10000000000000000555...`), còn `new BigDecimal("0.1")` hoặc `BigDecimal.valueOf(0.1)` mới chính xác.
> - `new BigDecimal("2.0").equals(new BigDecimal("2.00"))` trả về **`false`** (vì `equals` so cả `scale`), phải dùng **`compareTo() == 0`** khi so sánh giá trị tiền!
> - `BigDecimal.divide()` nếu thương số vô hạn tuần hoàn (`1 / 3`) mà không truyền ` scale` + `RoundingMode` sẽ ném `ArithmeticException: Non-terminating decimal expansion`.

**Tự kiểm tra:**
1. Java pass-by-value hay pass-by-reference? Chứng minh bằng code.
2. Vì sao không dùng `double` cho tiền? `BigDecimal.equals` khác `compareTo` thế nào (2.0 vs 2.00)?
3. Primitive có thể null không? Khi nào unboxing gây NPE trong code Spring thực tế?

---

## Ngày 2 — Thứ 3 (10/11): String, immutability, static/final
**Kiến thức phải nắm:**
- String **immutable**: vì sao (bảo mật, thread-safe, cache hashCode, String Pool).
- **String Pool**: `"abc"` vs `new String("abc")`, `intern()`, String pool nằm trong heap từ Java 7.
- `String` vs `StringBuilder` vs `StringBuffer` (synchronized); nối chuỗi trong vòng lặp.
- Các method hay dùng: `split`, `substring`, `strip` vs `trim`, `isBlank`, `String.join`, text block `"""` (Java 15).
- `final` cho biến/method/class; final reference ≠ object bất biến.
- `static`: biến class, static method, static block, static nested class vs inner class (inner class giữ tham chiếu outer → có thể memory leak).
- Cách tự viết 1 class immutable: `final class`, field `private final`, không setter, defensive copy cho List/Date.

**Code tay:**
- Viết class `Money` immutable (amount BigDecimal + currency), có `add()` trả object mới.
- Benchmark thô: nối 100.000 chuỗi bằng `+` vs `StringBuilder` (đo `System.nanoTime`).

**Tự kiểm tra:**
1. `String s = new String("hello")` tạo mấy object?
2. Vì sao String là immutable? Làm sao tạo 1 class immutable của riêng mình?
3. `final List<String> list` có thêm phần tử được không?

**LeetCode (20'):** Valid Anagram, Reverse String.

---

## Ngày 3 — Thứ 4 (11/11): OOP 4 tính chất + interface vs abstract class
**Kiến thức phải nắm:**
- **Encapsulation, Inheritance, Polymorphism, Abstraction** — mỗi cái 1 ví dụ thực tế trong code nghiệp vụ (không lấy ví dụ con mèo con chó).
- Overloading (compile-time) vs Overriding (runtime, dynamic dispatch); quy tắc override (access modifier không hẹp hơn, exception không rộng hơn, covariant return).
- `abstract class` vs `interface` (Java 8+: default/static method; Java 9: private method). Khi nào chọn cái nào.
- Composition over inheritance — vì sao kế thừa sâu là mùi code.
- Access modifier: private / default (package) / protected / public.
- `Object` class: `equals`, `hashCode`, `toString`, `getClass`, `clone`, `wait/notify`.
- Constructor chaining, `this()` / `super()`, thứ tự khởi tạo (static block → instance block → constructor, cha trước con).

**Code tay:**
- Mô hình `PaymentMethod` (interface) với `CreditCardPayment`, `MomoPayment`, `CodPayment` → thể hiện polymorphism; viết `PaymentService.pay(PaymentMethod m)`.
- Viết 1 ví dụ thứ tự khởi tạo cha/con có static block, in log ra để tự kiểm chứng.

**Tự kiểm tra:**
1. Abstract class khác interface thế nào sau Java 8? Khi nào chọn cái nào?
2. Có override được static method / private method không? Vì sao?
3. Cho ví dụ polymorphism trong dự án bạn từng làm.

---

## Ngày 4 — Thứ 5 (12/11): equals/hashCode, Comparable/Comparator, Exception
**Kiến thức phải nắm:**
- Hợp đồng `equals/hashCode`: equals bằng nhau ⇒ hashCode bằng nhau; vi phạm thì HashMap/HashSet "mất" phần tử.
- Đừng dùng field mutable trong hashCode của key HashMap; vấn đề equals/hashCode với Entity JPA (id null trước khi persist).
- `Comparable` (thứ tự tự nhiên) vs `Comparator` (`Comparator.comparing(...).thenComparing(...).reversed()`).
- Exception hierarchy: `Throwable` → `Error` / `Exception` → checked vs unchecked (`RuntimeException`).
- try-catch-finally, **try-with-resources** + `AutoCloseable`, suppressed exception; `finally` có `return` thì chuyện gì xảy ra.
- Best practice: không nuốt exception, không catch `Exception` bừa, custom exception nghiệp vụ (`OrderNotFoundException`), giữ cause khi wrap.
- Liên hệ Spring: `@Transactional` mặc định chỉ rollback với unchecked exception.

**Code tay:**
- Class `Product` có/không override hashCode → bỏ vào HashSet, chứng minh lỗi.
- Sắp xếp list Order theo status rồi createdAt giảm dần bằng Comparator.
- Viết hierarchy exception nghiệp vụ: `BusinessException` → `OrderNotFoundException`, `InsufficientStockException`.

> **⚠️ 2 Bẫy `equals/hashCode` & `HashMap` kinh điển (EPAM / KMS / Bank):**
> 1. **Bẫy không override `equals/hashCode`**: Tạo class `User(String name)` không override `equals/hashCode`, thực hiện `for (int i=0; i<100; i++) map.put(new User("Alice"), i);` → Hỏi `map.size()` bằng bao nhiêu?
>    - **Đáp án**: **`100`** (nhiều người trả lời nhầm là 1!). Vì mặc định `Object.hashCode()` trả địa chỉ/identity hash khác nhau và `Object.equals()` so sánh `==` tham chiếu. Gọi `map.get(new User("Alice"))` sẽ trả về `null`.
> 2. **Bẫy dùng Mutable Object làm Key**: Đã override `equals/hashCode` theo field `name`. Sau khi `map.put(user, "ADMIN")`, ta gọi `user.setName("Bob")`.
>    - **Hậu quả**: `map.get(user)` tính lại hash theo `"Bob"` → tìm sai bucket → trả về `null`. Trong khi đó entry cũ vẫn nằm kẹt vĩnh viễn ở bucket của `"Alice"` → **Memory Leak**. Do đó key của `HashMap` bắt buộc phải **immutable** (`String`, `Integer`, `UUID`, `record`).
> 3. **Bẫy `equals/hashCode` trên JPA `@Entity`**: Không dùng `@Data` của Lombok trên `@Entity` vì `@GeneratedValue` ID = `null` lúc `Transient`, sau khi `persist()` mới có ID → đổi `hashCode` làm mất phần tử trong `Set`. Giải pháp: dùng business key (natural key) hoặc chỉ so sánh `id != null && id.equals(other.id)` kèm cố định `hashCode() = getClass().hashCode()`.

**Tự kiểm tra:**
1. Override equals mà không override hashCode thì sao?
2. Checked vs unchecked, khi nào dùng loại nào? Vì sao Spring ưa unchecked?
3. `finally` có luôn chạy không? (System.exit, JVM crash).

**LeetCode (20'):** Two Sum (dùng HashMap), Contains Duplicate.

---

## Ngày 5 — Thứ 6 (13/11): SOLID + Clean Code
**Kiến thức phải nắm:**
- **S**ingle Responsibility, **O**pen/Closed, **L**iskov Substitution, **I**nterface Segregation, **D**ependency Inversion — mỗi nguyên tắc: định nghĩa 1 câu + ví dụ vi phạm + cách sửa.
- Ví dụ kinh điển: LSP với Rectangle/Square; OCP với `if (type == ...)` → Strategy; DIP là nền của Spring DI.
- DRY, KISS, YAGNI; code smell: God class, long method, magic number, feature envy.
- Đặt tên, hàm ngắn, guard clause thay vì if lồng nhau.

**Code tay:**
- Lấy 1 class "OrderService làm tất cả" (tự viết bản xấu ~100 dòng: validate, tính giá, giảm giá theo `if`, lưu, gửi mail) → refactor theo SRP + OCP + DIP. Commit 2 phiên bản để so sánh.

**Tự kiểm tra:**
1. Giải thích SOLID trong 2 phút, mỗi chữ 1 ví dụ từ dự án thật.
2. DIP khác DI khác IoC thế nào?
3. Vi phạm LSP thì hậu quả gì trong code thực tế?

---

## Ngày 6 — Thứ 7 (14/11): Lab lớn 4–5h — "Mini Order Engine" thuần Java
**Làm (không dùng Spring):**
- Domain: `Product`, `Customer`, `Order`, `OrderItem`, `Money` (immutable).
- Áp dụng: interface + polymorphism (PaymentMethod), custom exception, Comparator, equals/hashCode đúng, SOLID.
- Chức năng: tạo đơn, tính tổng tiền có giảm giá (Strategy cho 3 loại giảm giá), kiểm tra tồn kho, in hóa đơn.
- Viết 5–10 unit test JUnit 5 cơ bản (chuẩn bị cho tuần 6).
- Đẩy lên GitHub qua PR, commit theo convention.

**LeetCode (30'):** Valid Parentheses, Merge Two Sorted Lists.

---

## Ngày 7 — Chủ nhật (15/11): Review + luyện miệng
- Viết `tuan-01-java-core.md` theo format: Khái niệm → Code → Lỗi gặp → Câu hỏi.
- Tự ghi âm trả lời 10 câu dưới (mỗi câu ≤ 2 phút), nghe lại, sửa câu dài dòng.
- Cập nhật `skill-audit.md` dòng Java Core.

**10 câu phỏng vấn của tuần:**
1. JDK, JRE, JVM khác nhau thế nào? Java chạy từ `.java` tới mã máy ra sao (JIT)?
2. Stack và Heap chứa gì? Biến local kiểu object nằm ở đâu?
3. Java pass-by-value hay reference?
4. Vì sao String immutable? String Pool là gì?
5. String vs StringBuilder vs StringBuffer.
6. Abstract class vs interface.
7. Overloading vs overriding.
8. Hợp đồng equals/hashCode.
9. Checked vs unchecked exception; try-with-resources.
10. SOLID với ví dụ thực tế.

**Checklist cuối tuần:** ☐ 10 câu nói trôi chảy ☐ Mini Order Engine trên GitHub ☐ 6 bài LeetCode easy ☐ Note tuần 1

