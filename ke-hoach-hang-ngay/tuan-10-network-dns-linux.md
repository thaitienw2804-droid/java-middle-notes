# Tuần 10 (11/01 – 17/01/2027): Mạng, DNS, Nginx + Linux quản trị server

> Skill audit: Network = 2, Linux = 2 → mục tiêu 4. Học theo **luồng 1 request thật** và làm trên **VPS thật** (hoặc WSL nếu chưa có VPS).

---

## Ngày 64 — Thứ 2 (11/01): Mô hình mạng, IP, TCP/UDP
**Kiến thức phải nắm:**
- OSI 7 tầng vs TCP/IP 4 tầng — biết protocol nằm tầng nào (HTTP/TLS/TCP/IP/Ethernet).
- IP, subnet, CIDR (`10.0.0.0/16`), **public vs private IP**, **NAT**, port (well-known), localhost/`0.0.0.0` (vì sao app trong container phải bind 0.0.0.0).
- **TCP**: 3-way handshake (SYN, SYN-ACK, ACK), 4-way close, TIME_WAIT, đảm bảo thứ tự/tin cậy, flow/congestion control (biết tên). **UDP**: không kết nối — DNS, video, game, QUIC.
- Socket, connection pool liên quan TCP thế nào (tránh handshake lặp lại).
- Lệnh: `ping`, `traceroute`/`tracert`, `ss -tulpn`/`netstat`, `curl -v`, `telnet`/`nc` kiểm tra port.

**Code tay:** `curl -v https://...` đọc từng giai đoạn; `ss -tulpn` xem cổng app; dùng Wireshark/tcpdump bắt 1 handshake (tùy chọn).

**Tự kiểm tra:** TCP vs UDP? Vì sao handshake 3 bước? Public vs private IP, NAT giải thích trong 1 phút.

---

## Ngày 65 — Thứ 3 (12/01): DNS chi tiết
**Kiến thức phải nắm:**
- Luồng phân giải: browser cache → OS cache/`hosts` → **recursive resolver** (ISP/8.8.8.8/1.1.1.1) → **root** → **TLD** (.com) → **authoritative** NS → trả IP; recursive vs iterative query.
- Record: **A, AAAA, CNAME** (không đặt ở apex domain), **MX, TXT** (SPF/DKIM/verify domain), NS, SOA, SRV, CAA.
- **TTL**, DNS cache nhiều tầng, "propagation" thực chất là chờ cache hết hạn → hạ TTL trước khi migrate.
- DNS-based load balancing/GeoDNS, failover; DNS nội bộ: Docker embedded DNS, K8s CoreDNS (`svc.namespace.svc.cluster.local`).
- Lệnh: `dig +trace`, `dig @8.8.8.8`, `nslookup`, `host`; flush cache.

**Code tay:** `dig +trace` 1 domain thật; tạo subdomain trỏ về VPS (domain giá rẻ / DuckDNS miễn phí); đổi `/etc/hosts` để kiểm chứng ưu tiên.

> **⚠️ Bẫy JVM DNS Caching trên Production (Cloud / AWS RDS Failover / K8s):**
> - Ngoài TTL của DNS record và cache của OS, **chính JVM (`InetAddress`) cũng tự cache kết quả phân giải DNS**!
> - Nếu chạy với SecurityManager hoặc cấu hình cũ, JVM có thể cache IP mãi mãi (`-1`) hoặc lâu hơn TTL của DNS. Hệ quả: Khi Database (AWS RDS) hoặc downstream service failover đổi sang IP mới trên cùng tên miền DNS, ứng dụng Java **vẫn cố gọi vào IP cũ đã chết** cho tới khi restart pod!
> - **Cách sửa chuẩn**: Cấu hình `networkaddress.cache.ttl=30` (hoặc `5` giây) và `networkaddress.cache.negative.ttl=0` trong `java.security` / JVM properties.

**Tự kiểm tra:** Gõ `api.shop.com` DNS phân giải thế nào? A vs CNAME? Vì sao `dig` ra IP mới mà máy vẫn ra IP cũ?

**LeetCode (20'):** Pacific Atlantic Water Flow, Kth Smallest Element in BST.

---

## Ngày 66 — Thứ 4 (13/01): HTTP, TLS, CORS, CDN
**Kiến thức phải nắm:**
- HTTP message: method, header quan trọng (`Host`, `Authorization`, `Content-Type`, `Cache-Control`, `Cookie`, `X-Forwarded-For`, `X-Request-Id`), keep-alive.
- **HTTP/1.1** (head-of-line blocking, nhiều connection) → **HTTP/2** (multiplexing trên 1 TCP, nén header HPACK, binary) → **HTTP/3** (QUIC trên UDP, hết HOL ở tầng TCP, kết nối nhanh hơn).
- **TLS handshake** (1.2 vs 1.3): trao đổi khóa bất đối xứng → khóa phiên đối xứng; certificate chain, CA, **Let's Encrypt** + certbot (gia hạn 90 ngày); cert hết hạn → trình duyệt báo lỗi; mTLS (service-to-service).
- **CORS**: trình duyệt chặn đọc response cross-origin; preflight `OPTIONS`; vì sao Postman gọi được. Same-Origin Policy.
- **CDN**: edge cache, origin, cache static/media, cache-busting.

**Code tay:** `openssl s_client -connect domain:443` xem chain; tạo trang HTML gọi API khác origin → thấy lỗi CORS → sửa ở Gateway.

**Tự kiểm tra:** HTTP/1.1 vs 2 vs 3? TLS handshake làm gì? CORS ai chặn ai?

---

## Ngày 67 — Thứ 5 (14/01): Load Balancer, Reverse Proxy, Nginx
**Kiến thức phải nắm:**
- **Forward proxy** (đại diện client) vs **Reverse proxy** (đại diện server).
- **Load Balancer L4** (TCP/UDP, nhanh, không đọc HTTP) vs **L7** (đọc path/header/cookie, routing thông minh, SSL termination).
- Thuật toán: round robin, weighted, least connections, IP hash; **sticky session** (khi nào cần, vì sao nên tránh — làm app stateless + session ở Redis).
- Health check active/passive.
- **Nginx**: `server`, `location`, `proxy_pass`, `upstream`, header `X-Forwarded-*`, **SSL termination**, gzip, static cache, `limit_req` (rate limit), `client_max_body_size` (lỗi 413 khi upload), timeout (`proxy_read_timeout` → 504).
- Kiến trúc: DNS → (CDN) → LB/Nginx → Gateway → Service.

**Code tay:** Nginx reverse proxy trước Gateway với upstream 2 instance Gateway (round robin), tắt 1 instance quan sát.

> **⚠️ 2 Bẫy cấu hình Nginx Reverse Proxy trước Spring Boot / Gateway:**
> 1. **Mất Keep-Alive từ Nginx vào Upstream (Gây cạn cổng TCP / `TIME_WAIT` tràn ngập)**:
>    - Mặc định `proxy_pass` của Nginx dùng giao thức **`HTTP/1.0` và gửi header `Connection: close`** tới Gateway → mỗi request lại phải bắt tay TCP 3 bước mới!
>    - **Cách sửa**: Trong khối `upstream` thêm `keepalive 64;`, và trong `location` thêm `proxy_http_version 1.1; proxy_set_header Connection "";`.
> 2. **Rate Limit theo IP ở Gateway khóa nhầm toàn bộ người dùng**:
>    - Khi có Nginx đứng trước, IP kết nối trực tiếp vào Gateway là IP nội bộ của Nginx. Nếu không truyền `proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;` ở Nginx VÀ bật `server.forward-headers-strategy=framework` (cùng cấu hình `trusted-proxies`) ở Spring Boot, bộ Rate Limit theo IP sẽ tưởng **mọi khách hàng đều là 1 người** (cùng IP của Nginx) và chặn sạch toàn bộ hệ thống!

**Tự kiểm tra:** L4 vs L7? Reverse vs forward proxy? Sticky session khi nào? Nginx vs Spring Cloud Gateway — dùng cả hai thì mỗi cái làm gì?

---

## Ngày 68 — Thứ 6 (15/01): Linux thực chiến
**Kiến thức phải nắm:**
- File system & permission (`chmod 640`, `chown`, rwx, user/group), `sudo`, tạo user deploy, **SSH key**, tắt login root + password.
- Process: `ps aux`, `top/htop`, `kill -15` vs `kill -9` (graceful shutdown Spring!), `nohup`, signal.
- Tài nguyên: `free -h`, `df -h`, `du -sh *`, `iostat`, `vmstat`, load average nghĩa là gì; inode đầy.
- Log: `journalctl -u app -f`, `tail -f`, `grep`, `less`, `awk` cơ bản; **logrotate**.
- **systemd**: viết unit file cho app (Restart=always, User, Environment), `systemctl enable/start/status`.
- Firewall `ufw` (chỉ mở 22, 80, 443), fail2ban; cron (`crontab -e`, cú pháp 5 trường).

**Code tay:** trên VPS: tạo user deploy + SSH key, ufw, cài Docker, chạy app bằng systemd hoặc compose; tự gây "disk đầy" bằng file lớn rồi tìm bằng `du`.

> **⚠️ 2 Bẫy Linux "Đầy ổ đĩa (`No space left on device`)" kinh điển khi vận hành Server:**
> 1. **`df -h` báo ổ đĩa đầy 100%, nhưng chạy `du -sh /*` cộng lại chỉ thấy tốn có 20% — phần dung lượng kia biến đi đâu?**
>    - **Nguyên nhân**: Có ai đó vừa dùng lệnh `rm /var/log/app-huge.log` để xóa một file log vài chục GB, **nhưng tiến trình Java đang chạy vẫn giữ File Descriptor mở tới file đó**, nên Linux Kernel chưa giải phóng vùng đĩa!
>    - **Cách kiểm tra & xử lý**: Chạy `lsof +L1` (hoặc `lsof | grep deleted`) để tìm tiến trình đang giữ file đã xóa. Lần sau không dùng `rm` với file log đang mở mà dùng **`truncate -s 0 app.log`** (hoặc `: > app.log`) để làm rỗng file ngay lập tức mà không cần restart app!
> 2. **`df -h` báo còn trống 50% GB, nhưng tạo file mới vẫn báo lỗi `No space left on device`**:
>    - **Nguyên nhân**: **Cạn kiệt Inode (`df -i` báo `IUse% = 100%`)** do ứng dụng hoặc cronjob sinh ra hàng triệu file nhỏ li ti.

**Tự kiểm tra:** Disk đầy xử lý theo thứ tự nào? `kill -9` vs `-15`? Load average 4 trên máy 2 core nghĩa là gì?

**LeetCode (20'):** Gas Station, Jump Game.

---

## Ngày 69 — Thứ 7 (16/01): Lab 4–5h — đưa hệ thống lên VPS với domain + HTTPS
- DNS: `api.<domain>` A record → IP VPS (TTL thấp).
- Nginx: HTTPS bằng Let's Encrypt (certbot hoặc dùng Caddy/Traefik để so sánh), redirect 80→443, proxy tới Gateway, `client_max_body_size`, `limit_req`.
- Chạy shop-order-platform bằng Docker Compose trên VPS, chỉ mở 80/443 ra ngoài (DB/Redis/Kafka **không** expose public).
- Backup Postgres: `pg_dump` + cron hàng ngày + **restore thử** sang DB khác.
- **Vẽ sơ đồ luồng request** từ DNS → TLS → Nginx → Gateway → Service → DB/Kafka (`docs/request-flow.md`).

**LeetCode (30'):** Decode Ways, Unique Paths.

---

## Ngày 70 — Chủ nhật (17/01): Review + luyện miệng
**10 câu phỏng vấn của tuần (xem thêm ngân hàng DNS/Network trong lộ trình):**
1. Gõ `api.shop.com` thì chuyện gì xảy ra, theo đúng thứ tự? (3 phút, có vẽ)
2. Record A/CNAME/MX/TXT, TTL và propagation.
3. TCP handshake; TCP vs UDP.
4. HTTP/1.1 vs 2 vs 3.
5. TLS handshake; cert hết hạn thì sao?
6. L4 vs L7 load balancer; sticky session.
7. Reverse vs forward proxy; Nginx làm gì trong hệ thống của bạn?
8. CORS thực chất là gì?
9. Disk đầy / CPU 100% / RAM đầy — lệnh kiểm tra và thứ tự xử lý.
10. Backup & restore Postgres của bạn thế nào?

**Checklist:** ☐ Domain HTTPS chạy thật ☐ Sơ đồ request flow ☐ Backup + restore đã thử ☐ Chấm lại skill Network/Linux

