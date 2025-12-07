# Sửa Lỗi Chức Năng Đăng Ký FJobs

## Vấn đề ban đầu
- Chức năng đăng ký trong ứng dụng Android FJobs gặp lỗi khi gửi yêu cầu đến server
- Nguyên nhân: Giao tiếp API giữa Android client và Spring Boot backend không phù hợp

## Phân tích nguyên nhân
### 1. Không khớp mô hình dữ liệu
- Android gửi đối tượng `User` đến endpoint `/api/auth/register`
- Backend Spring Boot yêu cầu đối tượng `RegisterRequest` với cấu trúc cụ thể:
  - `username` (chuỗi)
  - `password` (chuỗi) 
  - `displayName` (chuỗi)
  - `contact` (chuỗi)
  - `role` (chuỗi - ADMIN, NTD, NV)

### 2. Thiếu trường mật khẩu
- Mô hình `User` trên Android không chứa trường `password`
- Backend yêu cầu cả tên tài khoản và mật khẩu để tạo tài khoản mới

### 3. Mapping không đúng API
- API service trên Android định nghĩa endpoint đăng ký nhận tham số `@Body User user`
- Trong khi backend chỉ chấp nhận `@RequestBody RegisterRequest registerRequest`

## Giải pháp áp dụng

### 1. Tạo mô hình RegisterRequest mới
- Tạo lớp `RegisterRequest.java` với các trường phù hợp:
  - username
  - password
  - displayName
  - contact
  - role

### 2. Cập nhật API service
- Thay đổi khai báo trong `ApiService.java`: `@Body RegisterRequest registerRequest`
- Thêm import cần thiết

### 3. Cập nhật logic xử lý đăng ký
- Trong `RegisterActivity.java`, tạo đối tượng `RegisterRequest` với các giá trị phù hợp
- Map roleId sang roleName: 2 -> "NV" (người tìm việc), 3 -> "NTD" (nhà tuyển dụng)
- Gửi yêu cầu đăng ký với đối tượng đúng cấu trúc

### 4. Thêm import vào các lớp liên quan
- Thêm import RegisterRequest vào ApiService và RegisterActivity

## Kết quả đạt được
- Build ứng dụng thành công
- API đăng ký hoạt động đúng theo cấu trúc backend
- Người dùng có thể đăng ký tài khoản mới với đầy đủ thông tin bao gồm mật khẩu
- Tích hợp liền mạch giữa Android app và Spring Boot API

## Kiểm tra
- Đảm bảo chức năng đăng nhập vẫn hoạt động bình thường
- Xác nhận vai trò người dùng được phân đúng (NV/NTD)
- Kiểm tra giao diện người dùng vẫn giữ nguyên như trước

## Lợi ích
- Sửa lỗi đăng ký, giúp người dùng có thể tạo tài khoản mới
- Cải thiện trải nghiệm người dùng từ bước đầu tiên
- Đảm bảo tính toàn vẹn của hệ thống xác thực
- Tăng tỷ lệ chuyển đổi đăng ký người dùng mới