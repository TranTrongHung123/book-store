# Bộ test Postman cho Bookstore backend

## File cần import
1. `postman/bookstore-local.postman_environment.json`
2. `postman/bookstore-api.postman_collection.json`
3. `postman/bookstore-admin-local.postman_environment.json` (bộ riêng cho admin)
4. `postman/bookstore-admin-api.postman_collection.json` (bộ riêng cho admin)

## Tài liệu API admin
- `postman/admin-api.md`: mô tả mục đích, URL, input, output cho toàn bộ API admin.

## Thứ tự chạy khuyến nghị
1. `00 - Auth`
2. `01 - Categories`
3. `02 - Books`
4. `03 - Users`
5. `04 - Orders`
6. `05 - Rentals`
7. `06 - Firebase`
8. `07 - Support`
9. `08 - Staff Dashboard`
10. `09 - Security Guard`
11. `10 - Cleanup`

## Biến môi trường quan trọng
- `baseUrl`: URL backend, mặc định `http://localhost:8081`
- `adminUsername` / `adminPassword`: tài khoản admin dùng cho API quản trị
- `managedUserPassword`: mật khẩu cho user tạo mới trong bước đăng ký
- `adminToken`, `userToken`: JWT được lưu tự động sau khi login
- `managedUserId`, `categoryId`, `bookId`, `orderId`, `rentalId`, `conversationId`: ID phát sinh trong quá trình test
- `seedBookItemId`: `book_item_id` có thể thuê được (mặc định `1` theo seed)

## Danh sách API có trong bộ test

### 1) Auth
- `POST /api/v1/auth/login`
- `POST /api/v1/auth/register`

### 2) Categories
- `GET /api/v1/categories`
- `GET /api/v1/categories/{id}`
- `POST /api/v1/categories`
- `PATCH /api/v1/categories/{id}`
- `DELETE /api/v1/categories/{id}`

### 3) Books
- `GET /api/v1/books`
- `GET /api/v1/books/{id}`
- `POST /api/v1/books`
- `PATCH /api/v1/books/{id}`
- `DELETE /api/v1/books/{id}`

### 4) Users
- `GET /api/v1/users`
- `GET /api/v1/users/{id}`
- `PATCH /api/v1/users/{id}`
- `DELETE /api/v1/users/{id}`

### 5) Orders
- `GET /api/v1/orders`
- `GET /api/v1/orders/{id}`
- `POST /api/v1/orders`
- `PATCH /api/v1/orders/{id}`
- `DELETE /api/v1/orders/{id}`

### 6) Rentals
- `GET /api/v1/rentals`
- `GET /api/v1/rentals/{id}`
- `POST /api/v1/rentals`
- `PATCH /api/v1/rentals/{id}`
- `DELETE /api/v1/rentals/{id}`

### 7) Firebase
- `GET /api/v1/firebase/custom-token`

### 8) Support
- `POST /api/v1/support/open`
- `POST /api/v1/support/claim-waiting`
- `PATCH /api/v1/support/{conversationId}/close`

### 9) Staff Dashboard
- `GET /api/v1/staff/dashboard/summary`

### 10) Security Guard (kiểm thử phân quyền)
- `GET /api/v1/users` không token -> `401`
- `GET /api/v1/firebase/custom-token` không token -> `401`
- `POST /api/v1/support/open` không token -> `401`
- `GET /api/v1/staff/dashboard/summary` với `userToken` -> `403`
- `POST /api/v1/support/claim-waiting` với `userToken` -> `403`
- `POST /api/v1/categories` với `userToken` -> `403`

## Lưu ý quan trọng
- API Firebase/Support cần cấu hình Firebase Admin SDK hợp lệ (`GOOGLE_APPLICATION_CREDENTIALS` hoặc `FIREBASE_SERVICE_ACCOUNT_PATH`).
- Nếu tài khoản admin local không phải `admin01 / Admin@123`, hãy sửa lại trong environment trước khi chạy.
- Bộ test có bước `Cleanup` để xóa dữ liệu test đã tạo.
- API rentals đang dùng mã thành công `code = 0` theo implementation hiện tại, không phải `1000`.
