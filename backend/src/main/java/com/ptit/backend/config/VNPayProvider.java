package com.ptit.backend.config;

import com.ptit.backend.entity.Order;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Component;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.*;

@Component
public class VNPayProvider {

    public String createPaymentUrl(Order order, HttpServletRequest request) {

        // 1. Dùng Secret Key và TmnCode của bạn
        String vnp_TmnCode = "9L6JR45G";
        String vnp_HashSecret = "TV0ZGOR9WZ8RVYGD3X7ZFRU0SICPBEWA";
        String vnp_PayUrl = "https://sandbox.vnpayment.vn/paymentv2/vpcpay.html";
        String vnp_ReturnUrl = "https://amends-omega-glitch.ngrok-free.dev/api/v1/payment/vnpay-callback";

        // 2. Tham số cơ bản
        long amount = order.getTotalAmount().longValue() * 100;

        Map<String, String> vnp_Params = new HashMap<>();
        vnp_Params.put("vnp_Version", "2.1.0");
        vnp_Params.put("vnp_Command", "pay");
        vnp_Params.put("vnp_TmnCode", vnp_TmnCode);
        vnp_Params.put("vnp_Amount", String.valueOf(amount));
        vnp_Params.put("vnp_CurrCode", "VND");

        vnp_Params.put("vnp_TxnRef", String.valueOf(order.getOrderId()));
        vnp_Params.put("vnp_OrderInfo", "Thanh toan don hang " + order.getOrderId());

        // Dùng mã danh mục chuẩn thay vì "other" như người bạn kia khuyên
        vnp_Params.put("vnp_OrderType", "250000");
        vnp_Params.put("vnp_Locale", "vn");
        vnp_Params.put("vnp_ReturnUrl", vnp_ReturnUrl);

        // Dùng IP thật của máy bạn (từ ipconfig)
        vnp_Params.put("vnp_IpAddr", "192.168.0.101");

        // 3. Thời gian
        Calendar cld = Calendar.getInstance(TimeZone.getTimeZone("Etc/GMT+7"));
        SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMddHHmmss");
        vnp_Params.put("vnp_CreateDate", formatter.format(cld.getTime()));

        cld.add(Calendar.MINUTE, 3);
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

                    // Build hash data (Không mã hóa tên biến, chỉ mã hóa giá trị, không dùng %20)
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
        String vnp_SecureHash = hmacSHA512(vnp_HashSecret, hashData.toString());
        queryUrl += "&vnp_SecureHash=" + vnp_SecureHash;

        return vnp_PayUrl + "?" + queryUrl;
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
        // 1. Lấy chữ ký do VNPay gửi về
        String vnp_SecureHash = params.get("vnp_SecureHash");

        // 2. Xóa các trường hash ra khỏi map trước khi băm lại
        params.remove("vnp_SecureHashType");
        params.remove("vnp_SecureHash");

        // 3. Sắp xếp các tham số
        List<String> fieldNames = new ArrayList<>(params.keySet());
        Collections.sort(fieldNames);

        StringBuilder hashData = new StringBuilder();

        try {
            Iterator<String> itr = fieldNames.iterator();
            while (itr.hasNext()) {
                String fieldName = itr.next();
                String fieldValue = params.get(fieldName);
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
        String vnp_HashSecret = "TV0ZGOR9WZ8RVYGD3X7ZFRU0SICPBEWA";
        String signValue = hmacSHA512(vnp_HashSecret, hashData.toString());

        // 5. So sánh 2 chữ ký
        return signValue.equals(vnp_SecureHash);
    }

}