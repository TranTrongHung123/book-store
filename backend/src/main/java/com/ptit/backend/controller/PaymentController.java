package com.ptit.backend.controller;

import com.ptit.backend.config.VNPayProvider;
import com.ptit.backend.dto.response.IpnResponse;
import com.ptit.backend.service.impl.VNPayIpnHandler;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.*;

@Slf4j
@RestController
@RequestMapping("/api/v1/payment")
@RequiredArgsConstructor
public class PaymentController {
    private final VNPayIpnHandler ipnHandler;
    private final VNPayProvider vnpProvider;
    private String vnp_HashSecret = "TV0ZGOR9WZ8RVYGD3X7ZFRU0SICPBEWA";

//    @GetMapping("/vnpay-callback")
//    public Object paymentCallback(HttpServletRequest request) {
//
//        log.info("========== [VNPay RETURN CALLBACK] ==========");
//
//        // 1. In nhanh các tham số quan trọng nhất để liếc mắt là thấy
//        String txnRef = request.getParameter("vnp_TxnRef");
//        String responseCode = request.getParameter("vnp_ResponseCode");
//        String secureHash = request.getParameter("vnp_SecureHash");
//
//        log.info("Mã đơn hàng (vnp_TxnRef): {}", txnRef);
//        log.info("Mã phản hồi (vnp_ResponseCode): {}", responseCode);
//        log.info("Chữ ký (vnp_SecureHash): {}", secureHash);
//
//        // 2. In toàn bộ danh sách tham số (giống hệt cách bạn làm với IPN)
//        log.info("Toàn bộ dữ liệu VNPay trả về:");
//        request.getParameterMap().forEach((key, value) -> {
//            log.info("{} = {}", key, value[0]);
//        });
//
//        log.info("=============================================");
//
//        Map<String, String> fields = new HashMap<>();
//        for (Enumeration<String> params = request.getParameterNames(); params.hasMoreElements(); ) {
//            String fieldName = params.nextElement();
//            String fieldValue = request.getParameter(fieldName);
//            if ((fieldValue != null) && (fieldValue.length() > 0)) {
//                fields.put(fieldName, fieldValue);
//            }
//        }
//
//        String vnp_SecureHash = request.getParameter("vnp_SecureHash");
//
//        // Loại bỏ 2 tham số này trước khi tạo chuỗi hash
//        if (fields.containsKey("vnp_SecureHashType")) {
//            fields.remove("vnp_SecureHashType");
//        }
//        if (fields.containsKey("vnp_SecureHash")) {
//            fields.remove("vnp_SecureHash");
//        }
//
//        // Bước cực kỳ quan trọng: Sort theo alphabet
//        List<String> fieldNames = new ArrayList<>(fields.keySet());
//        Collections.sort(fieldNames);
//
//        StringBuilder hashData = new StringBuilder();
//        Iterator<String> itr = fieldNames.iterator();
//        while (itr.hasNext()) {
//            String fieldName = (String) itr.next();
//            String fieldValue = (String) fields.get(fieldName);
//            if ((fieldValue != null) && (fieldValue.length() > 0)) {
//                // Nối chuỗi dữ liệu
//                hashData.append(fieldName);
//                hashData.append('=');
//                hashData.append(URLEncoder.encode(fieldValue, StandardCharsets.US_ASCII));
//                if (itr.hasNext()) {
//                    hashData.append('&');
//                }
//            }
//        }
//
//        // Gọi hàm băm dữ liệu với thuật toán SHA512
//        String signValue = vnpProvider.hmacSHA512(vnp_HashSecret, hashData.toString());
//
//        // Kiểm tra xem chữ ký do mình tự tính toán có khớp với chữ ký VNPay gửi về không
//        if (signValue.equals(vnp_SecureHash)) {
//            // Xác thực thành công -> Bắt đầu check vnp_ResponseCode
//            if ("00".equals(request.getParameter("vnp_ResponseCode"))) {
//                return "Thanh toán thành công. Đơn hàng: " + request.getParameter("vnp_TxnRef");
//                // TODO: Update database trạng thái đơn hàng thành "Đã thanh toán"
//            } else {
//                return "Thanh toán thất bại / Bị hủy";
//            }
//        } else {
//            // Xác thực thất bại
//            return "Chữ ký không hợp lệ (Invalid Signature)";
//        }
//    }

    @GetMapping("/vnpay-ipn")
    public IpnResponse processIpn(@RequestParam Map<String, String> params) {
        log.info("[VNPay IPN] Params received: {}", params);
        return ipnHandler.process(params);
    }
}
