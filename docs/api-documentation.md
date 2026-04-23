# TÀI LIỆU ĐẶC TẢ API - BOOKSTORE SYSTEM

---

## QUY ƯỚC CHUNG
*   **Content-Type:** `application/json`
*   **Base URL:** `/api/v1`
*   **Tính năng mặc định cần hỗ trợ cho các API GET (List):**
    *   `_page`, `_limit` (Phân trang)
    *   `_sort`, `_order` (Sắp xếp)
    *   `q` (Tìm kiếm Full-text)
    *   Lọc theo các trường (Ví dụ: `?category_name=Tiểu thuyết` hoặc `?user_id=1`)

---

## 0. XÁC THỰC (AUTH)
**Endpoint goc:** `/auth`

### 0.1. Đăng kí tài khoản mới
*   **Method:** `POST`
*   **URL:** `/auth/register`
*   **Input (Request Body):**
    ```json
    {
      "username": "newuser",
      "password": "12345678",
      "full_name": "Ten Khach Hang",
      "email": "email@example.com"
    }
    ```
*   **Output (201 Created):** `ApiResponse<UserResponse>`.

### 0.2. Đăng nhập
*   **Method:** `POST`
*   **URL:** `/auth/login`
*   **Output (200 OK):** `ApiResponse<LoginResponse>`.

---

## 1. QUẢN LÝ NGƯỜI DÙNG (USERS)
**Endpoint gốc:** `/users`

### 1.1. Lấy danh sách người dùng
*   **Method:** `GET`
*   **URL:** `/users`
*   **Output (200 OK):**
    ```json
    [
      {
        "id": "1",
        "username": "longdp",
        "role_id": 2,
        "full_name": "Đặng Phi Long",
        "email": "long@example.com",
        "phone": "0987654321",
        "total_points": 150,
        "status": 1
      }
    ]
    ```

### 1.2. Lấy thông tin chi tiết người dùng
*   **Method:** `GET`
*   **URL:** `/users/{id}`
*   **Output (200 OK):** Trả về 1 object user như cấu trúc trên.

### 1.3. Tạo người dùng mới (Quản trị)
*   **Method:** `POST`
*   **URL:** `/users`
*   **Input (Request Body):**
    ```json
    {
      "username": "newuser",
      "password": "raw_password",
      "role_id": 2,
      "full_name": "Tên Khách Hàng",
      "email": "email@example.com",
      "phone": "0987123456",
      "total_points": 0,
      "status": 1
    }
    ```
*   **Output (201 Created):** Object user vua tao (kem `id`).
*   **Ghi chú:** Dang ky public cho frontend su dung `/auth/register`.

### 1.4. Cập nhật một phần thông tin người dùng
*   **Method:** `PATCH`
*   **URL:** `/users/{id}`
*   **Input:** Các trường cần cập nhật (VD: `{"status": 0}`).
*   **Output (200 OK):** Object user sau khi cập nhật.

### 1.5. Xóa người dùng
*   **Method:** `DELETE`
*   **URL:** `/users/{id}`
*   **Output (200/204 OK)**

---

## 2. QUẢN LÝ ĐẦU SÁCH (BOOKS)
**Endpoint gốc:** `/books`

### 2.1. Lấy danh sách đầu sách
*   **Method:** `GET`
*   **URL:** `/books`
*   **Output (200 OK):**
    ```json
    [
      {
        "id": "1",
        "title": "Nhà Giả Kim",
        "author_name": "Paulo Coelho",
        "original_price": 129000,
        "selling_price": 99000,
        "category_name": "Tiểu thuyết",
        "cover_image": "url_anh_bia",
        "total_stock": 50,
        "sold_count": 15,
        "rental_count": 0,
        "description": "Mô tả ngắn...",
        "long_description": "Mô tả chi tiết...",
        "gallery_images": ["url_1", "url_2"]
      }
    ]
    ```

### 2.2. Lấy thông tin chi tiết đầu sách
*   **Method:** `GET`
*   **URL:** `/books/{id}`
*   **Output (200 OK):** 1 object book như trên.

### 2.3. Tạo mới đầu sách
*   **Method:** `POST`
*   **URL:** `/books`
*   **Input:** Object book (không có `id`).
*   **Output (201 Created):** Object book đã tạo.

### 2.4. Cập nhật thông tin đầu sách
*   **Method:** `PATCH` (hoặc `PUT`)
*   **URL:** `/books/{id}`
*   **Input:** Trường cần cập nhật (VD: `{"selling_price": 95000}`).
*   **Output (200 OK)**

### 2.5. Xóa đầu sách
*   **Method:** `DELETE`
*   **URL:** `/books/{id}`

---

## 3. QUẢN LÝ KHO & SÁCH VẬT LÝ (BOOK ITEMS)
**Endpoint gốc:** `/book_items`
*Ghi chú: Quản lý từng cuốn sách vật lý (có mã vạch riêng) dùng để bán hoặc cho thuê.*

### 3.1. Lấy danh sách sách vật lý
*   **Method:** `GET`
*   **URL:** `/book_items` (Hỗ trợ query: `/book_items?book_id={id}`)
*   **Output (200 OK):**
    ```json
    [
      {
        "id": "101",
        "book_id": "1",
        "barcode": "8935235226272",
        "condition_type": "Mới 100%",
        "is_for_rent": 0,
        "deposit_amount": null,
        "current_rental_price": 0,
        "status": "Sẵn sàng"
      }
    ]
    ```

### 3.2. Nhập kho (Thêm sách vật lý)
*   **Method:** `POST`
*   **URL:** `/book_items`
*   **Input:** Object book_item như trên (không `id`).
*   **Output (201 Created)**

### 3.3. Cập nhật trạng thái sách vật lý
*   **Method:** `PATCH`
*   **URL:** `/book_items/{id}`
*   **Input:** `{"status": "Đang thuê"}` hoặc `{"status": "Đã bán"}`
*   **Output (200 OK)**

---

## 4. QUẢN LÝ THỂ LOẠI (CATEGORIES)
**Endpoint gốc:** `/categories`

### 4.1. Lấy danh sách thể loại
*   **Method:** `GET`
*   **URL:** `/categories`
*   **Output (200 OK):**
    ```json
    [
      {
        "id": "1",
        "name": "Học tập"
      }
    ]
    ```

*(Các API POST, PATCH, DELETE cho categories cấu trúc tương tự chuẩn REST)*

---

## 5. QUẢN LÝ CHƯƠNG TRÌNH KHUYẾN MÃI (PROMOTIONS)
**Endpoint gốc:** `/promotions`

### 5.1. Lấy danh sách mã giảm giá
*   **Method:** `GET`
*   **URL:** `/promotions` (Hỗ trợ query: `/promotions?code=WELCOME10` để apply mã)
*   **Output (200 OK):**
    ```json
    [
      {
        "id": "1",
        "code": "WELCOME10",
        "title": "Giảm 10%",
        "subtitle": "Cho đơn từ 199k",
        "discount_percent": 10,
        "voucher_type": "discount",
        "applies_to_categories": ["Tiểu thuyết", "Khoa học"],
        "condition_text": "Áp dụng cho đơn từ 199.000đ, tối đa giảm 40.000đ.",
        "valid_from": "2026-04-01T00:00:00Z",
        "valid_to": "2026-06-30T23:59:59Z",
        "terms": "Mỗi khách hàng dùng 1 lần."
      }
    ]
    ```

---

## 6. QUẢN LÝ VOUCHER ĐÃ LƯU CỦA USER (USER VOUCHERS)
**Endpoint gốc:** `/user_vouchers`
*Ghi chú: Tách ra từ lỗi dữ liệu bị gộp nhầm trong bảng categories của FE.*

### 6.1. Lấy danh sách mã đã lưu của 1 User
*   **Method:** `GET`
*   **URL:** `/user_vouchers?username={username}`
*   **Output (200 OK):**
    ```json
    [
      {
        "id": "pR4ie9HhEgk",
        "username": "longdp",
        "promotionId": "5",
        "code": "PAYDAY8",
        "title": "Giảm 8%",
        "subtitle": "Ngày lương về",
        "discount_percent": 8,
        "voucher_type": "discount",
        "applies_to_categories": ["ALL"],
        "claimed_at": "2026-04-04T18:13:59.737Z"
      }
    ]
    ```

### 6.2. User lưu mã giảm giá mới
*   **Method:** `POST`
*   **URL:** `/user_vouchers`
*   **Input:** Object user_voucher như trên.
*   **Output (201 Created)**

---

## 7. QUẢN LÝ ĐƠN HÀNG BÁN (ORDERS)
**Endpoint gốc:** `/orders`

### 7.1. Lấy danh sách đơn hàng
*   **Method:** `GET`
*   **URL:** `/orders` (Hỗ trợ query: `/orders?user_id={id}`)
*   **Output (200 OK):**
    ```json
    [
      {
        "id": "501",
        "user_id": "1",
        "promotion_id": "1",
        "order_date": "2026-03-25T10:30:00Z",
        "total_amount": 89100,
        "shipping_address": "Hà Nội",
        "payment_method": "COD",
        "order_status": "Đang giao",
        "items": [
          {
            "book_item_id": "101",
            "title": "Nhà Giả Kim",
            "unit_price": 99000
          }
        ]
      }
    ]
    ```

### 7.2. Tạo đơn hàng mới (Checkout)
*   **Method:** `POST`

````
{
  "user_id": "{{managedUserId}}",
  "shipping_address": "Ha Noi",
  "payment_method": "VNPAY",
  "items": [
   {
      "book_id": "{{bookId}}",
      "quantity": 1,
      "unit_price": 76000
      }
    ]
  }
````
*   **URL:** `/orders`
*   **Input:** Gửi object order (bao gồm mảng `items`).
*   **Output (201 Created):
* Nếu "payment_method": "VNPAY" thì output là:
```json
{
    "code": 1000,
    "message": "Thanh cong",
    "result": {
        "id": 26,
        "user_id": 12,
        "promotion_id": null,
        "order_date": "2026-04-23T17:23:22.140086",
        "total_amount": 76000,
        "shipping_address": "Ha Noi",
        "payment_method": "VNPAY",
        "order_status": "Cho duyet",
        "payment_url": "https://sandbox.vnpayment.vn/paymentv2/vpcpay.html?vnp_Amount=7600000&vnp_Command=pay&vnp_CreateDate=20260417153636&vnp_CurrCode=VND&vnp_ExpireDate=20260417155136&vnp_IpAddr=192.168.0.101&vnp_Locale=vn&vnp_OrderInfo=Thanh+toan+don+hang+11&vnp_OrderType=250000&vnp_ReturnUrl=https%3A%2F%2Famends-omega-glitch.ngrok-free.dev%2Fapi%2Fv1%2Fpayment%2Fvnpay-callback&vnp_TmnCode=9L6JR45G&vnp_TxnRef=11&vnp_Version=2.1.0&vnp_SecureHash=abdae70c3e63b3d0e029c51583a38e26b2983ebfb0c9dc307dad9a6979e348f693b90ca103566dfabfbc16b68a7a5770805c72e525192faab3a1c59cd65826a0",
        "items": [
            {
                "book_item_id": 1,
                "title": "Cho Tôi Xin Một Vé Đi Tuổi Thơ",
                "unit_price": 76000,
                "quantity": 1
            }
        ]
    }
}
```
*  froned phải chạy vào đường dẫn của payment_url để thanh toán sau khi thanh toán xong thì chạy về đường dẫn `vnp_ReturnUrl` xử lý callback từ VNPAY.

### 7.3. Cập nhật trạng thái đơn hàng
*   **Method:** `PATCH`
*   **URL:** `/orders/{id}`
*   **Input:** `{"order_status": "Đã giao"}`
*   **Output (200 OK)**

---

## 8. QUẢN LÝ HỢP ĐỒNG THUÊ SÁCH (RENTALS)
**Endpoint gốc:** `/rentals`

### 8.1. Lấy danh sách hợp đồng thuê
*   **Method:** `GET`
*   **URL:** `/rentals` (Hỗ trợ query: `/rentals?user_id={id}` hoặc `?status=Đang thuê`)
*   **Output (200 OK):**
    ```json
    [
      {
        "id": "801",
        "user_id": "1",
        "book_item_id": "102",
        "book_title": "Nhà Giả Kim (Cũ 90%)",
        "rent_date": "2026-04-01T08:00:00Z",
        "due_date": "2026-04-15T08:00:00Z",
        "actual_deposit": 50000,
        "rental_fee": 15000,
        "payment_status": "Đã thu cọc",
        "status": "Đang thuê"
      }
    ]
    ```

### 8.2. Tạo hợp đồng thuê mới
*   **Method:** `POST`
*   **URL:** `/rentals`
*   **Input:** Object rental (không `id`).
*   **Output (201 Created)**

### 8.3. Cập nhật hợp đồng (Khách trả sách)
*   **Method:** `PATCH`
*   **URL:** `/rentals/{id}`
*   **Input:** `{"status": "Đã trả", "payment_status": "Đã hoàn cọc"}`
*   **Output (200 OK)**

---

## 9. QUẢN LÝ ĐÁNH GIÁ (REVIEWS)
**Endpoint gốc:** `/reviews`

### 9.1. Lấy danh sách đánh giá
*   **Method:** `GET`
*   **URL:** `/reviews` (Hỗ trợ query: `/reviews?book_id={id}`)
*   **Output (200 OK):**
    ```json
    [
      {
        "id": "301",
        "book_id": "1",
        "user_id": "1",
        "user_name": "Đặng Phi Long",
        "rating": 5,
        "comment": "Sách rất hay, đóng gói cẩn thận.",
        "is_approved": 1,
        "created_at": "2026-03-28T14:20:00Z"
      }
    ]
    ```

### 9.2. Gửi đánh giá mới
*   **Method:** `POST`
*   **URL:** `/reviews`
*   **Input:** Object review (mặc định `is_approved: 0` khi gửi).
*   **Output (201 Created)**

### 9.3. Duyệt đánh giá (Dành cho Admin)
*   **Method:** `PATCH`
*   **URL:** `/reviews/{id}`
*   **Input:** `{"is_approved": 1}`
*   **Output (200 OK)**

---

## 10. NESTED ROUTES

*   **Lấy lịch sử mua hàng của user:** `GET /users/{userId}/orders`
*   **Lấy lịch sử thuê sách của user:** `GET /users/{userId}/rentals`
*   **Lấy danh sách các cuốn vật lý của 1 đầu sách:** `GET /books/{bookId}/book_items`
*   **Lấy tất cả bình luận của 1 cuốn sách:** `GET /books/{bookId}/reviews`

---


## 11. QUẢN LÝ UPLOAD FILE (UPLOAD)
**Endpoint gốc:** `/upload`

### 11.1. Tải lên một hình ảnh
* **Method:** `POST`
* **URL:** `/upload/image`
* **Headers:**
    * `Content-Type: multipart/form-data`
* **Input (Form Data):**
    * `file`: File ảnh cần tải lên.
* **Output (200 OK):**
```json
{
  "url": "[https://res.cloudinary.com/.../bookstore_abc123.jpg](https://res.cloudinary.com/.../bookstore_abc123.jpg)",
  "public_id": "book_covers/bookstore_abc123"
}
```

### 11.2. Xóa hình ảnh đã tải lên
* **Method:** `DELETE`
* **URL:** `/upload/image`
* **Input:**
```json
{
  "public_id": "book_covers/bookstore_abc123"
}
```
* **Output (200 OK):**
```json
{
  "message": "Xóa hình ảnh thành công."
}
```
