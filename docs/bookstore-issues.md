# Các vấn đề và cách xử lí

## 1. Tranh chấp dữ liệu (Race Condition)

**Vấn đề:**  
Nhiều người cùng mua một quyển sách cuối cùng hoặc cùng áp dụng một mã giảm giá chỉ còn 1 lượt dùng.

**Hướng giải quyết:**  
Atomic update khi trừ tồn kho.

**Cách làm:**
- Trừ kho bằng một câu lệnh SQL duy nhất tại `BookRepository#decreaseStockIfAvailable`.
- Điều kiện `WHERE total_stock >= :quantity` đảm bảo không thể trừ âm khi có nhiều request đồng thời.
- Nếu câu lệnh update không tác động dòng nào, service ném `AppException(ErrorCode.INSUFFICIENT_STOCK)`.

---

## 2. Quản lý Giao dịch

**Vấn đề:**  
Đang tạo đơn hàng thành công nhưng đến bước trừ tồn kho thì lỗi mạng. Nếu không xử lý:
- Khách bị mất tiền mà không có đơn
- Hoặc có đơn nhưng kho không trừ

**Hướng giải quyết:**  
Annotation `@Transactional`.

**Cách làm:**
- Bọc toàn bộ phương thức đặt hàng trong `@Transactional`.
- Nếu bất kỳ bước nào thất bại, Spring Boot sẽ tự động rollback toàn bộ các lệnh SQL trước đó.
- Đưa database về trạng thái sạch ban đầu.

---

## 3. Đơn hàng "Ma" và Tồn kho ảo

**Vấn đề:**  
Khách bấm đặt hàng, hệ thống đã trừ kho nhưng khách không thanh toán (ví dụ: tắt trình duyệt ở trang VNPay).  
→ Sách bị giữ chỗ, người khác không mua được.

**Hướng giải quyết:**  
Spring Scheduler (Cron Job).

**Cách làm:**
- Viết một hàm tự động chạy mỗi 1–5 phút.
- Quét các đơn hàng có trạng thái `"Chờ thanh toán"` mà:
    - Thời gian tạo đã quá 15 phút
- Tự động:
    - Chuyển trạng thái thành `"Đã hủy"`
    - Cộng trả lại số lượng vào kho

---

## 4. Xử lý thanh toán an toàn

**Vấn đề:**  
Kẻ xấu giả mạo request gửi đến API để báo `"Đã thanh toán thành công"` nhằm lấy sách miễn phí.

**Hướng giải quyết:**  
Checksum / Digital Signature Verification.

**Cách làm:**
- Luôn kiểm tra chữ ký điện tử (Hash) từ VNPay / MoMo gửi kèm trong Webhook.
- Backend phải:
    - Chủ động gọi ngược lại API của cổng thanh toán
    - Xác nhận trạng thái giao dịch thực sự

---

## 5. AI Context

**Vấn đề:**  
Gemini API không tự nhớ câu hỏi trước đó.  
Ví dụ:
- Khách hỏi: "Quyển này giá bao nhiêu?"
- Nhưng trước đó hỏi về "Đắc Nhân Tâm"  
  → Bot không hiểu "quyển này" là gì

**Hướng giải quyết:**  
Message History Buffer.

**Cách làm:**
- Lấy $N$ tin nhắn gần nhất từ bảng `chat_message` dựa trên `session_id`
- Nối chúng lại thành một chuỗi văn bản
- Gửi kèm cùng câu hỏi hiện tại lên Gemini API  