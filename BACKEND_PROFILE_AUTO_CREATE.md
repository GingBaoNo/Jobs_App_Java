# Cập Nhật Backend Để Tự Động Tạo Hồ Sơ Khi Đăng Ký

## Vấn đề
- Khi người dùng đăng ký tài khoản mới, hệ thống chỉ tạo User mà không tạo Profile tương ứng
- Khi người dùng cố gắng cập nhật hồ sơ sau khi đăng ký, ứng dụng Android gọi API update profile
- Backend trả về lỗi "Profile not found for current user" vì hồ sơ chưa tồn tại
- Điều này dẫn đến lỗi trong ứng dụng Android khi cố gắng cập nhật hồ sơ

## Phân tích hệ thống
### Cấu trúc hiện tại:
1. ApiAuthController.java - xử lý đăng ký và đăng nhập
2. UserService.java - xử lý logic người dùng
3. ProfileService.java - xử lý logic hồ sơ
4. ApiProfileController.java - xử lý API hồ sơ

### Quá trình đăng ký:
1. Người dùng gửi yêu cầu đăng ký đến /api/auth/register
2. ApiAuthController gọi UserService.registerUser()
3. UserService tạo người dùng mới nhưng không tạo hồ sơ
4. Người dùng có thể đăng nhập nhưng không có hồ sơ

### Quá trình cập nhật hồ sơ:
1. Người dùng cố gắng cập nhật hồ sơ qua /api/v1/profiles/my-profile
2. ApiProfileController tìm hồ sơ theo người dùng hiện tại
3. Vì hồ sơ không tồn tại, nên trả về lỗi "Profile not found for current user"

## Giải pháp áp dụng

### 1. Cập nhật UserService
- Thêm inject ProfileService vào UserService
- Cập nhật phương thức registerUser() để tự động tạo hồ sơ mặc định cho người dùng mới (chỉ cho người tìm việc - NV)
- Đặt giá trị mặc định: tên (từ thông tin đăng ký), giới tính mặc định là "Nam" (người dùng có thể cập nhật sau)

### 2. Logic cập nhật
- Chỉ tạo hồ sơ mặc định cho người dùng có vai trò là "NV" (người tìm việc)
- Không tạo hồ sơ cho nhà tuyển dụng (NTD) vì họ không cần hồ sơ cá nhân theo cách mà người tìm việc cần
- Bao bọc trong try-catch để đảm bảo đăng ký vẫn thành công ngay cả khi tạo hồ sơ thất bại

### 3. Cải tiến hệ thống
- Tự động tạo hồ sơ ngay sau khi người dùng đăng ký thành công
- Đảm bảo người dùng có thể cập nhật hồ sơ ngay sau khi đăng ký
- Giảm lỗi trong ứng dụng Android khi tương tác với hồ sơ

## Kết quả đạt được
- Người dùng mới được tự động tạo hồ sơ mặc định khi đăng ký
- Tránh lỗi "Profile not found" trong quá trình cập nhật hồ sơ
- Cải thiện trải nghiệm người dùng trong ứng dụng Android
- Tăng độ ổn định của hệ thống

## Lưu ý
- Hồ sơ được tạo với thông tin tối thiểu: họ tên (từ thông tin đăng ký) và giới tính mặc định
- Người dùng có thể cập nhật đầy đủ thông tin hồ sơ sau trong quá trình sử dụng
- Nhà tuyển dụng (NTD) không được tạo hồ sơ, vì họ có các chức năng quản lý công ty thay vì hồ sơ cá nhân

## Kiểm tra
- Kiểm tra chức năng đăng ký vẫn hoạt động bình thường
- Kiểm tra hồ sơ được tạo tự động cho người tìm việc
- Kiểm tra chức năng cập nhật hồ sơ hoạt động sau khi đăng ký
- Đảm bảo không ảnh hưởng đến nhà tuyển dụng

## Lợi ích
- Tăng độ nhất quán giữa các thành phần hệ thống
- Cải thiện trải nghiệm người dùng đầu tiên
- Giảm lỗi và sự cố trong ứng dụng Android
- Giảm yêu cầu hỗ trợ do lỗi liên quan đến hồ sơ