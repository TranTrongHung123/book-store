package com.ptit.backend.config;

import com.ptit.backend.entity.Book;
import com.ptit.backend.entity.Order;
import com.ptit.backend.entity.OrderDetail;
import com.ptit.backend.repository.BookRepository;
import com.ptit.backend.repository.OrderDetailRepository;
import com.ptit.backend.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class OrderCleanupScheduler {
    private static final String PAYMENT_STATUS_UNPAID = "Chưa thanh toán";
    private static final String PAYMENT_STATUS_FAILED = "Thanh toán thất bại";
    private static final String PAYMENT_METHOD_VNPAY = "VNPAY";
    private static final String ORDER_STATUS_CANCELLED = "Đã hủy";

    private final OrderRepository orderRepository;
    private final BookRepository bookRepository;
    private final OrderDetailRepository orderDetailRepository;

    // Chạy mỗi phút (cron: "0 * * * * *")
    @Scheduled(cron = "0 * * * * *")
    @Transactional
    public void cleanupExpiredOrders() {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime minutesAgo = now.minusMinutes(2);
        List<Order> expiredOrders = orderRepository.findByPaymentStatusAndPaymentMethodAndCreatedAtBefore(
                PAYMENT_STATUS_UNPAID, PAYMENT_METHOD_VNPAY, minutesAgo
        );

        for (Order order : expiredOrders) {
            if (ORDER_STATUS_CANCELLED.equals(order.getOrderStatus())) {
                continue; // Bỏ qua nếu đã hủy
            }

            // Bỏ qua đơn flash sale, đã có RabbitMQ hủy trễ xử lý
            if (orderDetailRepository.existsFlashSaleItemByOrderId(order.getOrderId())) {
                log.debug("[OrderCleanup] Skipping flash sale order {}", order.getOrderId());
                continue;
            }

            List<OrderDetail> orderDetails = orderDetailRepository.findByOrderOrderId(order.getOrderId());

            for (OrderDetail detail : orderDetails) {
                Book book = detail.getBook();
                book.setTotalStock(book.getTotalStock() + detail.getQuantity());
                bookRepository.save(book);
            }

            order.setOrderStatus(ORDER_STATUS_CANCELLED);
            order.setPaymentStatus(PAYMENT_STATUS_FAILED);
            orderRepository.save(order);

            log.info("[OrderCleanup] Đã hủy đơn hàng {} do timeout thanh toán.", order.getOrderId());
        }
    }
}
