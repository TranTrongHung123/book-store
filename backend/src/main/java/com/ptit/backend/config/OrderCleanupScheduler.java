package com.ptit.backend.config;  // Hoặc service

import com.ptit.backend.entity.Book;
import com.ptit.backend.entity.Order;
import com.ptit.backend.entity.OrderDetail;
import com.ptit.backend.repository.BookRepository;
import com.ptit.backend.repository.OrderDetailRepository;
import com.ptit.backend.repository.OrderRepository;  // Giả sử bạn có repo
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Component
@RequiredArgsConstructor
public class OrderCleanupScheduler {

    private final OrderRepository orderRepository;
    private final BookRepository bookRepository;
    private final OrderDetailRepository orderDetailRepository;

    // Chạy mỗi phút (cron: "0 * * * * *")
    // Bạn có thể điều chỉnh tần suất, ví dụ: mỗi 30 giây ("*/30 * * * * *")
    @Scheduled(cron = "0 * * * * *")
    @Transactional
    public void cleanupExpiredOrders() {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime minutesAgo = now.minusMinutes(2);
        // Tìm các đơn PENDING_PAYMENT đã hết hạn (dựa trên expireAt)
        List<Order> expiredOrders = orderRepository.findByPaymentStatusAndPaymentMethodAndCreatedAtBefore("Chưa thanh toán","VNPAY", minutesAgo);

        for (Order order : expiredOrders) {
            // Khôi phục kho

            if(order.getOrderStatus().equals("Đã hủy")) {
                continue; // Bỏ qua nếu đã hủy
            }
            List<OrderDetail> orderDetails = orderDetailRepository.findByOrderOrderId(order.getOrderId());

            for (OrderDetail detail : orderDetails) {
                Book book = detail.getBook();
                // Giảm stock: availableStock -= quantity
                book.setTotalStock(book.getTotalStock() + detail.getQuantity());
                bookRepository.save(book);
            }

            // Cập nhật trạng thái đơn hàng
            order.setOrderStatus("Đã hủy");
            orderRepository.save(order);

            // Log hoặc gửi thông báo (tùy chọn)
            System.out.println("Đã hủy đơn hàng " + order.getOrderId() + " do timeout thanh toán.");
        }
    }
}