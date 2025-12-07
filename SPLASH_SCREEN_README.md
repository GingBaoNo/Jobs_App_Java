# Trang Giới Thiệu Phần Mềm Tìm Việc Làm FJobs

## Tổng Quan
Chúng tôi đã triển khai một trang giới thiệu (splash screen) chuyên nghiệp cho ứng dụng tìm việc làm FJobs trên nền tảng Android. Trang giới thiệu xuất hiện ngay khi người dùng mở ứng dụng, giúp xây dựng thương hiệu và tạo trải nghiệm người dùng mượt mà hơn.

## Chi Tiết Triển Khai

### 1. Cấu trúc Tệp
```
app/
├── src/main/java/com/example/fjobs/activities/
│   └── FlashActivity.java           # Activity xử lý trang giới thiệu
├── src/main/res/layout/
│   └── activity_flash.xml           # Layout cho trang giới thiệu
└── src/main/res/values/
    └── strings.xml                  # Chuỗi văn bản cho trang giới thiệu
```

### 2. Giao Diện Người Dùng
- **Màu nền:** Xanh dương (#3B82F6) - màu thương hiệu của ứng dụng
- **Logo:** Hiển thị hình tròn của ứng dụng với hiệu ứng phóng to mượt
- **Tên ứng dụng:** "FJobs" với chữ đậm và lớn
- **Khẩu hiệu:** "Phần mềm tìm việc làm tốt nhất"
- **Mô tả:** "Kết nối ứng viên với nhà tuyển dụng hàng đầu"
- **Thanh tiến trình:** Hiển thị hoạt động trong khi khởi động ứng dụng

### 3. Hiệu Ứng Hoạt Hình
- Logo có hiệu ứng phóng to mượt từ 0% đến 100%
- Các phần tử văn bản có hiệu ứng mờ dần theo trình tự
- Thời lượng hoạt ảnh: 3 giây tổng cộng

### 4. Điều Hướng
- Sau 3 giây, ứng dụng tự động chuyển sang MainActivity
- Có hiệu ứng chuyển tiếp mờ dần giữa các màn hình
- Splash activity sẽ bị hủy sau khi chuyển hướng

## Hệ Thống Backend
Ứng dụng được tích hợp chặt chẽ với hệ thống backend Java Spring Boot tại thư mục `web_service`, hỗ trợ:
- Quản lý người dùng và vai trò
- Quản lý công ty và công việc
- Quản lý hồ sơ ứng viên
- Tính năng nộp đơn, lưu công việc, theo dõi công ty
- Thông báo và cảnh báo công việc phù hợp

## Cơ Sở Dữ Liệu
Cấu trúc cơ sở dữ liệu SQL được định nghĩa trong `job_dt.txt` với các bảng chính:
- `user`: Quản lý người dùng hệ thống
- `company`: Thông tin công ty
- `jobdetails`: Chi tiết công việc
- `profile`: Hồ sơ cá nhân
- `applied_jobs`: Công việc đã ứng tuyển
- `saved_jobs`: Công việc đã lưu

## Kết Luận
Trang giới thiệu đã được tích hợp thành công vào ứng dụng, giúp cải thiện trải nghiệm người dùng và thể hiện chuyên nghiệp của ứng dụng tìm việc làm FJobs. Hệ thống hoàn chỉnh cung cấp tất cả các tính năng cần thiết cho cả ứng viên và nhà tuyển dụng.