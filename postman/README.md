# Bộ test Postman cho backend Bookstore

## Thứ tự import
1. `postman/bookstore-local.postman_environment.json`
2. `postman/bookstore-api.postman_collection.json`

## Thứ tự chạy khuyến nghị
1. `00 - Auth`
2. `01 - Security Guard`
3. `02 - Users`
4. `03 - Categories`
5. `04 - Books`
6. `05 - Orders`
7. `06 - Rentals`

## Biến quan trọng
- `adminUsername` / `adminPassword`: tài khoản admin trên môi trường local
- `userUsername` / `userPassword`: sẽ được ghi đè sau request `Register User`
- `managedUserId`, `managedUsername`, `managedUserEmail`: tài khoản được tạo từ luồng đăng ký auth
- `adminToken`, `userToken`: JWT dùng cho các API cần xác thực
- `seedCategoryId`, `seedBookId`, `seedBookItemId`, `seedOrderId`, `seedRentalId`: ID mẫu để test các API chi tiết

## Ghi chú
- Bộ test này chỉ nhắm vào 6 controller hiện đang được implement trong backend.
- Các request create / update / delete sẽ lưu ID trả về vào environment để dùng cho các bước sau.
- `05 - Orders` đã có kịch bản kiểm thử ngưỡng tồn kho theo atomic update: tạo sách `total_stock=1`, lần đặt đầu thành công, lần đặt cùng dữ liệu tiếp theo phải lỗi `1005`.
- Nếu dữ liệu auth local của bạn không khớp `admin / Admin@123`, hãy cập nhật lại environment trước khi chạy.
- Request tạo rental thành công cần `book_item` có trạng thái khớp điều kiện service hiện tại (`San sang`).
