package com.ptit.backend.service.impl;

import com.ptit.backend.config.VNPayProvider;
import com.ptit.backend.dto.response.IpnResponse;
import com.ptit.backend.dto.response.VnpIpnResponseConst;
import com.ptit.backend.exception.BusinessException;
import com.ptit.backend.service.OrderService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class VNPayIpnHandler {

    // TIÊM VNPayProvider CỦA BẠN VÀO ĐÂY
    private final VNPayProvider vnPayProvider;
    private final OrderService orderService;

    public IpnResponse process(Map<String, String> params) {

        // 1. Dùng VNPayProvider để kiểm tra chữ ký
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

            // 2. Gọi Service xử lý nghiệp vụ đơn hàng
            orderService.confirmOrderPayment(orderId, vnpAmount, responseCode, transactionNo);

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
}