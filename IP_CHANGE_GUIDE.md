# Hướng dẫn thay đổi IP khi đổi mạng

Để thay đổi địa chỉ IP khi chuyển sang mạng khác, bạn cần thực hiện thay đổi trong 2 file:

## 1. Thay đổi IP trong file cấu hình chính

Mở file: `app/src/main/res/values/strings.xml`

Tìm dòng:
```xml
<string name="server_ip">192.168.1.8</string>
```

Thay đổi thành IP mới của máy chủ backend:
```xml
<string name="server_ip">192.168.x.x</string>
```

## 2. Cập nhật network_security_config.xml

Mở file: `app/src/main/res/xml/network_security_config.xml`

Cập nhật domain cho phép truy cập:
```xml
<network-security-config>
    <domain-config cleartextTrafficPermitted="true">
        <domain includeSubdomains="true">192.168.x.x</domain>  <!-- Thay đổi IP ở đây -->
        <domain includeSubdomains="true">localhost</domain>
    </domain-config>
</network-security-config>
```

Lưu ý: XML không hỗ trợ tham chiếu đến string resources nên bạn vẫn cần cập nhật thủ công IP trong file này.

## 3. Các nơi sử dụng IP

IP được sử dụng ở các nơi sau trong ứng dụng:
- Gọi API backend qua Retrofit
- Kết nối WebSocket cho chat
- Tải hình ảnh từ server
- Tải file CV từ server

Tất cả các vị trí này đều được quản lý tập trung qua lớp `ServerConfig` nên khi thay đổi IP trong `strings.xml`, toàn bộ ứng dụng sẽ sử dụng IP mới.

## 4. Lưu ý

- Đảm bảo máy chủ backend đang chạy trên IP mới
- Đảm bảo cổng 8080 được mở và không bị firewall chặn
- Sau khi thay đổi IP, bạn cần build lại ứng dụng
- Bạn chỉ cần nhớ thay đổi IP ở 2 vị trí như trên là đủ