package com.ptit.backend.controller;

import com.ptit.backend.config.VNPayProperties;
import com.ptit.backend.config.VNPayProvider;
import com.ptit.backend.exception.BusinessException;
import com.ptit.backend.service.OrderService;
import com.ptit.backend.service.impl.VNPayIpnHandler;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.atomic.AtomicBoolean;

import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class PaymentControllerIpnIntegrationTest {

    private static final String HASH_SECRET = "integration-test-secret";

    private MockMvc mockMvc;
    private VNPayProvider vnPayProvider;
    private OrderService orderService;

    @BeforeEach
    void setUp() {
        VNPayProperties properties = new VNPayProperties();
        properties.setHashSecret(HASH_SECRET);
        properties.setFrontendReturnUrl("http://localhost:5173/payment/vnpay-callback");

        vnPayProvider = new VNPayProvider(properties);
        orderService = mock(OrderService.class);

        VNPayIpnHandler ipnHandler = new VNPayIpnHandler(vnPayProvider, orderService);
        PaymentController paymentController = new PaymentController(ipnHandler, vnPayProvider, properties);
        mockMvc = MockMvcBuilders.standaloneSetup(paymentController).build();
    }

    @Test
    void processIpn_returns97_whenSignatureIsInvalid() throws Exception {
        Map<String, String> params = signedIpnParams("1001", "2500000", "00", "TXN-1001");
        params.put("vnp_Amount", "2500100");

        mockMvc.perform(get("/api/v1/payment/vnpay-ipn").queryParam("vnp_TxnRef", params.get("vnp_TxnRef"))
                        .queryParam("vnp_Amount", params.get("vnp_Amount"))
                        .queryParam("vnp_ResponseCode", params.get("vnp_ResponseCode"))
                        .queryParam("vnp_TransactionNo", params.get("vnp_TransactionNo"))
                        .queryParam("vnp_TransactionStatus", params.get("vnp_TransactionStatus"))
                        .queryParam("vnp_SecureHash", params.get("vnp_SecureHash")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.RspCode").value("97"))
                .andExpect(jsonPath("$.Message").value("Invalid Checksum"));

        verify(orderService, never()).confirmOrderPayment(org.mockito.ArgumentMatchers.anyLong(),
                org.mockito.ArgumentMatchers.anyLong(),
                org.mockito.ArgumentMatchers.anyString(),
                org.mockito.ArgumentMatchers.anyString());
    }

    @Test
    void processIpn_returns02_forRepeatedTransaction() throws Exception {
        AtomicBoolean firstAttempt = new AtomicBoolean(true);
        doAnswer(invocation -> {
            if (firstAttempt.getAndSet(false)) {
                return null;
            }
            throw new BusinessException(BusinessException.ErrorCode.ORDER_ALREADY_CONFIRMED);
        }).when(orderService).confirmOrderPayment(org.mockito.ArgumentMatchers.anyLong(),
                org.mockito.ArgumentMatchers.anyLong(),
                org.mockito.ArgumentMatchers.anyString(),
                org.mockito.ArgumentMatchers.anyString());

        Map<String, String> params = signedIpnParams("1002", "3500000", "00", "TXN-1002");

        mockMvc.perform(get("/api/v1/payment/vnpay-ipn").queryParam("vnp_TxnRef", params.get("vnp_TxnRef"))
                        .queryParam("vnp_Amount", params.get("vnp_Amount"))
                        .queryParam("vnp_ResponseCode", params.get("vnp_ResponseCode"))
                        .queryParam("vnp_TransactionNo", params.get("vnp_TransactionNo"))
                        .queryParam("vnp_TransactionStatus", params.get("vnp_TransactionStatus"))
                        .queryParam("vnp_SecureHash", params.get("vnp_SecureHash")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.RspCode").value("00"))
                .andExpect(jsonPath("$.Message").value("Confirm Success"));

        mockMvc.perform(get("/api/v1/payment/vnpay-ipn").queryParam("vnp_TxnRef", params.get("vnp_TxnRef"))
                        .queryParam("vnp_Amount", params.get("vnp_Amount"))
                        .queryParam("vnp_ResponseCode", params.get("vnp_ResponseCode"))
                        .queryParam("vnp_TransactionNo", params.get("vnp_TransactionNo"))
                        .queryParam("vnp_TransactionStatus", params.get("vnp_TransactionStatus"))
                        .queryParam("vnp_SecureHash", params.get("vnp_SecureHash")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.RspCode").value("02"))
                .andExpect(jsonPath("$.Message").value("Order already confirmed"));
    }

    @Test
    void callback_redirectsToFrontendWithPaymentStatus() throws Exception {
        Map<String, String> params = signedIpnParams("1003", "4500000", "00", "TXN-1003");

        mockMvc.perform(get("/api/v1/payment/vnpay-callback")
                        .queryParam("vnp_TxnRef", params.get("vnp_TxnRef"))
                        .queryParam("vnp_Amount", params.get("vnp_Amount"))
                        .queryParam("vnp_ResponseCode", params.get("vnp_ResponseCode"))
                        .queryParam("vnp_TransactionNo", params.get("vnp_TransactionNo"))
                        .queryParam("vnp_TransactionStatus", params.get("vnp_TransactionStatus"))
                        .queryParam("vnp_SecureHash", params.get("vnp_SecureHash")))
                .andExpect(status().isFound())
                .andExpect(org.springframework.test.web.servlet.result.MockMvcResultMatchers.header()
                        .string("Location", org.hamcrest.Matchers.containsString("status=success")));
    }

    private Map<String, String> signedIpnParams(String txnRef, String amount, String responseCode, String transactionNo) {
        Map<String, String> params = new LinkedHashMap<>();
        params.put("vnp_TxnRef", txnRef);
        params.put("vnp_Amount", amount);
        params.put("vnp_ResponseCode", responseCode);
        params.put("vnp_TransactionNo", transactionNo);
        params.put("vnp_TransactionStatus", responseCode);

        String hashData = new TreeMap<>(params).entrySet().stream()
                .map(entry -> entry.getKey() + "=" + URLEncoder.encode(entry.getValue(), StandardCharsets.US_ASCII))
                .reduce((left, right) -> left + "&" + right)
                .orElse("");

        params.put("vnp_SecureHash", vnPayProvider.hmacSHA512(HASH_SECRET, hashData));
        return params;
    }
}
