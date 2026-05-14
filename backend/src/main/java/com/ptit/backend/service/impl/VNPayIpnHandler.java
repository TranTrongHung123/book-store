package com.ptit.backend.service.impl;

import com.ptit.backend.config.VNPayProvider;
import com.ptit.backend.dto.response.IpnResponse;
import com.ptit.backend.dto.response.VnpIpnResponseConst;
import com.ptit.backend.exception.BusinessException;
import com.ptit.backend.repository.OrderDetailRepository;
import com.ptit.backend.repository.OrderRepository;
import com.ptit.backend.service.FlashSaleCustomerService;
import com.ptit.backend.service.OrderService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.Set;

@Slf4j
@Component
@RequiredArgsConstructor
public class VNPayIpnHandler {

    private final VNPayProvider vnPayProvider;
    private final OrderService orderService;
    private final FlashSaleCustomerService flashSaleCustomerService;
    private final OrderDetailRepository orderDetailRepository;
    private final OrderRepository orderRepository;
    private final RedisTemplate<String, String> redisTemplate;

    private static final String PAYMENT_STATUS_FAILED = "Thanh toán thất bại";
    private static final String ORDER_STATUS_CANCELLED = "Đã hủy";

    public IpnResponse process(Map<String, String> params) {

        // 1. Kiểm tra chữ ký
        if (!vnPayProvider.verifyIpn(params)) {
            log.warn("[VNPay IPN] Sai chữ ký bảo mật!");
            return VnpIpnResponseConst.SIGNATURE_FAILED;
        }

        IpnResponse response;
        String txnRef = params.get("vnp_TxnRef");

        try {
            long orderId = Long.parseLong(txnRef);
            long vnpAmount = Long.parseLong(params.get("vnp_Amount")) / 100;
            String responseCode = params.get("vnp_ResponseCode");
            String transactionNo = params.get("vnp_TransactionNo");

            // 2. Kiểm tra có phải đơn flash sale không
            boolean isFlashSaleOrder = orderDetailRepository.existsFlashSaleItemByOrderId(orderId);

            if (isFlashSaleOrder) {
                // Xử lý đơn flash sale qua reservation
                handleFlashSalePayment(orderId, responseCode, transactionNo, vnpAmount);
            } else {
                // Xử lý đơn thường theo luồng cũ
                orderService.confirmOrderPayment(orderId, vnpAmount, responseCode, transactionNo);
            }

            response = VnpIpnResponseConst.SUCCESS;

        } catch (BusinessException e) {
            log.error("[VNPay IPN] Lỗi nghiệp vụ: {}", e.getErrorCode());
            response = switch (e.getErrorCode()) {
                case ORDER_NOT_FOUND -> VnpIpnResponseConst.ORDER_NOT_FOUND;
                case INVALID_AMOUNT -> VnpIpnResponseConst.INVALID_AMOUNT;
                case ORDER_ALREADY_CONFIRMED -> VnpIpnResponseConst.ORDER_ALREADY_CONFIRMED;
            };
        } catch (Exception e) {
            log.error("[VNPay IPN] Lỗi hệ thống", e);
            response = VnpIpnResponseConst.UNKNOWN_ERROR;
        }

        log.info("[VNPay IPN] txnRef: {}, response: {}", txnRef, response);
        return response;
    }

    /**
     * Tìm reservation theo orderId để xác nhận hoặc hủy.
     */
    private void handleFlashSalePayment(long orderId, String responseCode, String transactionNo, long vnpAmount) {
        // Tìm reservation theo orderId bằng Redis scan
        String reservationId = findReservationByOrderId(orderId);

        if (reservationId == null) {
            log.warn("[VNPay IPN] No active reservation found for flash sale orderId={}. Do not fallback to normal payment flow.", orderId);
            orderRepository.findById(orderId).ifPresent(order -> {
                if (!ORDER_STATUS_CANCELLED.equals(order.getOrderStatus())) {
                    order.setOrderStatus(ORDER_STATUS_CANCELLED);
                }
                if (!PAYMENT_STATUS_FAILED.equals(order.getPaymentStatus())) {
                    order.setPaymentStatus(PAYMENT_STATUS_FAILED);
                }
                orderRepository.save(order);
            });
            return;
        }

        if ("00".equals(responseCode)) {
            log.info("[VNPay IPN] Flash sale payment SUCCESS for orderId={}, reservation={}", orderId, reservationId);
            flashSaleCustomerService.commitReservation(reservationId);
        } else {
            log.warn("[VNPay IPN] Flash sale payment FAILED for orderId={}, responseCode={}", orderId, responseCode);
            flashSaleCustomerService.cancelReservation(reservationId);
        }
    }

    /**
     * Quét Redis để tìm reservation chứa orderId.
     */
    private String findReservationByOrderId(long orderId) {
        Set<String> keys = redisTemplate.keys("flash:reserve:*");
        if (keys == null || keys.isEmpty()) {
            return null;
        }
        for (String key : keys) {
            Map<Object, Object> data = redisTemplate.opsForHash().entries(key);
            String storedOrderId = data.get("orderId") != null ? data.get("orderId").toString() : null;
            if (String.valueOf(orderId).equals(storedOrderId)) {
                // Lấy reservationId từ key "flash:reserve:{id}"
                return key.replace("flash:reserve:", "");
            }
        }
        return null;
    }
}
