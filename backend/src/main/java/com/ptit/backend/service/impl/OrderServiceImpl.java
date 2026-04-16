package com.ptit.backend.service.impl;

import com.ptit.backend.config.VNPayProvider;
import com.ptit.backend.dto.request.OrderItemRequest;
import com.ptit.backend.dto.request.OrderRequest;
import com.ptit.backend.dto.request.OrderStatusUpdateRequest;
import com.ptit.backend.dto.response.OrderResponse;
import com.ptit.backend.entity.Book;
import com.ptit.backend.entity.BookItem;
import com.ptit.backend.entity.Order;
import com.ptit.backend.entity.OrderDetail;
import com.ptit.backend.entity.Promotion;
import com.ptit.backend.entity.User;
import com.ptit.backend.exception.AppException;
import com.ptit.backend.exception.BusinessException;
import com.ptit.backend.exception.ErrorCode;
import com.ptit.backend.mapper.OrderItemMapper;
import com.ptit.backend.mapper.OrderMapper;
import com.ptit.backend.repository.BookItemRepository;
import com.ptit.backend.repository.BookRepository;
import com.ptit.backend.repository.OrderDetailRepository;
import com.ptit.backend.repository.OrderRepository;
import com.ptit.backend.repository.PromotionRepository;
import com.ptit.backend.repository.UserRepository;
import com.ptit.backend.service.OrderService;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {

    private static final BigDecimal ZERO = BigDecimal.ZERO;
    private static final BigDecimal POINTS_DIVISOR = new BigDecimal("10000");

    private final VNPayProvider vnPayProvider;
    private final OrderRepository orderRepository;
    private final OrderDetailRepository orderDetailRepository;
    private final UserRepository userRepository;
    private final BookRepository bookRepository;
    private final BookItemRepository bookItemRepository;
    private final PromotionRepository promotionRepository;
    private final OrderMapper orderMapper;
    private final OrderItemMapper orderItemMapper;

    @Override
    public Page<OrderResponse> getOrders(Long userId, Pageable pageable) {
        Page<Order> orders = userId == null
                ? orderRepository.findAll(pageable)
                : orderRepository.findByUserUserId(userId, pageable);

        return orders.map(this::buildOrderResponse);
    }

    @Override
    public OrderResponse getOrderById(Long id) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.ORDER_NOT_FOUND));
        return buildOrderResponse(order);
    }

    @Override
    @Transactional
    public OrderResponse createOrder(OrderRequest request, HttpServletRequest httpServletRequest) {
        if (request.getUserId() == null) {
            throw new AppException(ErrorCode.INVALID_REQUEST, "Truong user_id la bat buoc");
        }
        if (request.getItems() == null || request.getItems().isEmpty()) {
            throw new AppException(ErrorCode.ORDER_ITEMS_EMPTY);
        }

        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));

        Order order = orderMapper.toEntity(request);
        order.setUser(user);
        order.setCreatedAt(request.getOrderDate() != null ? request.getOrderDate() : LocalDateTime.now());

        if (request.getOrderStatus() == null) {
            order.setOrderStatus("Cho duyet");
        }
        if (request.getPaymentStatus() == null) {
            order.setPaymentStatus("Chua thanh toan");
        }

        BigDecimal subTotal = ZERO;
        List<OrderDetail> orderDetails = new ArrayList<>();
        List<Book> booksToUpdate = new ArrayList<>();

        for (OrderItemRequest itemRequest : request.getItems()) {
            Book book = resolveBookForOrderItem(itemRequest);
            int quantity = itemRequest.getQuantity() != null ? itemRequest.getQuantity() : 1;
            if (quantity <= 0) {
                throw new AppException(ErrorCode.ORDER_ITEM_INVALID, "So luong mua phai lon hon 0");
            }

            int availableStock = book.getTotalStock() != null ? book.getTotalStock() : 0;
            if (availableStock < quantity) {
                throw new AppException(ErrorCode.INSUFFICIENT_STOCK);
            }

            BigDecimal unitPrice = itemRequest.getUnitPrice() != null
                    ? itemRequest.getUnitPrice()
                    : (book.getSellingPrice() != null ? book.getSellingPrice() : ZERO);

            BigDecimal lineTotal = unitPrice.multiply(BigDecimal.valueOf(quantity));
            subTotal = subTotal.add(lineTotal);

            OrderDetail detail = orderItemMapper.toEntity(itemRequest);
            detail.setBook(book);
            detail.setQuantity(quantity);
            detail.setUnitPrice(unitPrice);
            orderDetails.add(detail);

            book.setTotalStock(availableStock - quantity);
            booksToUpdate.add(book);
        }

        Promotion promotion = null;
        BigDecimal promotionDiscount = ZERO;
        if (request.getPromotionId() != null) {
            promotion = promotionRepository.findById(request.getPromotionId())
                    .orElseThrow(() -> new AppException(ErrorCode.PROMOTION_NOT_FOUND));
            validatePromotion(promotion, subTotal);
            promotionDiscount = calculatePromotionDiscount(promotion, subTotal);
            promotion.setUsedCount((promotion.getUsedCount() != null ? promotion.getUsedCount() : 0) + 1);
            promotionRepository.save(promotion);
        }

        BigDecimal totalAmount = subTotal.subtract(promotionDiscount);
        if (totalAmount.compareTo(ZERO) < 0) {
            totalAmount = ZERO;
        }

        order.setPromotion(promotion);
        order.setPromotionDiscountAmount(promotionDiscount);
        order.setPointDiscountAmount(ZERO);
        order.setPointsUsed(0);
        order.setTotalAmount(totalAmount);

        Order savedOrder = orderRepository.save(order);

        for (OrderDetail detail : orderDetails) {
            detail.setOrder(savedOrder);
        }
        List<OrderDetail> savedDetails = orderDetailRepository.saveAll(orderDetails);
        bookRepository.saveAll(booksToUpdate);

        int earnedPoints = totalAmount.divide(POINTS_DIVISOR, 0, RoundingMode.DOWN).intValue();
        user.setTotalPoints((user.getTotalPoints() != null ? user.getTotalPoints() : 0) + earnedPoints);
        userRepository.save(user);

        // 1. Map ra Response
        OrderResponse response = orderMapper.toResponse(savedOrder, savedDetails);

        // [THÊM MỚI] - 2. Xử lý logic sinh Link VNPay
        if ("VNPAY".equalsIgnoreCase(request.getPaymentMethod())) {
            // vnPayProvider là class bạn Inject vào Service để tạo URL (như các trao đổi trước)
            // Truyền savedOrder vào để lấy được ID đơn hàng (TxnRef) và Tổng tiền (Amount)
            String paymentUrl = vnPayProvider.createPaymentUrl(savedOrder, httpServletRequest);

            response.setPaymentUrl(paymentUrl);
        } else {
            response.setPaymentUrl(null); // Trả về null nếu là COD
        }

        return response;
    }


    @Transactional
    public void confirmOrderPayment(Long orderId, long vnpAmount, String responseCode, String transactionNo) {
        log.info("[OrderService] Bắt đầu xác nhận thanh toán cho Đơn hàng ID: {}, Số tiền VNP gửi: {}, ResponseCode: {}",
                orderId, vnpAmount, responseCode);

        // 1. Tìm đơn hàng
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> {
                    log.error("[OrderService] LỖI: Không tìm thấy Đơn hàng với ID: {}", orderId);
                    return new BusinessException(BusinessException.ErrorCode.ORDER_NOT_FOUND);
                });

        // 2. Kiểm tra số tiền
        // In ra cả 2 giá trị để so sánh nếu có lệch
        long expectedAmount = order.getTotalAmount().longValue();
        if (expectedAmount != vnpAmount) {
            log.error("[OrderService] LỖI: Số tiền không khớp! Trong DB: {}, VNPay gửi: {}", expectedAmount, vnpAmount);
            throw new BusinessException(BusinessException.ErrorCode.INVALID_AMOUNT);
        }

        // 3. Kiểm tra trạng thái đơn hàng (Đây là chỗ bạn đang bị vướng)
//        if (!"PENDING".equals(order.getPaymentStatus())) {
//            log.warn("[OrderService] CẢNH BÁO: Đơn hàng {} đã được xử lý trước đó. Trạng thái hiện tại: {}",
//                    orderId, order.getPaymentStatus());
//            throw new BusinessException(BusinessException.ErrorCode.ORDER_ALREADY_CONFIRMED);
//        }

        if ("Thanh toan thanh cong".equals(order.getPaymentStatus())) {
            log.warn("[OrderService] Đơn hàng {} đã THÀNH CÔNG từ trước. Bỏ qua để tránh xử lý trùng.", orderId);
            throw new BusinessException(BusinessException.ErrorCode.ORDER_ALREADY_CONFIRMED);
        }

        // 4. Xử lý cập nhật
        if ("00".equals(responseCode)) {
            log.info("[OrderService] Giao dịch thành công cho đơn hàng: {}", orderId);
            order.setPaymentStatus("Thanh toan thanh cong");
            // Bạn nên lưu thêm mã giao dịch của VNPay để đối soát sau này
            // order.setVnpTransactionNo(transactionNo);
        } else {
            log.warn("[OrderService] Giao dịch thất bại (Mã lỗi từ VNPay: {}) cho đơn hàng: {}", responseCode, orderId);
            order.setPaymentStatus("Thanh toan that bai");
        }

        orderRepository.save(order);
        log.info("[OrderService] Đã cập nhật trạng thái đơn hàng {} xuống Database thành công.", orderId);
    }
    @Override
    @Transactional
    public OrderResponse updateOrderStatus(Long id, OrderStatusUpdateRequest request) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.ORDER_NOT_FOUND));

        if (request.getOrderStatus() != null) {
            order.setOrderStatus(request.getOrderStatus());
        }
        if (request.getPaymentStatus() != null) {
            order.setPaymentStatus(request.getPaymentStatus());
        }

        Order updatedOrder = orderRepository.save(order);
        return buildOrderResponse(updatedOrder);
    }

    @Override
    @Transactional
    public void deleteOrder(Long id) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.ORDER_NOT_FOUND));

        List<OrderDetail> orderDetails = orderDetailRepository.findByOrderOrderId(id);
        orderDetailRepository.deleteAll(orderDetails);
        orderRepository.delete(order);
    }

    private OrderResponse buildOrderResponse(Order order) {
        List<OrderDetail> details = orderDetailRepository.findByOrderOrderId(order.getOrderId());
        return orderMapper.toResponse(order, details);
    }

    private Book resolveBookForOrderItem(OrderItemRequest itemRequest) {
        if (itemRequest.getBookId() != null) {
            return bookRepository.findById(itemRequest.getBookId())
                    .orElseThrow(() -> new AppException(ErrorCode.BOOK_NOT_FOUND));
        }

        if (itemRequest.getBookItemId() != null) {
            BookItem bookItem = bookItemRepository.findById(itemRequest.getBookItemId())
                    .orElseThrow(() -> new AppException(ErrorCode.BOOK_ITEM_NOT_FOUND));
            return bookItem.getBook();
        }

        throw new AppException(ErrorCode.ORDER_ITEM_INVALID, "Moi san pham phai co book_id hoac book_item_id");
    }

    private void validatePromotion(Promotion promotion, BigDecimal subTotal) {
        LocalDateTime now = LocalDateTime.now();

        if (promotion.getStatus() == null || promotion.getStatus() != 1) {
            throw new AppException(ErrorCode.PROMOTION_NOT_ACTIVE);
        }
        if (promotion.getStartDate() != null && now.isBefore(promotion.getStartDate())) {
            throw new AppException(ErrorCode.PROMOTION_NOT_STARTED);
        }
        if (promotion.getEndDate() != null && now.isAfter(promotion.getEndDate())) {
            throw new AppException(ErrorCode.PROMOTION_EXPIRED);
        }
        if (promotion.getUsageLimit() != null) {
            int usedCount = promotion.getUsedCount() != null ? promotion.getUsedCount() : 0;
            if (usedCount >= promotion.getUsageLimit()) {
                throw new AppException(ErrorCode.PROMOTION_USAGE_LIMIT_EXCEEDED);
            }
        }
        if (promotion.getMinOrderValue() != null && subTotal.compareTo(promotion.getMinOrderValue()) < 0) {
            throw new AppException(ErrorCode.PROMOTION_MIN_ORDER_NOT_MET);
        }
    }

    private BigDecimal calculatePromotionDiscount(Promotion promotion, BigDecimal subTotal) {
        if (promotion.getDiscountPercent() == null) {
            return ZERO;
        }

        BigDecimal discount = subTotal
                .multiply(promotion.getDiscountPercent())
                .divide(new BigDecimal("100"), 2, RoundingMode.HALF_UP);

        if (promotion.getMaxDiscountAmount() != null && discount.compareTo(promotion.getMaxDiscountAmount()) > 0) {
            discount = promotion.getMaxDiscountAmount();
        }

        return discount;
    }
}

