# Bộ Test Postman

## Tệp sử dụng
- `docs/postman/bookstore-api.postman_collection.json`
- `docs/postman/bookstore-local.postman_environment.json`
- `database/seed_auth_users.sql`

## Phạm vi URL đã có trong collection
- Auth:
  - `POST /api/v1/auth/login`
- Users:
  - `GET /api/v1/users`
  - `GET /api/v1/users/{id}`
  - `POST /api/v1/users`
  - `PATCH /api/v1/users/{id}`
  - `DELETE /api/v1/users/{id}`
- Categories:
  - `GET /api/v1/categories`
  - `GET /api/v1/categories/{id}`
  - `POST /api/v1/categories`
  - `PATCH /api/v1/categories/{id}`
  - `DELETE /api/v1/categories/{id}`
- Books:
  - `GET /api/v1/books`
  - `GET /api/v1/books/{id}`
  - `POST /api/v1/books`
  - `PATCH /api/v1/books/{id}`
  - `DELETE /api/v1/books/{id}`
- Orders:
  - `GET /api/v1/orders`
  - `GET /api/v1/orders/{id}`
  - `POST /api/v1/orders`
  - `PATCH /api/v1/orders/{id}`
  - `DELETE /api/v1/orders/{id}`
- Rentals:
  - `GET /api/v1/rentals`
  - `GET /api/v1/rentals/{id}`
  - `POST /api/v1/rentals`
  - `PATCH /api/v1/rentals/{id}`
  - `DELETE /api/v1/rentals/{id}`

## Quy trình chạy
1. Chạy script seed: `database/seed_auth_users.sql`
2. Import collection và environment `bookstore-local.postman_environment.json`
3. Chạy theo đúng thứ tự folder:
   1. `00 - Auth`
   2. `01 - Security Guard`
   3. `02 - Categories Workflow (Admin)`
   4. `03 - Books Workflow`
   5. `04 - Users Workflow (Đăng ký + Quản lý)`
   6. `05 - Orders Workflow`
   7. `06 - Rentals Workflow`
   8. `99 - Cleanup Workflow`

## Đăng nhập
- Đăng nhập sử dụng endpoint: `POST /api/v1/auth/login`.

## Dữ liệu mặc định để test nhanh
- `admin / Admin@123`
- `user / User@123`

