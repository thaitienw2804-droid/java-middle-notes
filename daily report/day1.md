# Báo Cáo Học Tập Ngày 1 (09/11/2026): Kiểu Dữ Liệu, Bộ Nhớ & Pass-by-Value

---

## 1. Kiến thức cốt lõi

### 1.1. 8 Kiểu dữ liệu nguyên thủy (Primitive) vs Wrapper Class

#### Bảng 8 kiểu Primitive trong Java
> **Lưu ý**: Giá trị mặc định chỉ áp dụng cho **biến instance / static** (lưu trên Heap/Metaspace). **Biến local** (lưu trên Stack) không có giá trị mặc định — nếu dùng mà chưa khởi tạo sẽ báo lỗi biên dịch (Compile Error).

| Kiểu dữ liệu | Kích thước | Giá trị mặc định | Phạm vi / Ghi chú | Wrapper Class |
|---|---|---|---|---|
| `byte` | 1 byte (8-bit) | `0` | -128 đến 127 | `Byte` |
| `short` | 2 bytes (16-bit) | `0` | -32,768 đến 32,767 | `Short` |
| `int` | 4 bytes (32-bit) | `0` | -2^31 đến 2^31 - 1 (~2.1 tỷ) | `Integer` |
| `long` | 8 bytes (64-bit) | `0L` | -2^63 đến 2^63 - 1 | `Long` |
| `float` | 4 bytes (32-bit) | `0.0f` | Số thực IEEE 754 đơn | `Float` |
| `double` | 8 bytes (64-bit) | `0.0d` | Số thực IEEE 754 kép | `Double` |
| `char` | 2 bytes (16-bit) | `'\u0000'` | Ký tự Unicode (0 đến 65,535) | `Character` |
| `boolean` | 1 bit logic (JVM thường cấp 1 byte) | `false` | `true` / `false` | `Boolean` |

#### So sánh Primitive vs Wrapper Class
| Tiêu chí | Primitive (`int`, `long`, `double`...) | Wrapper Class (`Integer`, `Long`, `Double`...) |
|---|---|---|
| **Bản chất** | Kiểu dữ liệu nguyên thủy, không phải Object, không có method đi kèm. | Là Object (kế thừa `Object`), bất biến (`Immutable`), có nhiều hàm tiện ích (`parseInt`, `compareTo`...). |
| **Giá trị `null`** | **Không thể chứa `null`**. | **Có thể chứa `null`** (đại diện cho trạng thái "chưa có dữ liệu"). |
| **Vùng nhớ lưu trữ** | Biến local nằm trên **Stack**; nếu là field của class thì nằm phẳng (inline) bên trong Object trên **Heap**. | Biến tham chiếu (reference pointer) nằm trên **Stack**, còn bản thân đối tượng nằm trên **Heap** (tốn thêm bộ nhớ Object Header). |
| **So sánh `==`** | So sánh trực tiếp **giá trị**. | So sánh **địa chỉ ô nhớ (reference)** của 2 đối tượng (trừ khi 1 vế là primitive thì vế kia bị unbox). |
| **Khi nào sử dụng?** | - Các biến cục bộ tính toán, vòng lặp (`for`), xử lý hiệu năng cao.<br>- Các trường nghiệp vụ bắt buộc luôn có giá trị (`not null`). | - Dùng trong **Collections & Generics** (`List<Integer>`, `Map<Long, User>`).<br>- **Entity Database / ORM (JPA)** & **DTO** (cần phân biệt `0` với `null` — chưa truyền lên hoặc cột DB cho phép `NULL`). |

---

### 1.2. Autoboxing & Unboxing — Nguy cơ `NullPointerException` (NPE)

- **Autoboxing (Primitive $\rightarrow$ Wrapper)**:
  - Khi gán `Integer i = 10;`, trình biên dịch (`javac`) tự động chuyển thành:
    ```java
    Integer i = Integer.valueOf(10); // KHÔNG phải new Integer(10) (đã deprecated từ Java 9)
    ```
- **Unboxing (Wrapper $\rightarrow$ Primitive)**:
  - Khi thực hiện phép toán hoặc gán vào biến nguyên thủy, ví dụ:
    ```java
    Integer x = 5, y = 10;
    int c = x + y;
    ```
  - Trình biên dịch tự động chuyển thành lời gọi hàm **`intValue()`** (hoặc `longValue()`, `doubleValue()`...):
    ```java
    int c = x.intValue() + y.intValue();
    ```
- **Nguy cơ NPE khi Unbox `null`**:
  - Vì bản chất Unboxing là gọi `x.intValue()`, nếu biến Wrapper `x` đang mang giá trị `null`, JVM sẽ thực thi `null.intValue()` và ném ra **`NullPointerException`** ngay lập tức.

---

### 1.3. Integer Cache `[-128..127]`

- **Cơ chế hoạt động**:
  - Bên trong class `Integer` có một static inner class là `IntegerCache`, khởi tạo sẵn một mảng `cache[]` chứa các đối tượng `Integer` từ **`-128` đến `127`** ngay khi class được nạp vào bộ nhớ.
  - Khi gọi `Integer.valueOf(i)` (hoặc khi Autoboxing):
    - Nếu `-128 <= i <= 127`: Trả về ngay tham chiếu tới đối tượng có sẵn trong mảng `cache[]` mà **không cấp phát thêm bộ nhớ mới**.
    - Nếu nằm ngoài khoảng `[-128, 127]`: Khởi tạo đối tượng mới `new Integer(i)` trên **Heap**.
  - Lưu ý: `new Integer(127)` luôn ép tạo object mới trên Heap và không dùng cache.

- **Vì sao `Integer a = 127, b = 127; a == b` là `true` nhưng `128` lại là `false`?**
  ```java
  Integer a = 127; // Integer.valueOf(127) -> lấy từ IntegerCache
  Integer b = 127; // Integer.valueOf(127) -> lấy cùng object trong IntegerCache
  System.out.println(a == b); // true (cùng trỏ tới 1 địa chỉ ô nhớ trong cache)

  Integer x = 128; // Integer.valueOf(128) -> ngoài cache -> tạo Object #1 trên Heap
  Integer y = 128; // Integer.valueOf(128) -> ngoài cache -> tạo Object #2 trên Heap
  System.out.println(x == y); // false (2 đối tượng khác địa chỉ ô nhớ trên Heap)
  System.out.println(x.equals(y)); // true (so sánh giá trị nội dung bên trong: 128 == 128)
  ```
  > **Quy tắc xương máu**: So sánh giá trị của các kiểu Wrapper (`Integer`, `Long`...) bắt buộc dùng **`.equals()`** hoặc **`Objects.equals(a, b)`**, tuyệt đối không dùng `==`.

---

### 1.4. Bộ nhớ Stack vs Heap

- **Stack**:
  - Mỗi Thread có **1 vùng nhớ Stack riêng biệt** (Thread-safe tự nhiên đối với biến local).
  - Lưu trữ các **Stack Frame** cho mỗi lời gọi hàm, bao gồm: biến nguyên thủy cục bộ (local primitive) và **biến tham chiếu (reference pointer)**.
- **Heap**:
  - Vùng nhớ **dùng chung cho tất cả các Thread**.
  - Lưu trữ toàn bộ các **Đối tượng (Object)** (bao gồm cả các trường dữ liệu bên trong Object đó).
- **Chuyện gì xảy ra khi một method kết thúc?**
  1. **Stack Frame** của hàm đó bị **pop (xóa) khỏi Stack ngay lập tức**.
  2. Mọi biến nguyên thủy cục bộ và **biến tham chiếu (con trỏ)** trong hàm đó biến mất ngay lập tức.
  3. **Đối tượng (Object) nằm trên Heap** mà biến tham chiếu đó từng trỏ tới vẫn tồn tại tạm thời trên Heap; nếu không còn bất kỳ tham chiếu nào khác trỏ tới nó nữa (Unreachable), nó sẽ được **Garbage Collector (GC)** thu hồi trong đợt dọn rác tiếp theo.

---

### 1.5. Ép kiểu (Casting), Tràn số (Overflow) & `BigDecimal` cho tiền tệ

- **Ép kiểu**:
  - **Widening (Tự động / Ngầm định)**: Từ kiểu nhỏ sang kiểu lớn hơn (`byte` $\rightarrow$ `short` $\rightarrow$ `int` $\rightarrow$ `long` $\rightarrow$ `float` $\rightarrow$ `double`), không làm mất mát phạm vi.
  - **Narrowing (Tường minh)**: Từ kiểu lớn về kiểu nhỏ (`int a = (int) 10L;`), có nguy cơ mất phần thập phân hoặc tràn số.
- **Tràn số (`Integer Overflow`)**:
  - Trong Java, `Integer.MAX_VALUE + 1` **không ném lỗi** mà bị tràn bit dấu (two's complement wrap-around) thành `Integer.MIN_VALUE` (`-2147483648`).
  - Trong nghiệp vụ tài chính/tính toán quan trọng, dùng `long` hoặc dùng **`Math.addExact(a, b)` / `Math.multiplyExact(a, b)`** để tự động ném `ArithmeticException` khi bị tràn số.
- **Vì sao tiền tệ dùng `BigDecimal` chứ không dùng `double`?**
  - `double` lưu trữ theo chuẩn nhị phân dấu phẩy động IEEE 754, không thể biểu diễn chính xác các phân số thập phân như `0.1` hay `0.2` (trong hệ nhị phân chúng là số thập phân vô hạn tuần hoàn). Do đó `0.1 + 0.2 = 0.30000000000000004`.
  - Tiền tệ yêu cầu độ chính xác tuyệt đối đến từng xu và quy tắc làm tròn (`RoundingMode`) minh bạch, nên bắt buộc dùng `BigDecimal`.

> ### ⚠️ 3 Bẫy `BigDecimal` Thực Chiến (Fintech / Ngân hàng cực hay hỏi)
> 1. **Bẫy Constructor**:
>    - ❌ **Sai**: `new BigDecimal(0.1)` — vì `0.1` truyền vào là kiểu `double` vốn đã bị sai số nhị phân (`0.100000000000000005551115...`).
>    - ✅ **Đúng**: Luôn dùng **`new BigDecimal("0.1")`** (String constructor) hoặc **`BigDecimal.valueOf(0.1)`**.
> 2. **Bẫy so sánh `equals()` vs `compareTo()`**:
>    - `new BigDecimal("2.0").equals(new BigDecimal("2.00"))` trả về **`false`** vì `equals()` so sánh cả **giá trị (`unscaled value`)** lẫn **số chữ số sau dấu phẩy (`scale`: 1 vs 2)**.
>    - ✅ Khi so sánh giá trị tiền tệ, bắt buộc dùng **`a.compareTo(b) == 0`**.
> 3. **Bẫy phép chia `divide()` không truyền `RoundingMode`**:
>    - Gọi `new BigDecimal("1").divide(new BigDecimal("3"))` sẽ nổ lỗi **`ArithmeticException: Non-terminating decimal expansion`** vì kết quả `0.3333...` là vô hạn tuần hoàn.
>    - ✅ Luôn truyền `scale` và `RoundingMode`: `a.divide(b, 2, RoundingMode.HALF_UP)`.

---

## 2. Code tay thực hành (Chứng minh lý thuyết)

```java
import java.math.BigDecimal;
import java.math.RoundingMode;

public class Day1Practice {

    static class User {
        String name;
        User(String name) { this.name = name; }
    }

    // 1. Chứng minh Pass-by-Value với Wrapper (Immutable)
    static void swap(Integer a, Integer b) {
        Integer temp = a;
        a = b;
        b = temp;
        // Chỉ hoán đổi 2 bản copy của tham chiếu nằm trong Stack Frame của hàm swap
    }

    // 2. Chứng minh Pass-by-Value với Object (Đổi field vs Gán lại tham chiếu)
    static void modifyUser(User u) {
        u.name = "Bob"; // Thay đổi trạng thái của Object gốc trên Heap -> Bên ngoài THẤY
        u = new User("Charlie"); // Trỏ biến local u sang Object mới -> Bên ngoài KHÔNG bị ảnh hưởng
        u.name = "David";
    }

    public static void main(String[] args) {
        // --- Demo 1: Pass-by-value ---
        Integer x = 1, y = 2;
        swap(x, y);
        System.out.println("Sau swap: x = " + x + ", y = " + y); // Vẫn là x = 1, y = 2

        User user = new User("Alice");
        modifyUser(user);
        System.out.println("Tên user sau modifyUser: " + user.name); // Kết quả: "Bob" (không phải Charlie hay David)

        // --- Demo 2: Integer Cache [-128..127] ---
        Integer c1 = 127, c2 = 127;
        Integer c3 = 128, c4 = 128;
        System.out.println("127 == 127: " + (c1 == c2));         // true (cùng đối tượng trong cache)
        System.out.println("128 == 128: " + (c3 == c4));         // false (2 đối tượng khác nhau trên Heap)
        System.out.println("128 equals 128: " + c3.equals(c4));  // true

        // --- Demo 3: BigDecimal & 3 bẫy thực chiến ---
        System.out.println("0.1 + 0.2 (double) = " + (0.1 + 0.2)); // 0.30000000000000004
        System.out.println("new BigDecimal(0.1) = " + new BigDecimal(0.1)); // Sai số dài
        System.out.println("new BigDecimal(\"0.1\") = " + new BigDecimal("0.1")); // 0.1 chính xác

        BigDecimal b1 = new BigDecimal("2.0");
        BigDecimal b2 = new BigDecimal("2.00");
        System.out.println("2.0 equals 2.00: " + b1.equals(b2));               // false (khác scale)
        System.out.println("2.0 compareTo 2.00 == 0: " + (b1.compareTo(b2) == 0)); // true

        BigDecimal one = new BigDecimal("1");
        BigDecimal three = new BigDecimal("3");
        BigDecimal result = one.divide(three, 2, RoundingMode.HALF_UP);
        System.out.println("1 / 3 (HALF_UP, scale 2) = " + result); // 0.33
    }
}
```

---

## 3. Tự kiểm tra (Trả lời phỏng vấn)

### Câu 1: Java là pass-by-value hay pass-by-reference? Chứng minh bằng code.
- **Trả lời**:
  - Java **100% là pass-by-value** (truyền tham trị) đối với cả kiểu nguyên thủy lẫn Object.
  - Đối với Object, giá trị được copy truyền vào tham số của hàm là **giá trị của con trỏ tham chiếu (value of the reference)** — tức là cả biến ngoài hàm và tham số trong hàm cùng trỏ vào một địa chỉ Object trên Heap.
  - **Hệ quả thực tế**:
    1. Nếu trong hàm ta gọi phương thức làm thay đổi thuộc tính của Object (ví dụ `user.setName("Bob")`), thì Object gốc trên Heap bị thay đổi $\rightarrow$ bên ngoài hàm **thấy sự thay đổi đó**.
    2. Nhưng nếu trong hàm ta gán lại tham số sang một Object khác (`user = new User("Charlie")` hoặc viết hàm `swap(a, b)`), ta chỉ đang đổi hướng trỏ của biến bản sao nằm trên Stack Frame của hàm đó. Khi hàm kết thúc, Stack Frame bị xóa, biến gốc của caller bên ngoài **hoàn toàn không đổi**.

---

### Câu 2: Vì sao không dùng `double` cho tiền? `BigDecimal.equals` khác `compareTo` thế nào (`2.0` vs `2.00`)?
- **Trả lời**:
  - Không dùng `double` cho tiền tệ vì `double` dùng hệ nhị phân dấu phẩy động (IEEE 754), không thể biểu diễn chính xác các số thập phân như `0.1`, `0.2`, `0.3` (chúng bị lặp vô hạn tuần hoàn trong hệ nhị phân). Khi cộng dồn nhiều giao dịch sẽ gây lệch tiền.
  - `BigDecimal` lưu số dưới dạng hệ cơ số 10 (`unscaledValue * 10^-scale`), giữ độ chính xác tuyệt đối. Khi khởi tạo bắt buộc dùng `new BigDecimal("0.1")` hoặc `BigDecimal.valueOf(0.1)`.
  - **Sự khác biệt giữa `equals()` và `compareTo()`**:
    - `BigDecimal.equals()` so sánh cả **giá trị (`value`)** lẫn **độ dài phần thập phân (`scale`)**, nên `new BigDecimal("2.0").equals(new BigDecimal("2.00"))` trả về **`false`**.
    - `BigDecimal.compareTo()` chỉ so sánh **giá trị toán học thuần túy** (bỏ qua khác biệt về `scale`), nên `new BigDecimal("2.0").compareTo(new BigDecimal("2.00")) == 0` trả về **`true`**. Trong nghiệp vụ so sánh số tiền, luôn phải dùng `compareTo() == 0`.

---

### Câu 3: Primitive có thể `null` không? Khi nào unboxing gây NPE trong code Spring Boot thực tế?
- **Trả lời**:
  - Kiểu `primitive` **không bao giờ có thể `null`** (nó luôn mang giá trị bit thực tế).
  - Trong dự án Spring Boot thực tế, lỗi `NullPointerException` do Unboxing ngầm định (`xxx.intValue()`, `xxx.booleanValue()`) rất hay xảy ra ở **4 tình huống sau**:
    1. **Map dữ liệu từ Database qua JPA/Hibernate**: Cột trong bảng DB cho phép `NULL` (ví dụ `discount_rate` đang là `NULL`), nhưng field hoặc hàm getter trong Entity/Projection lại khai báo kiểu nguyên thủy `int` hoặc `double` $\rightarrow$ Hibernate hoặc code nghiệp vụ gọi getter sẽ unbox `null` gây nổ NPE.
    2. **Kiểm tra điều kiện trên DTO / Request (`@RequestBody`, `@RequestParam`)**: Client gửi JSON thiếu trường `status` hoặc `isActive` (`Boolean isActive = null` trong DTO), nhưng ở Service viết trực tiếp `if (request.getIsActive())` hoặc `if (dto.getQuantity() > 0)` $\rightarrow$ Java tự gọi `request.getIsActive().booleanValue()` trên `null` và ném NPE.
    3. **Hàm Aggregate (`SUM`, `AVG`, `MAX`) trong Spring Data JPA `@Query`**: Khi bảng không có bản ghi nào thỏa mãn điều kiện `WHERE`, câu SQL `SELECT SUM(o.totalAmount) FROM Order o` trả về `NULL`. Nếu method trong Repository khai báo kiểu trả về là `long sumTotalAmount()` thay vì `Long` (hoặc dùng `COALESCE(..., 0)`), Spring sẽ unbox `null` sang `long` và nổ NPE.
    4. **Toán tử 3 ngôi (Ternary operator `?:`)**: Viết `Integer result = flag ? null : 0;` — vì vế thứ 3 là kiểu nguyên thủy `0` (`int`), Java ép kiểu toàn bộ biểu thức 3 ngôi về kiểu `int`, dẫn đến việc tự động gọi `((Integer) null).intValue()` khi `flag == true` và ném NPE!
