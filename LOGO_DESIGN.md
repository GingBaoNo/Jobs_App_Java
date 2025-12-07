# Thiết Kế Logo Ứng Dụng FJobs

## Giới thiệu
Logo FJobs được thiết kế đặc biệt cho ứng dụng tìm việc làm nhằm thể hiện sự chuyên nghiệp, tin cậy và mục tiêu kết nối ứng viên với nhà tuyển dụng.

## Mô tả Logo
- **Hình dạng chính**: Cặp vali công việc (briefcase) - biểu tượng cho công việc và nghề nghiệp
- **Màu sắc**: 
  - Nền vali màu trắng (đại diện cho sự trong sáng, chuyên nghiệp)
  - Bên trong vali màu xanh (#3B82F6) - màu thương hiệu của ứng dụng
  - Dấu đô la ($) bên trong vali - đại diện cho thu nhập, giá trị công việc
- **Ý nghĩa**:
  - Vali công việc đại diện cho cơ hội việc làm
  - Dấu đô la thể hiện giá trị tài chính từ công việc
  - Tổ hợp này truyền tải thông điệp: "FJobs - Nơi tìm kiếm cơ hội việc làm có giá trị"

## Tệp tài nguyên
- Tệp: `app/src/main/res/drawable/fjobs_logo.xml`
- Định dạng: Vector drawable (SVG)
- Tương thích: Tất cả độ phân giải màn hình

## Kích thước và sử dụng
- Kích thước vector chuẩn: 24x24dp (có thể mở rộng tùy nhu cầu)
- Được sử dụng trong splash screen của ứng dụng
- Có thể tái sử dụng cho biểu tượng ứng dụng, tiêu đề, footer, v.v.

## Triển khai
Logo được tích hợp vào splash screen như một hình ảnh vector drawable:
```xml
<ImageView
    android:id="@+id/iv_logo"
    android:layout_width="150dp"
    android:layout_height="150dp"
    android:src="@drawable/fjobs_logo" />
```

## Hiệu ứng hoạt hình
- Trong splash screen, logo có hiệu ứng phóng to mượt mà khi màn hình được tải
- Tạo cảm giác hiện đại và chuyên nghiệp cho người dùng

## Thông điệp thương hiệu
FJobs không chỉ là một ứng dụng tìm việc thông thường - mà là nơi:
- Kết nối ứng viên với cơ hội việc làm có giá trị
- Mang lại thu nhập xứng đáng
- Tạo nền tảng nghề nghiệp bền vững
- Giúp người lao động phát triển sự nghiệp

Logo là sự kết hợp giữa biểu tượng công việc (vali) và giá trị tài chính (dấu đô la), truyền tải rõ ràng sứ mệnh của ứng dụng trong hệ sinh thái việc làm hiện đại.