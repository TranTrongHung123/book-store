# Quy trình cấu hình MySQL hỗ trợ Tiếng Việt (2 ký tự)

Tài liệu này hướng dẫn cách thay đổi cấu hình mặc định của MySQL để hỗ trợ tìm kiếm toàn văn (**Full-text Search**) cho các từ đơn Tiếng Việt có độ dài **2 ký tự** (mặc định là 3).

Giúp hệ thống sử dụng **RAG (Retrieval-Augmented Generation)** để tư vấn sách dựa trên ngữ nghĩa.

---

## Bước 1: Truy cập thư mục cấu hình hệ thống

* Nhấn tổ hợp phím **Windows + R**
* Dán đường dẫn sau và nhấn Enter:

```plaintext
C:\ProgramData\MySQL\MySQL Server 8.0
```

> ⚠️ Lưu ý: Nếu dùng phiên bản khác (ví dụ 8.4), hãy thay đổi số phiên bản tương ứng.

---

## Bước 2: Chỉnh sửa file `my.ini`

1. Chuột phải vào file `my.ini` → chọn **Open with → Notepad**
2. Nhấn **Ctrl + F**, tìm từ khóa:

```
[mysqld]
```

3. Thêm dòng cấu hình sau ngay bên dưới:

```ini
innodb_ft_min_token_size = 2
```

### Mẹo xử lý lỗi "Access Denied"

Nếu Windows không cho phép lưu trực tiếp:

1. Chọn **File → Save As** → lưu ra Desktop
2. Copy file `my.ini` từ Desktop
3. Dán đè lại vào thư mục gốc
4. Chọn **Continue** khi được yêu cầu quyền Administrator

---

## Bước 3: Khởi động lại dịch vụ MySQL

Cấu hình mới chỉ có tác dụng sau khi restart service:

1. Nhấn **Windows + R**
2. Gõ:

```
services.msc
```

3. Tìm dịch vụ **MySQL80** (hoặc tên tương tự)
4. Chuột phải → chọn **Restart**

---

## Kiểm tra cấu hình đã được áp dụng

```sql
SHOW VARIABLES LIKE 'innodb_ft_min_token_size';
```

Kết quả sẽ hiển thị:

* Cột Variable_name: innodb_ft_min_token_size

* Cột Value: 2

---

## Kiểm tra indexes đã được tạo thành công
```sql
SHOW INDEX FROM book WHERE Key_name = 'ft_book_search';
SHOW INDEX FROM author WHERE Key_name = 'ft_author_search';
```

---

## Thử Fulltext Search

Sau khi hoàn tất, chạy thử truy vấn:

```sql
SELECT book_id, title,
       MATCH(title, description) AGAINST('lập trình java' IN BOOLEAN MODE) as score
FROM book
WHERE MATCH(title, description) AGAINST('lập trình java' IN BOOLEAN MODE) > 0
ORDER BY score DESC
LIMIT 5;
```