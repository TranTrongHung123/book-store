package com.ptit.backend.config;

import com.ptit.backend.entity.Order;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.*;

@Component
@RequiredArgsConstructor
public class VNPayProvider {
    private static final String VERSION = "2.1.0";
    private static final String COMMAND = "pay";
    private static final String CURRENCY = "VND";
    private static final String ORDER_TYPE = "250000";
    private static final String LOCALE = "vn";
    private static final String DEFAULT_IP = "127.0.0.1";
    private final VNPayProperties vnPayProperties;

    public String createPaymentUrl(Order order, HttpServletRequest request) {
        String tmnCode = vnPayProperties.getTmnCode();
        String hashSecret = vnPayProperties.getHashSecret();
        String payUrl = vnPayProperties.getPayUrl();
        String returnUrl = resolveReturnUrl(request);

        // 2. Tham số cơ bản
        long amount = order.getTotalAmount().longValue() * 100;

        Map<String, String> vnp_Params = new HashMap<>();
        vnp_Params.put("vnp_Version", VERSION);
        vnp_Params.put("vnp_Command", COMMAND);
        vnp_Params.put("vnp_TmnCode", tmnCode);
        vnp_Params.put("vnp_Amount", String.valueOf(amount));
        vnp_Params.put("vnp_CurrCode", CURRENCY);

        vnp_Params.put("vnp_TxnRef", String.valueOf(order.getOrderId()));
        vnp_Params.put("vnp_OrderInfo", "Thanh toan don hang " + order.getOrderId());

        vnp_Params.put("vnp_OrderType", ORDER_TYPE);
        vnp_Params.put("vnp_Locale", LOCALE);
        vnp_Params.put("vnp_ReturnUrl", returnUrl);

        vnp_Params.put("vnp_IpAddr", resolveClientIp(request));

        // 3. Thời gian (sử dụng Asia/Ho_Chi_Minh thay vì Etc/GMT+7 bị sai lệch giờ)
        Calendar cld = Calendar.getInstance(TimeZone.getTimeZone("Asia/Ho_Chi_Minh"));
        SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMddHHmmss");
        formatter.setTimeZone(TimeZone.getTimeZone("Asia/Ho_Chi_Minh"));
        vnp_Params.put("vnp_CreateDate", formatter.format(cld.getTime()));

        cld.add(Calendar.MINUTE, 15); // Đổi thành 15 phút theo chuẩn VNPay
        vnp_Params.put("vnp_ExpireDate", formatter.format(cld.getTime()));

        // 4. Sắp xếp Alphabet
        List<String> fieldNames = new ArrayList<>(vnp_Params.keySet());
        Collections.sort(fieldNames);

        StringBuilder hashData = new StringBuilder();
        StringBuilder query = new StringBuilder();

        // =================================================================
        // ÁP DỤNG CHÍNH XÁC LOGIC TỪ CODE VNPAYSERVICE CỦA BẠN VÀO ĐÂY
        // =================================================================
        try {
            Iterator<String> itr = fieldNames.iterator();
            while (itr.hasNext()) {
                String fieldName = itr.next();
                String fieldValue = vnp_Params.get(fieldName);
                if ((fieldValue != null) && (fieldValue.length() > 0)) {

                    // Build hash data (Không mã hóa tên biến, chỉ mã hóa giá trị)
                    hashData.append(fieldName);
                    hashData.append('=');
                    hashData.append(URLEncoder.encode(fieldValue, StandardCharsets.US_ASCII.toString()));

                    // Build query (Mã hóa cả hai)
                    query.append(URLEncoder.encode(fieldName, StandardCharsets.US_ASCII.toString()));
                    query.append('=');
                    query.append(URLEncoder.encode(fieldValue, StandardCharsets.US_ASCII.toString()));

                    if (itr.hasNext()) {
                        query.append('&');
                        hashData.append('&');
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
        // =================================================================

        // 5. Băm bằng HMAC-SHA512 (Chuẩn theo code VNPayConfig của bạn)
        String queryUrl = query.toString();
        String vnp_SecureHash = hmacSHA512(hashSecret, hashData.toString());
        queryUrl += "&vnp_SecureHash=" + vnp_SecureHash;

        return payUrl + "?" + queryUrl;
    }

    // Hàm băm nguyên bản
    public String hmacSHA512(final String key, final String data) {
        try {
            if (key == null || data == null) throw new NullPointerException();
            final Mac hmac512 = Mac.getInstance("HmacSHA512");
            byte[] hmacKeyBytes = key.getBytes(StandardCharsets.UTF_8);
            final SecretKeySpec secretKey = new SecretKeySpec(hmacKeyBytes, "HmacSHA512");
            hmac512.init(secretKey);
            byte[] dataBytes = data.getBytes(StandardCharsets.UTF_8);
            byte[] result = hmac512.doFinal(dataBytes);
            StringBuilder sb = new StringBuilder(2 * result.length);
            for (byte b : result) {
                sb.append(String.format("%02x", b & 0xff));
            }
            return sb.toString();
        } catch (Exception ex) {
            return "";
        }
    }

    public boolean verifyIpn(Map<String, String> params) {
        Map<String, String> filteredParams = new HashMap<>(params);
        // 1. Lấy chữ ký do VNPay gửi về
        String vnp_SecureHash = filteredParams.get("vnp_SecureHash");

        // 2. Xóa các trường hash ra khỏi map trước khi băm lại
        filteredParams.remove("vnp_SecureHashType");
        filteredParams.remove("vnp_SecureHash");

        // 3. Sắp xếp các tham số
        List<String> fieldNames = new ArrayList<>(filteredParams.keySet());
        Collections.sort(fieldNames);

        StringBuilder hashData = new StringBuilder();

        try {
            Iterator<String> itr = fieldNames.iterator();
            while (itr.hasNext()) {
                String fieldName = itr.next();
                String fieldValue = filteredParams.get(fieldName);
                if ((fieldValue != null) && (fieldValue.length() > 0)) {
                    // Nối chuỗi giống hệt lúc tạo link (chỉ encode value)
                    hashData.append(fieldName);
                    hashData.append('=');
                    hashData.append(URLEncoder.encode(fieldValue, StandardCharsets.US_ASCII.toString()));
                    if (itr.hasNext()) {
                        hashData.append('&');
                    }
                }
            }
        } catch (Exception e) {
            return false;
        }

        // 4. Băm lại chuỗi bằng HashSecret của bạn
        String vnp_HashSecret = vnPayProperties.getHashSecret();
        String signValue = hmacSHA512(vnp_HashSecret, hashData.toString());

        // 5. So sánh 2 chữ ký
        return signValue.equals(vnp_SecureHash);
    }

    private String resolveClientIp(HttpServletRequest request) {
        if (request == null) {
            return DEFAULT_IP;
        }
        String ip = DEFAULT_IP;
        String forwardedFor = request.getHeader("X-Forwarded-For");
        if (forwardedFor != null && !forwardedFor.isBlank()) {
            ip = forwardedFor.split(",")[0].trim();
        } else {
            String realIp = request.getHeader("X-Real-IP");
            if (realIp != null && !realIp.isBlank()) {
                ip = realIp.trim();
            } else {
                String remoteAddr = request.getRemoteAddr();
                if (remoteAddr != null && !remoteAddr.isBlank()) {
                    ip = remoteAddr;
                }
            }
        }
        // VNPay có thể lỗi chữ ký nếu IP là IPv6 (có chứa dấu : )
        if (ip.contains(":")) {
            return "127.0.0.1";
        }
        return ip;
    }

    private String resolveReturnUrl(HttpServletRequest request) {
        String configuredUrl = vnPayProperties.getReturnUrl();
        if (configuredUrl != null && !configuredUrl.isBlank()) {
            return configuredUrl.trim();
        }
        if (request == null) {
            return "http://localhost:8081/api/v1/payment/vnpay-callback";
        }

        String scheme = request.getScheme();
        String host = request.getHeader("X-Forwarded-Host");
        if (host == null || host.isBlank()) {
            host = request.getHeader("Host");
        }
        if (host == null || host.isBlank()) {
            host = request.getServerName() + ":" + request.getServerPort();
        }

        String forwardedProto = request.getHeader("X-Forwarded-Proto");
        if (forwardedProto != null && !forwardedProto.isBlank()) {
            scheme = forwardedProto.trim();
        }

        return scheme + "://" + host + "/api/v1/payment/vnpay-callback";
    }

}