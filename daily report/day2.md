# Báo Cáo Học Tập Ngày 2 (10/11/2026): String, Immutability, `static` & `final`

---

## 1. Kiến thức cốt lõi

### 1.1. Tại sao `String` trong Java là Immutable (Bất biến)?

Một đối tượng `Immutable` là đối tượng mà sau khi được khởi tạo, trạng thái (giá trị) của nó **không bao giờ có thể thay đổi**. Mọi thao tác như nối chuỗi (`+`, `concat`), cắt chuỗi (`substring`), thay thế (`replace`) đều **tạo ra một đối tượng `String` mới** trên Heap.

Java thiết kế `String` là Immutable vì **4 lý do cốt lõi**:
1. **Tiết kiệm bộ nhớ nhờ String Pool**:
   - Nhiều biến tham chiếu có thể trỏ chung vào cùng một đối tượng `"abc"` trong String Pool. Nếu `String` có thể bị sửa (mutable), việc thay đổi chuỗi ở một biến sẽ vô tình làm hỏng dữ liệu của tất cả các biến khác đang trỏ chung vào đó.
2. **Bảo mật (Security)**:
   - `String` được dùng rộng rãi để lưu các thông tin nhạy cảm: URL kết nối Database, Username/Password, đường dẫn file hệ thống, Host/Port mạng, và tên Class truyền vào `ClassLoader`.
   - Nếu `String` là mutable, kẻ tấn công (hoặc một đoạn code khác giữ cùng tham chiếu) có thể thay đổi nội dung chuỗi *sau khi* nó đã vượt qua bước kiểm tra quyền bảo mật (Security Check).
3. **An toàn đa luồng tự nhiên (Thread-safe)**:
   - Vì giá trị không bao giờ đổi, nhiều Thread có thể đọc và chia sẻ cùng một biến `String` mà không cần bất kỳ cơ chế đồng bộ (`synchronized`) nào, không bao giờ xảy ra Race Condition.
4. **Tối ưu hiệu năng với HashCode Caching**:
   - `String` là kiểu dữ liệu được dùng làm **Key** phổ biến nhất trong `HashMap` / `HashSet`.
   - Nhờ tính bất biến, `String` chỉ cần tính toán `hashCode()` **đúng 1 lần duy nhất** (khi gọi lần đầu) rồi lưu vào biến `private int hash` để tái sử dụng mãi mãi, giúp thao tác tra cứu trong `HashMap` đạt tốc độ cực nhanh.

> **Mở rộng (Java 9+ Compact Strings)**: Trước Java 9, `String` lưu ký tự bằng mảng `private final char[] value` (2 bytes/ký tự). Từ Java 9+, Java đổi sang `private final byte[] value` kèm biến `byte coder` (LATIN1 tốn 1 byte/ký tự, UTF-16 tốn 2 bytes/ký tự) giúp giảm gần 50% RAM cho các chuỗi tiếng Anh/ASCII.

---

### 1.2. String Pool & Phương thức `intern()`

- **String Literal (`"abc"`) vs `new String("abc")`**:
  - **Khai báo Literal (`String s1 = "abc";`)**: JVM kiểm tra trong **String Pool** trước. Nếu đã có chuỗi `"abc"`, nó trả về ngay tham chiếu có sẵn. Nếu chưa có, nó mới tạo `"abc"` trong Pool rồi trả về tham chiếu.
  - **Khởi tạo bằng `new` (`String s2 = new String("abc");`)**: Luôn ép JVM cấp phát **một đối tượng `String` mới trên Heap (nằm ngoài String Pool)**. Do đó `s1 == s2` sẽ trả về **`false`**, còn `s1.equals(s2)` trả về **`true`**.
- **Phương thức `String.intern()`**:
  - Dùng để đưa/lấy tham chiếu chuẩn từ String Pool: Khi gọi `s2.intern()`, JVM kiểm tra trong Pool đã có chuỗi bằng với `s2` chưa — nếu có thì trả về tham chiếu trong Pool, nếu chưa có thì thêm vào Pool rồi trả về tham chiếu đó.
  - Hệ quả: `new String("abc").intern() == "abc"` trả về **`true`**.
- **Tại sao từ Java 7+, String Pool được chuyển từ PermGen vào Heap?**
  - Trước Java 7, String Pool nằm ở vùng nhớ **PermGen** có kích thước cố định, rất nhỏ và hầu như không được Garbage Collector (GC) dọn dẹp $\rightarrow$ nếu ứng dụng gọi `intern()` nhiều rất dễ nổ lỗi `OutOfMemoryError: PermGen space`.
  - Từ Java 7+, String Pool được dời vào **Heap chính**, giúp các chuỗi trong Pool khi không còn ai tham chiếu tới sẽ được **GC thu hồi bộ nhớ** như các Object thông thường.

---

### 1.3. So sánh `String` vs `StringBuilder` vs `StringBuffer`

| Tiêu chí | `String` | `StringBuilder` (Java 5+) | `StringBuffer` (Java 1.0) |
|---|---|---|---|
| **Tính biến đổi** | **Immutable** (Bất biến) | **Mutable** (Có thể thay đổi nội dung mảng bên trong) | **Mutable** (Có thể thay đổi nội dung mảng bên trong) |
| **Đa luồng (Thread-safe)** | **Thread-safe** tự nhiên (vì không đổi) | **Không Thread-safe** | **Thread-safe** (các hàm `append`, `insert`, `delete` đều gắn từ khóa `synchronized`) |
| **Hiệu năng** | Chậm nhất và tốn RAM nhất nếu nối chuỗi nhiều lần | **Nhanh nhất** (không tốn chi phí khóa) | Chậm hơn `StringBuilder` do tốn chi phí lock/unlock (`synchronized` overhead) |
| **Khi nào dùng?** | Lưu trữ chuỗi thông thường, làm Key trong Map, truyền tham số, nối 1–2 chuỗi cố định ngoài vòng lặp. | **99% trường hợp cần nối/biến đổi chuỗi nhiều lần** (trong vòng lặp, tạo câu query động, build JSON/XML) vì biến thường là biến local của 1 hàm. | Rất hiếm dùng — chỉ dùng khi nhiều luồng cùng chia sẻ và ghi chung vào 1 buffer (thực tế thường ưu tiên xử lý riêng ở mỗi luồng rồi gộp lại). |

#### ⚠️ Bẫy nối chuỗi bằng `+` trong vòng lặp (`for`)
- Nếu viết `String s = ""; for (int i = 0; i < 100_000; i++) s += i;`:
  - Qua mỗi vòng lặp, một đối tượng `String` mới lại được cấp phát trên Heap và phải **copy toàn bộ mảng ký tự cũ sang mảng mới**.
  - Độ phức tạp thời gian tăng lên **$O(N^2)$**, tạo ra hàng trăm nghìn Object rác gây áp lực cực lớn lên Garbage Collector.
- **Giải pháp**: Luôn khởi tạo `StringBuilder sb = new StringBuilder();` bên ngoài vòng lặp và gọi `sb.append(i)` bên trong vòng lặp $\rightarrow$ Độ phức tạp chỉ còn **$O(N)$**.

---

### 1.4. Các Method `String` quan trọng cần nhớ (Java 8 $\rightarrow$ Java 15+)

1. **`trim()` vs `strip()` (Java 11+)**:
   - `trim()`: Chỉ cắt được các ký tự khoảng trắng có mã ASCII $\le$ `U+0020` (không cắt được khoảng trắng Unicode đặc biệt như Ideographic Space `U+3000`).
   - `strip()`, `stripLeading()`, `stripTrailing()`: Chuẩn Unicode hiện đại, cắt được mọi loại khoảng trắng Unicode $\rightarrow$ **Ưu tiên dùng `strip()`**.
2. **`isEmpty()` vs `isBlank()` (Java 11+)**:
   - `"   ".isEmpty()` $\rightarrow$ **`false`** (vì chỉ kiểm tra `length() == 0`).
   - `"   ".isBlank()` $\rightarrow$ **`true`** (kiểm tra chuỗi rỗng hoặc chỉ chứa toàn khoảng trắng).
3. **`split(String regex)` — Bẫy biểu thức chính quy (Regex)**:
   - Tham số của `split()` là một **Regex**, không phải chuỗi thường!
   - Ví dụ: `"a.b.c".split(".")` trả về mảng rỗng `[]` vì dấu `.` trong Regex đại diện cho *bất kỳ ký tự nào*. Phải viết là `"a.b.c".split("\\.")`.
4. **`substring(beginIndex, endIndex)`**:
   - Lấy từ `beginIndex` (bao gồm) đến `endIndex` (**không bao gồm** — độ dài chuỗi con là `endIndex - beginIndex`).
5. **`String.join(delimiter, elements)` (Java 8+)**:
   - Ví dụ: `String.join(", ", List.of("Java", "Spring", "Kafka"))` $\rightarrow$ `"Java, Spring, Kafka"`.
6. **Text Blocks `"""` (Java 15+)**:
   - Viết chuỗi nhiều dòng (SQL, JSON, HTML) sạch đẹp mà không cần nối chuỗi `+` hay escape `\"`, `\n`:
     ```java
     String json = """
         {
             "status": "SUCCESS",
             "amount": 100000
         }
         """;
     ```

---

### 1.5. Từ khóa `final` & Sự khác biệt giữa `final reference` và `Immutable Object`

- **3 vị trí đặt `final`**:
  1. **`final` variable (Biến)**: Chỉ được gán giá trị **1 lần duy nhất** (lúc khai báo hoặc trong constructor).
  2. **`final` method (Phương thức)**: Class con **không thể `@Override`** (ghi đè) phương thức này.
  3. **`final` class (Lớp)**: **Không thể kế thừa (`extends`)** lớp này (ví dụ: `public final class String`, `Integer`, `LocalDate`).
- **Phân biệt `final reference` $\neq$ `Immutable Object`**:
  - Từ khóa `final` trên một biến Object **chỉ khóa con trỏ tham chiếu** (ngăn không cho biến đó trỏ sang Object khác), chứ **không khóa trạng thái bên trong của Object đó**:
    ```java
    final List<String> list = new ArrayList<>();
    list.add("Java"); // ✅ HỢP LỆ: Thay đổi nội dung bên trong Object trên Heap
    // list = new ArrayList<>(); // ❌ COMPILE ERROR: Không thể gán lại tham chiếu mới
    ```

---

### 1.6. Từ khóa `static` & Bẫy Memory Leak với Inner Class

- **Đặc điểm của `static`**:
  - Thuộc về **Class** (lưu ở vùng nhớ **Metaspace** / gắn với Class object), được chia sẻ chung cho mọi instance, gọi trực tiếp qua `ClassName.method()` mà không cần khởi tạo đối tượng (`new`).
  - **Biến `static` không phải là hằng số** (trừ khi đi kèm `static final`). Nếu nhiều luồng cùng sửa một biến `static` sẽ gây lỗi Race Condition!
  - **`static block` (`static { ... }`)**: Chạy **đúng 1 lần duy nhất** khi Class được `ClassLoader` nạp vào bộ nhớ (chạy trước cả mọi constructor).
- **⚠️ Câu hỏi phân loại Middle: `static nested class` vs `non-static inner class`**:
  - **Non-static Inner Class** (`class Inner` nằm trong `class Outer`): Luôn ngầm giữ một **tham chiếu ẩn (`Outer.this`)** trỏ tới đối tượng `Outer` đã tạo ra nó.
    - **Nguy cơ Memory Leak**: Nếu đối tượng `Inner` được trả ra ngoài và sống lâu hơn (ví dụ được lưu vào cache hoặc chạy trong 1 thread nền), thì đối tượng `Outer` khổng lồ bên ngoài **không bao giờ được GC thu hồi** vì vẫn bị `Inner` giữ tham chiếu!
  - **Static Nested Class** (`static class Nested` nằm trong `class Outer`): **Không giữ tham chiếu ẩn** tới instance của `Outer`, hoạt động độc lập như một class bình thường $\rightarrow$ **Best Practice**: Nếu class bên trong không cần truy cập trực tiếp vào các field non-static của class bên ngoài, **luôn khai báo nó là `static class`** (như cách Joshua Bloch khuyến nghị trong sách *Effective Java*).

---

### 1.7. 5 Quy tắc vàng để tự viết một Class Immutable chuẩn

Để viết một class bất biến hoàn toàn (như `ImmutableStudent` hoặc `Money`), bắt buộc tuân thủ đủ **5 bước**:
1. Khai báo class là **`public final class`** (ngăn class con kế thừa rồi override các hàm getter để giả mạo dữ liệu).
2. Tất cả các fields phải là **`private final`**.
3. **Tuyệt đối không cung cấp các hàm `setter`**.
4. Khởi tạo toàn bộ fields thông qua Constructor. Với các field là **Mutable Object** (như `java.util.Date`, `List`, `Map`, mảng), phải thực hiện **Defensive Copy (Sao chép phòng thủ) lúc truyền vào Constructor**:
   - Ví dụ: `this.dob = new Date(dob.getTime());` và `this.courses = new ArrayList<>(courses);` (hoặc `List.copyOf(courses)` từ Java 10+).
   - *Lý do*: Ngăn người gọi bên ngoài giữ lại biến `dob`/`courses` gốc rồi âm thầm sửa nó sau khi đã tạo object!
5. Trong các hàm **`getter` của các field Mutable**, tiếp tục trả về **bản sao (Defensive Copy)** hoặc **Unmodifiable View** (`new Date(this.dob.getTime())`, `Collections.unmodifiableList(this.courses)`).
   - *Lý do*: Ngăn người gọi viết `student.getCourses().add("Hack")` làm thay đổi danh sách bên trong!

---

## 2. Tự kiểm tra (Trả lời phỏng vấn)

### Câu 1: `String s = new String("hello")` tạo ra mấy đối tượng (Object)?
- **Trả lời chuẩn phỏng vấn**:
  - Câu lệnh này tạo ra **1 hoặc 2 đối tượng `String`** (tùy thuộc vào trạng thái của String Pool trước đó):
    1. **Đối tượng thứ nhất (trong String Pool)**: Khi class được nạp, JVM nhìn thấy literal `"hello"`. Nếu trong String Pool **chưa có** chuỗi `"hello"`, JVM sẽ tạo **1 đối tượng** trong String Pool. Ngược lại, nếu trước đó trong chương trình đã từng xuất hiện `"hello"` trong Pool rồi, JVM sẽ **tái sử dụng** chứ không tạo mới.
    2. **Đối tượng thứ hai (trên Heap ngoài String Pool)**: Toán tử `new String(...)` **luôn luôn** ép JVM cấp phát thêm **1 đối tượng `String` mới trên Heap** (nằm ngoài String Pool) và biến `s` sẽ trỏ vào đối tượng trên Heap này.
  - *(Điểm cộng Middle)*: Dù có 2 đối tượng `String` (1 trong Pool, 1 ngoài Heap), nhưng mảng `byte[] value` lưu các ký tự `'h','e','l','l','o'` bên trong chúng thì được **chia sẻ chung (trỏ cùng 1 mảng `byte[]`)** chứ không bị nhân đôi.

---

### Câu 2: Vì sao `String` là immutable? Làm sao để tự tạo 1 class immutable của riêng mình?
- **Trả lời chuẩn phỏng vấn**:
  - **Vế 1 — 4 lý do `String` là Immutable**:
    1. **String Pool**: Cho phép nhiều biến trỏ chung vào 1 chuỗi trong Pool để tiết kiệm bộ nhớ mà không sợ biến này sửa làm ảnh hưởng biến kia.
    2. **Bảo mật (Security)**: Đảm bảo các chuỗi nhạy cảm (URL DB, thông tin xác thực, tên class trong `ClassLoader`, đường dẫn file) không bị thay đổi sau khi đã qua bước kiểm tra hợp lệ.
    3. **Thread-safe tự nhiên**: Đa luồng có thể đọc/chia sẻ cùng một chuỗi mà không cần đồng bộ (`synchronized`).
    4. **Hashcode Caching**: Tính `hashCode` đúng 1 lần rồi cache lại, giúp làm Key trong `HashMap` / `HashSet` đạt tốc độ tối đa.
  - **Vế 2 — 5 quy tắc tự tạo 1 class Immutable** (hoặc dùng `record` từ Java 16+ kết hợp defensive copy):
    1. Đặt từ khóa **`final`** cho class (`public final class ...`) để chống kế thừa.
    2. Đặt tất cả các thuộc tính là **`private final`**.
    3. **Không viết hàm setter**; mọi phương thức tính toán (như `money.add(other)`) phải trả về một **instance mới** (`return new Money(...)`).
    4. Thực hiện **Defensive Copy trong Constructor** đối với các thuộc tính là đối tượng khả biến (`Date`, `List`, `Map`).
    5. Thực hiện **Defensive Copy hoặc trả về `Collections.unmodifiable...` trong các hàm Getter** của các thuộc tính khả biến.

---

### Câu 3: `final List<String> list = new ArrayList<>()` có thêm phần tử được không? Làm sao để tạo một `List` thực sự không thể thêm/xóa phần tử?
- **Trả lời chuẩn phỏng vấn**:
  - **Có thể thêm/xóa/sửa phần tử bình thường** (`list.add("A")` hoàn toàn hợp lệ).
  - **Lý do**: Từ khóa `final` chỉ biến `list` thành một **hằng tham chiếu (final reference)** — tức là không cho phép gán lại biến `list` trỏ sang một đối tượng `List` khác (`list = new LinkedList<>()` sẽ báo lỗi biên dịch), nhưng bản thân đối tượng `ArrayList` nằm trên Heap vẫn là một đối tượng **Mutable**.
  - **Cách tạo một `List` thực sự bất biến (Unmodifiable / Immutable List)**:
    - Dùng **`List.of("A", "B")`** hoặc **`List.copyOf(existingList)`** (Java 9/10+) hoặc **`Collections.unmodifiableList(list)`** — khi gọi `.add()`, `.remove()`, `.set()` sẽ lập tức ném ra `UnsupportedOperationException`.
