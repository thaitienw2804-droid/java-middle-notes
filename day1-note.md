"Ba cái này có quan hệ bao nhau: JDK chứa JRE, JRE chứa JVM.

JVM — Java Virtual Machine là máy ảo chạy chương trình Java. Nó nhận bytecode trong file .class, nạp class, kiểm tra, rồi thực thi — vừa thông dịch, vừa dùng JIT biên dịch những đoạn code chạy nhiều thành mã máy cho nhanh. JVM cũng quản lý bộ nhớ và dọn rác (GC).

JRE — Java Runtime Environment là môi trường để chạy chương trình Java. Nó gồm JVM + thư viện core như java.lang, java.util. Chỉ có JRE thì chạy được app, nhưng không biên dịch được code mới.

JDK — Java Development Kit là bộ công cụ để phát triển chương trình Java. Nó gồm toàn bộ JRE + trình biên dịch javac và các tool khác như jar, javadoc. Muốn viết code Java thì phải cài JDK, còn máy chỉ chạy app thì JRE là đủ.

Ví dụ luồng chạy: em viết Hello.java, dùng javac trong JDK biên dịch thành Hello.class, rồi dùng lệnh java để JVM nạp bytecode đó lên, dùng thư viện trong JRE để chạy, và JVM lo quản lý bộ nhớ trong suốt quá trình đó."