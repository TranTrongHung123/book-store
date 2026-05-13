package com.ptit.backend.service.impl;

import com.ptit.backend.config.VNPayProvider;
import com.ptit.backend.dto.request.FlashSaleReserveRequest;
import com.ptit.backend.dto.response.FlashSaleActiveResponse;
import com.ptit.backend.dto.response.FlashSaleActiveResponse.FlashSaleActiveItemResponse;
import com.ptit.backend.dto.response.FlashSaleReservationStatusResponse;
import com.ptit.backend.dto.response.FlashSaleReserveResponse;
import com.ptit.backend.entity.Book;
import com.ptit.backend.entity.FlashSaleCampaign;
import com.ptit.backend.entity.FlashSaleItem;
import com.ptit.backend.entity.Order;
import com.ptit.backend.entity.OrderDetail;
import com.ptit.backend.entity.PaymentTransaction;
import com.ptit.backend.entity.User;
import com.ptit.backend.exception.AppException;
import com.ptit.backend.exception.ErrorCode;
import com.ptit.backend.messaging.FlashSaleMessage;
import com.ptit.backend.messaging.FlashSaleMessagePublisher;
import com.ptit.backend.repository.FlashSaleCampaignRepository;
import com.ptit.backend.repository.FlashSaleItemRepository;
import com.ptit.backend.repository.OrderDetailRepository;
import com.ptit.backend.repository.OrderRepository;
import com.ptit.backend.repository.PaymentTransactionRepository;
import com.ptit.backend.repository.UserRepository;
import com.ptit.backend.service.FlashSaleCustomerService;
import com.ptit.backend.service.FlashSaleSseService;
import com.ptit.backend.service.FlashSaleStockRedisService;
import jakarta.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class FlashSaleCustomerServiceImpl implements FlashSaleCustomerService {

    private static final String PAYMENT_METHOD_VNPAY = "VNPAY";
    private static final String PAYMENT_STATUS_UNPAID = "Chưa thanh toán";
    private static final String PAYMENT_STATUS_PAID = "Đã thanh toán";
    private static final String PAYMENT_STATUS_FAILED = "Thanh toán thất bại";
    private static final String ORDER_STATUS_PENDING = "Chờ duyệt";
    private static final String ORDER_STATUS_WAITING_DELIVERY = "Chờ giao hàng";
    private static final String ORDER_STATUS_CANCELLED = "Đã hủy";
    private static final String TXN_STATUS_PENDING = "PENDING";

    private final FlashSaleStockRedisService redisService;
    private final FlashSaleSseService sseService;
    private final FlashSaleMessagePublisher messagePublisher;
    private final FlashSaleCampaignRepository campaignRepository;
    private final FlashSaleItemRepository flashSaleItemRepository;
    private final OrderRepository orderRepository;
    private final OrderDetailRepository orderDetailRepository;
    private final PaymentTransactionRepository paymentTransactionRepository;
    private final UserRepository userRepository;
    private final VNPayProvider vnPayProvider;

    @Value("${flash-sale.reservation-ttl-seconds:300}")
    private int reservationTtlSeconds;

    // ======= GET ACTIVE CAMPAIGNS =======

    @Override
    public List<FlashSaleActiveResponse> getActiveCampaigns(Long userId) {
        LocalDateTime now = LocalDateTime.now();
        List<FlashSaleCampaign> activeCampaigns = campaignRepository.findAllActiveCampaigns(now);
        List<FlashSaleActiveResponse> responses = new ArrayList<>();

        for (FlashSaleCampaign campaign : activeCampaigns) {
            // JIT Activation: If campaign should be active but status is UPCOMING, initialize it
            if ("UPCOMING".equals(campaign.getStatus())) {
                initializeCampaignInRedis(campaign);
            }

            List<FlashSaleItem> items = flashSaleItemRepository.findByCampaignCampaignId(campaign.getCampaignId());
            List<FlashSaleActiveItemResponse> itemResponses = new ArrayList<>();

            for (FlashSaleItem item : items) {
                // Read stock from Redis (not MySQL) during active campaign
                int remainingStock = redisService.getStock(item.getFlashSaleItemId());
                Book book = item.getBook();

                boolean isPurchased = false;
                if (userId != null) {
                    int userQty = redisService.getUserPurchaseCount(item.getFlashSaleItemId(), userId);
                    int maxPerUser = item.getMaxPerUser() != null ? item.getMaxPerUser() : 1;
                    isPurchased = userQty >= maxPerUser;
                }

                itemResponses.add(FlashSaleActiveItemResponse.builder()
                        .flashSaleItemId(item.getFlashSaleItemId())
                        .bookId(book.getBookId())
                        .bookTitle(book.getTitle())
                        .coverImage(book.getCoverImage())
                        .originalPrice(book.getSellingPrice())
                        .flashSalePrice(item.getFlashSalePrice())
                        .totalQuantity(item.getQuantity())
                        .remainingStock(remainingStock)
                        .soldQuantity(item.getQuantity() - remainingStock)
                        .maxPerUser(item.getMaxPerUser())
                        .soldOut(remainingStock <= 0)
                        .isPurchased(isPurchased)
                        .build());
            }

            responses.add(FlashSaleActiveResponse.builder()
                    .campaignId(campaign.getCampaignId())
                    .name(campaign.getName())
                    .startsAt(campaign.getStartTime())
                    .endsAt(campaign.getEndTime())
                    .items(itemResponses)
                    .build());
        }

        return responses;
    }

    private void initializeCampaignInRedis(FlashSaleCampaign campaign) {
        log.info("[FlashSale] JIT Initialization for campaign={} '{}'", campaign.getCampaignId(), campaign.getName());
        campaign.setStatus("ACTIVE");
        campaignRepository.save(campaign);

        List<FlashSaleItem> items = flashSaleItemRepository.findByCampaignCampaignId(campaign.getCampaignId());
        for (FlashSaleItem item : items) {
            int soldQty = item.getSoldQuantity() != null ? item.getSoldQuantity() : 0;
            int availableStock = item.getQuantity() - soldQty;
            redisService.initializeStock(item.getFlashSaleItemId(), Math.max(0, availableStock));
        }
    }

    // ======= RESERVE STOCK =======

    @Override
    @Transactional
    public FlashSaleReserveResponse reserveStock(Long userId, FlashSaleReserveRequest request, HttpServletRequest httpRequest) {
        // 1. Validate flash sale item exists and campaign is active
        FlashSaleItem flashSaleItem = flashSaleItemRepository.findById(request.getFlashSaleItemId())
                .orElseThrow(() -> new AppException(ErrorCode.FLASH_SALE_ITEM_NOT_FOUND));

        FlashSaleCampaign campaign = flashSaleItem.getCampaign();
        String status = campaign.getStatus();
        LocalDateTime now = LocalDateTime.now();

        // Check if truly active (either ACTIVE or UPCOMING but it's time)
        boolean isTime = !now.isBefore(campaign.getStartTime()) && !now.isAfter(campaign.getEndTime());
        boolean isActive = "ACTIVE".equals(status);
        boolean isUpcomingButShouldBeActive = "UPCOMING".equals(status) && isTime;

        if (!isActive && !isUpcomingButShouldBeActive) {
            throw new AppException(ErrorCode.FLASH_SALE_NOT_ACTIVE);
        }

        // If it's UPCOMING but should be active, init it JIT
        if (isUpcomingButShouldBeActive) {
            initializeCampaignInRedis(campaign);
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));

        int qty = request.getQuantity();
        int maxPerUser = flashSaleItem.getMaxPerUser() != null ? flashSaleItem.getMaxPerUser() : 1;

        // 2. Atomic Redis stock reservation
        long result = redisService.reserveStock(flashSaleItem.getFlashSaleItemId(), userId, qty, maxPerUser);

        switch ((int) result) {
            case -1 -> throw new AppException(ErrorCode.FLASH_SALE_DUPLICATE_REQUEST);
            case -2 -> throw new AppException(ErrorCode.FLASH_SALE_MAX_PER_USER);
            case -3 -> throw new AppException(ErrorCode.FLASH_SALE_SOLD_OUT);
        }

        // 3. Create Order in MySQL (PENDING status)
        BigDecimal unitPrice = flashSaleItem.getFlashSalePrice();
        BigDecimal totalAmount = unitPrice.multiply(BigDecimal.valueOf(qty));

        Order order = Order.builder()
                .user(user)
                .totalAmount(totalAmount)
                .shippingAddress(request.getShippingAddress())
                .paymentMethod(PAYMENT_METHOD_VNPAY)
                .paymentStatus(PAYMENT_STATUS_UNPAID)
                .orderStatus(ORDER_STATUS_PENDING)
                .promotionDiscountAmount(BigDecimal.ZERO)
                .pointDiscountAmount(BigDecimal.ZERO)
                .pointsUsed(0)
                .build();
        Order savedOrder = orderRepository.save(order);

        // 4. Create OrderDetail with flash_sale_item_id
        OrderDetail detail = OrderDetail.builder()
                .order(savedOrder)
                .book(flashSaleItem.getBook())
                .flashSaleItem(flashSaleItem)
                .quantity(qty)
                .unitPrice(unitPrice)
                .build();
        orderDetailRepository.save(detail);

        // 5. Create PaymentTransaction (PENDING)
        PaymentTransaction paymentTxn = PaymentTransaction.builder()
                .order(savedOrder)
                .provider("VNPAY")
                .amount(totalAmount)
                .status(TXN_STATUS_PENDING)
                .build();
        paymentTransactionRepository.save(paymentTxn);

        // 6. Generate VNPay URL
        String paymentUrl = vnPayProvider.createPaymentUrl(savedOrder, httpRequest);

        // 7. Store reservation in Redis with TTL
        String reservationId = UUID.randomUUID().toString();
        Map<String, String> reservationData = new HashMap<>();
        reservationData.put("userId", userId.toString());
        reservationData.put("flashSaleItemId", flashSaleItem.getFlashSaleItemId().toString());
        reservationData.put("orderId", savedOrder.getOrderId().toString());
        reservationData.put("quantity", String.valueOf(qty));
        reservationData.put("paymentUrl", paymentUrl);
        redisService.setReservation(reservationId, reservationData, reservationTtlSeconds);

        // 8. Publish delayed cancel to RabbitMQ
        FlashSaleMessage cancelMessage = FlashSaleMessage.builder()
                .reservationId(reservationId)
                .flashSaleItemId(flashSaleItem.getFlashSaleItemId())
                .userId(userId)
                .orderId(savedOrder.getOrderId())
                .quantity(qty)
                .type("CANCEL")
                .build();
        messagePublisher.publishDelayedCancel(cancelMessage, reservationTtlSeconds * 1000L);

        // 9. Broadcast stock update via SSE
        int newStock = redisService.getStock(flashSaleItem.getFlashSaleItemId());
        sseService.broadcastStockUpdate(flashSaleItem.getFlashSaleItemId(), newStock, newStock <= 0);

        long expiresAt = Instant.now().getEpochSecond() + reservationTtlSeconds;

        log.info("[FlashSale] Reserved: user={}, item={}, order={}, reservation={}",
                userId, flashSaleItem.getFlashSaleItemId(), savedOrder.getOrderId(), reservationId);

        return FlashSaleReserveResponse.builder()
                .reservationId(reservationId)
                .orderId(savedOrder.getOrderId())
                .paymentUrl(paymentUrl)
                .expiresAt(expiresAt)
                .countdownSeconds(reservationTtlSeconds)
                .build();
    }

    // ======= GET RESERVATION STATUS =======

    @Override
    public FlashSaleReservationStatusResponse getReservationStatus(String reservationId, Long userId) {
        Map<String, String> data = redisService.getReservation(reservationId);

        if (data.isEmpty()) {
            // Check if order exists and its status
            // Reservation expired or was committed
            return FlashSaleReservationStatusResponse.builder()
                    .reservationId(reservationId)
                    .status("EXPIRED")
                    .remainingSeconds(0)
                    .build();
        }

        Long storedUserId = Long.parseLong(data.get("userId"));
        if (!storedUserId.equals(userId)) {
            throw new AppException(ErrorCode.FORBIDDEN, "Khong co quyen xem reservation nay");
        }

        long ttl = redisService.getReservationTtl(reservationId);
        Long orderId = Long.parseLong(data.get("orderId"));

        // Check if order already paid
        Order order = orderRepository.findById(orderId).orElse(null);
        String status = "PENDING";
        if (order != null && PAYMENT_STATUS_PAID.equals(order.getPaymentStatus())) {
            status = "SUCCESS";
        }

        return FlashSaleReservationStatusResponse.builder()
                .reservationId(reservationId)
                .orderId(orderId)
                .status(status)
                .remainingSeconds(Math.max(0, ttl))
                .paymentUrl(data.get("paymentUrl"))
                .build();
    }

    // ======= COMMIT RESERVATION (after VNPay success) =======

    @Override
    @Transactional
    public void commitReservation(String reservationId) {
        Map<String, String> data = redisService.getReservation(reservationId);
        if (data.isEmpty()) {
            log.warn("[FlashSale] Commit called but reservation={} not found (already committed or expired)", reservationId);
            return; // Idempotent — already processed
        }

        Long flashSaleItemId = Long.parseLong(data.get("flashSaleItemId"));
        Long userId = Long.parseLong(data.get("userId"));
        Long orderId = Long.parseLong(data.get("orderId"));
        int quantity = Integer.parseInt(data.get("quantity"));

        // Delete reservation (stock permanently deducted)
        redisService.deleteReservation(reservationId);
        redisService.commitUserPurchase(flashSaleItemId, userId);

        // Update order status
        Order order = orderRepository.findById(orderId).orElse(null);
        if (order != null) {
            order.setPaymentStatus(PAYMENT_STATUS_PAID);
            order.setOrderStatus(ORDER_STATUS_WAITING_DELIVERY);
            orderRepository.save(order);
        }

        // Publish async order confirmed event (sync sold_quantity to MySQL)
        FlashSaleMessage confirmedMessage = FlashSaleMessage.builder()
                .reservationId(reservationId)
                .flashSaleItemId(flashSaleItemId)
                .userId(userId)
                .orderId(orderId)
                .quantity(quantity)
                .type("ORDER_CONFIRMED")
                .build();
        messagePublisher.publishOrderConfirmed(confirmedMessage);

        // Broadcast SSE
        int newStock = redisService.getStock(flashSaleItemId);
        sseService.broadcastStockUpdate(flashSaleItemId, newStock, newStock <= 0);

        log.info("[FlashSale] Committed reservation={}, order={}", reservationId, orderId);
    }

    // ======= CANCEL RESERVATION (timeout or payment failure) =======

    @Override
    @Transactional
    public void cancelReservation(String reservationId) {
        Map<String, String> data = redisService.getReservation(reservationId);
        if (data.isEmpty()) {
            log.info("[FlashSale] Cancel called but reservation={} already gone (idempotent)", reservationId);
            return; // Idempotent
        }

        Long flashSaleItemId = Long.parseLong(data.get("flashSaleItemId"));
        Long userId = Long.parseLong(data.get("userId"));
        Long orderId = Long.parseLong(data.get("orderId"));
        int quantity = Integer.parseInt(data.get("quantity"));

        // Release stock atomically
        long releaseResult = redisService.releaseStock(flashSaleItemId, userId, reservationId, quantity);

        if (releaseResult == 1) {
            // Update order to CANCELLED
            Order order = orderRepository.findById(orderId).orElse(null);
            if (order != null && !ORDER_STATUS_CANCELLED.equals(order.getOrderStatus())) {
                order.setOrderStatus(ORDER_STATUS_CANCELLED);
                order.setPaymentStatus(PAYMENT_STATUS_FAILED);
                orderRepository.save(order);

                // Update payment transaction
                PaymentTransaction txn = paymentTransactionRepository.findByOrderOrderId(orderId);
                if (txn != null && TXN_STATUS_PENDING.equals(txn.getStatus())) {
                    txn.setStatus("CANCELLED");
                    paymentTransactionRepository.save(txn);
                }
            }

            // Broadcast SSE — stock restored
            int newStock = redisService.getStock(flashSaleItemId);
            sseService.broadcastStockUpdate(flashSaleItemId, newStock, newStock <= 0);

            log.info("[FlashSale] Cancelled reservation={}, stock restored", reservationId);
        }
    }
}
