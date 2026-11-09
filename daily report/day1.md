

Wrapper class: Integer, Long, Double, Boolean... — khác primitive ở điểm nào, khi nào dùng wrapper?

 primitive là kiểu dữ liệu không chưa null và được lưu trên stask hoặc nằm phẳng bên trong object không có phương thức đi kèm  nó khi so sánh == thì so sánh 2 giá trị/ còn đối với wrapper thì nó là thuộc dạng object( kế thừa class bject ) nó có thể chứa null, được lưu trên bộ nhớ heap và biến tham chiếu (reference pointer) lưu trên Stack, có nhiều phương thức đi kèm, hỗ trợ Generics & Collections, nó khi so sánh == thì so sánh 2 địa chỉ ô nhớ / chúng ta sử dụng wrapper khi 1: Collections & Generics, 2: Entity Database & ORM, 3: DTO, 4 : khi cần dùng các method/ dùng primitive  khi Các trường model/entity nghiệp vụ bắt buộc tồn tại giá trị:(not null), các biến nội bộ để xử lý logic như for... 


Autoboxing / Unboxing: tự động chuyển primitive <-> wrapper khi nào, và nguy cơ NPE khi unbox null
 Autoboxing (Primitive -> Wrapper)?
 nếu ta khai báo Interger i = 10 thì lúc này 10 này được hiểu là Wrapper chương trình sẽ autoboxing thành new Interger(10)
 Khi nào xảy ra Unboxing (Wrapper ->  Primitive)?

nếu ta khai báo Integer x = 5, y = 10;
int c = x+y thì lúc này chương trình sẽ tự động Unboxing x, y để gán vào c 

và nguy cơ NPE khi unbox null vì Unboxing là cơ chế thêm xxx.value(), nên khi unbobject có giá trị null thì sẽ ném ra NPE

Integer Cache -128..127: Integer.valueOf() dùng cache, new Integer() thì không. Vì sao 127 == 127 là true mà 128 == 128 là false?

Lớp này khởi tạo sẵn một mảng đối tượng Integer cache[] chứa các giá trị từ -128 đến 127
khi Integer.valueOf() Autoboxing Kiểm tra xem giá trị có nằm trong khoảng $[-128, 127]$ hay không. Nếu có, nó trả về ngay tham chiếu đến đối tượng đã tồn tại sẵn trong cache[] mà không cấp phát thêm bộ nhớ. Nếu nằm ngoài khoảng, nó mới khởi tạo một đối tượng mới trên Heap.


 Vì sao 127 == 127 là true mà 128 == 128 là false?
 vì 127 nằm trong cache [-128, 127] nên cả 2  127 == 127 đều cùng 1 địa chỉ ô nhớ ở trong cache nêu bằng nhau còn 128 thì nằm ngoài nên khi thực hiệnBoxing Auto tạo ra 2 Object khác nhau nên địa chỉ ô nhớ khác nhau dẫn đến == là false


 Stack vs Heap: biến local + tham chiếu nằm ở stack (mỗi thread 1 stack), object nằm ở heap (dùng chung). Biến đi đâu khi method kết thúc?

 method kết thúc Stack Frame bị pop khỏi Stack ngay lập tức các biến nguyên thủy và giá trị sẽ được biến mất ngay lập tức còn đối với Biến tham chiếu sẽ bị giải phóng bởi Garbage Collector khi không còn tham chiếu nào đến nó nữa


 Java pass-by-value: Java luôn pass-by-value. Với object, giá trị được copy là tham chiếu. Đổi field thì thấy, gán lại tham chiếu thì không

 == vs equals(): == so sánh giá trị primitive / tham chiếu, equals() so sánh nội dung (mặc định của Object cũng là so tham chiếu)

 vì sao tiền dùng `BigDecimal` chứ không dùng `double` (0.1 + 0.2).

 double là số thực nhị phân, không biểu diễn chính xác số thập phân như 0.1 nên cộng dồn sẽ sai. Tiền cần chính xác tuyệt đối đến từng xu và làm tròn có quy tắc, nên dùng BigDecimal với String constructor và RoundingMode rõ ràng. double chỉ hợp cho tính toán xấp xỉ như đồ họa, vật lý, còn tiền thì không.



 1. Java pass-by-value hay pass-by-reference? Chứng minh bằng code.
 Java luôn pass-by-value. Với object, giá trị được truyền là tham chiếu, nên mình có thể thay đổi trạng thái bên trong object, nhưng không thể thay đổi bản thân tham chiếu của caller bằng cách gán lại tham số

ví dụ swap 2 số  void swap (Interger a, Interger b{
    int t = a; a= b, b =t
}

int a =1;
int b= 2;
swap(a,b)
sout(a.b)
 thì khi này pass-by-value được truyền vào hàm swap sau khi sử lý thì xóa fream k ảnh hưởng gì tới data


2. Vì sao không dùng `double` cho tiền? `BigDecimal.equals` khác `compareTo` thế nào (2.0 vs 2.00)?

vì double không có tính đúng đắng, double nhị phên còn BigDecimal thập phân chính xác, ví dụ 0.3 ở double 
là vô hạn tuần haongf, còn đối với những tác vụ liên quan đến tiền khi thực hiện các phép tính k chấp nhận sai số khi thực hiện phép tính BigDecimal sẽ giữ toàn vẹn giá trị của số BigDecimal.equals` khác `compareTo` ở equals so sánh cả scale nên khi so sánh 2 số BigDecimal mêm sủ dụng compareTo

3. Primitive có thể null không? Khi nào unboxing gây NPE trong code Spring thực tế?

Primitive không thể null, unboxing dể gây NPE vì trong spring khi chuyển từ swapper sang Primitive theo co chết xxxx.value() nên nếu như swapper chấp nhận null bị null mà .value() nê dể gây ra lỗi NPE