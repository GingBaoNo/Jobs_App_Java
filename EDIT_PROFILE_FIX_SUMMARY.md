# Sửa Lỗi NullPointerException trong EditProfileFragment

## Vấn đề
- Ứng dụng bị crash với lỗi: `java.lang.NullPointerException: Attempt to invoke virtual method 'java.lang.Integer com.example.fjobs.models.Profile.getMaHoSo()' on a null object reference`
- Lỗi xảy ra tại dòng 383 trong phương thức `updateProfile()` của `EditProfileFragment`
- Nguyên nhân: `currentProfile` là null khi người dùng cố gắng cập nhật hồ sơ

## Phân tích nguyên nhân
1. Phương thức `loadCurrentProfile()` là bất đồng bộ (async) - tải dữ liệu từ API
2. Người dùng có thể nhấn nút "Cập nhật hồ sơ" trước khi dữ liệu hồ sơ được tải hoàn tất
3. Khi `currentProfile` vẫn là null, việc gọi `currentProfile.getMaHoSo()` gây ra NullPointerException
4. Không có kiểm tra trạng thái trước khi thực hiện cập nhật

## Giải pháp áp dụng

### 1. Thêm kiểm tra null cho currentProfile
- Kiểm tra nếu `currentProfile == null` trước khi truy cập các thuộc tính
- Hiển thị thông báo cho người dùng biết phải chờ tải hồ sơ hoàn tất
- Gọi lại `loadCurrentProfile()` nếu hồ sơ chưa được tải

### 2. Thêm kiểm tra cho ID hồ sơ
- Kiểm tra nếu `currentProfile.getMaHoSo()` trả về null
- Hiển thị thông báo lỗi phù hợp nếu không thể xác định hồ sơ người dùng

### 3. Thêm trạng thái cập nhật (isUpdating)
- Thêm biến `isUpdating` để theo dõi trạng thái cập nhật
- Ngăn người dùng nhấn nút cập nhật nhiều lần trong khi đang cập nhật
- Vô hiệu hóa nút và thay đổi văn bản nút trong khi cập nhật
- Khôi phục trạng thái nút sau khi cập nhật hoàn tất (thành công hoặc thất bại)

### 4. Cải thiện trải nghiệm người dùng
- Hiển thị trạng thái "Đang cập nhật..." trên nút
- Bật/tắt nút cập nhật phù hợp trong các trạng thái khác nhau
- Hiển thị thông báo trạng thái rõ ràng cho người dùng

## Kết quả đạt được
- Ứng dụng không còn crash khi cập nhật hồ sơ
- Ngăn chặn lỗi NullPointerException
- Cải thiện trải nghiệm người dùng trong quá trình cập nhật hồ sơ
- Tránh việc gửi nhiều yêu cầu cập nhật đồng thời
- Xử lý lỗi một cách chuyên nghiệp và thông báo rõ ràng cho người dùng

## Kiểm tra
- Build ứng dụng thành công
- Kiểm tra logic xử lý trường hợp hồ sơ chưa được tải
- Kiểm tra trạng thái nút trong khi cập nhật
- Đảm bảo cập nhật hồ sơ hoạt động bình thường sau khi hồ sơ đã được tải

## Lợi ích
- Tăng độ ổn định của ứng dụng
- Cải thiện trải nghiệm người dùng
- Ngăn chặn crash không mong muốn
- Tăng tính chuyên nghiệp của ứng dụng