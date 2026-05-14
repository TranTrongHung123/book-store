package com.ptit.backend.controller;

import com.ptit.backend.config.VNPayProperties;
import com.ptit.backend.config.VNPayProvider;
import com.ptit.backend.exception.BusinessException;
import com.ptit.backend.repository.OrderDetailRepository;
import com.ptit.backend.repository.OrderRepository;
import com.ptit.backend.service.FlashSaleCustomerService;
import com.ptit.backend.service.OrderService;
import com.ptit.backend.service.impl.VNPayIpnHandler;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.atomic.AtomicBoolean;

import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SuppressWarnings("SpellCheckingInspection")
@ExtendWith(MockitoExtension.class)
class PaymentControllerIpnIntegrationTest {

    private static final String HASH_SECRET = "integration-test-secret";
    private static final String VNP_TXN_REF = "vnp_TxnRef";
    private static final String VNP_AMOUNT = "vnp_Amount";
    private static final String VNP_RESPONSE_CODE = "vnp_ResponseCode";
    private static final String VNP_TRANSACTION_NO = "vnp_TransactionNo";
    private static final String VNP_TRANSACTION_STATUS = "vnp_TransactionStatus";
    private static final String VNP_SECURE_HASH = "vnp_SecureHash";

    private MockMvc mockMvc;
    private VNPayProvider vnPayProvider;
    private OrderService orderService;
    @Mock
    private RedisTemplate<String, String> redisTemplate;

    @BeforeEach
    void setUp() {
        VNPayProperties properties = new VNPayProperties();
        properties.setHashSecret(HASH_SECRET);
        properties.setFrontendReturnUrl("http://localhost:5173/payment/vnpay-callback");

        vnPayProvider = new VNPayProvider(properties);
        orderService = mock(OrderService.class);
        FlashSaleCustomerService flashSaleCustomerService = mock(FlashSaleCustomerService.class);
        OrderDetailRepository orderDetailRepository = mock(OrderDetailRepository.class);
        OrderRepository orderRepository = mock(OrderRepository.class);

        VNPayIpnHandler ipnHandler = new VNPayIpnHandler(vnPayProvider, orderService,
                flashSaleCustomerService, orderDetailRepository, orderRepository, redisTemplate);
        PaymentController paymentController = new PaymentController(ipnHandler, vnPayProvider, properties);
        mockMvc = MockMvcBuilders.standaloneSetup(paymentController).build();
    }

    @Test
    void processIpn_returns97_whenSignatureIsInvalid() throws Exception {
        Map<String, String> params = signedIpnParams("1001", "2500000", "00", "TXN-1001");
        params.put(VNP_AMOUNT, "2500100");

        mockMvc.perform(get("/api/v1/payment/vnpay-ipn").queryParam(VNP_TXN_REF, params.get(VNP_TXN_REF))
                        .queryParam(VNP_AMOUNT, params.get(VNP_AMOUNT))
                        .queryParam(VNP_RESPONSE_CODE, params.get(VNP_RESPONSE_CODE))
                        .queryParam(VNP_TRANSACTION_NO, params.get(VNP_TRANSACTION_NO))
                        .queryParam(VNP_TRANSACTION_STATUS, params.get(VNP_TRANSACTION_STATUS))
                        .queryParam(VNP_SECURE_HASH, params.get(VNP_SECURE_HASH)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.RspCode").value("97"))
                .andExpect(jsonPath("$.Message").value("Invalid Checksum"));

        verify(orderService, never()).confirmOrderPayment(anyLong(), anyLong(), anyString(), anyString());
    }

    @Test
    void processIpn_returns02_forRepeatedTransaction() throws Exception {
        AtomicBoolean firstAttempt = new AtomicBoolean(true);
        doAnswer(invocation -> {
            if (firstAttempt.getAndSet(false)) {
                return null;
            }
            throw new BusinessException(BusinessException.ErrorCode.ORDER_ALREADY_CONFIRMED);
        }).when(orderService).confirmOrderPayment(anyLong(), anyLong(), anyString(), anyString());

        Map<String, String> params = signedIpnParams("1002", "3500000", "00", "TXN-1002");

        mockMvc.perform(get("/api/v1/payment/vnpay-ipn").queryParam(VNP_TXN_REF, params.get(VNP_TXN_REF))
                        .queryParam(VNP_AMOUNT, params.get(VNP_AMOUNT))
                        .queryParam(VNP_RESPONSE_CODE, params.get(VNP_RESPONSE_CODE))
                        .queryParam(VNP_TRANSACTION_NO, params.get(VNP_TRANSACTION_NO))
                        .queryParam(VNP_TRANSACTION_STATUS, params.get(VNP_TRANSACTION_STATUS))
                        .queryParam(VNP_SECURE_HASH, params.get(VNP_SECURE_HASH)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.RspCode").value("00"))
                .andExpect(jsonPath("$.Message").value("Confirm Success"));

        mockMvc.perform(get("/api/v1/payment/vnpay-ipn").queryParam(VNP_TXN_REF, params.get(VNP_TXN_REF))
                        .queryParam(VNP_AMOUNT, params.get(VNP_AMOUNT))
                        .queryParam(VNP_RESPONSE_CODE, params.get(VNP_RESPONSE_CODE))
                        .queryParam(VNP_TRANSACTION_NO, params.get(VNP_TRANSACTION_NO))
                        .queryParam(VNP_TRANSACTION_STATUS, params.get(VNP_TRANSACTION_STATUS))
                        .queryParam(VNP_SECURE_HASH, params.get(VNP_SECURE_HASH)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.RspCode").value("02"))
                .andExpect(jsonPath("$.Message").value("Order already confirmed"));
    }

    @Test
    void callback_redirectsToFrontendWithPaymentStatus() throws Exception {
        Map<String, String> params = signedIpnParams("1003", "4500000", "00", "TXN-1003");

        mockMvc.perform(get("/api/v1/payment/vnpay-callback")
                        .queryParam(VNP_TXN_REF, params.get(VNP_TXN_REF))
                        .queryParam(VNP_AMOUNT, params.get(VNP_AMOUNT))
                        .queryParam(VNP_RESPONSE_CODE, params.get(VNP_RESPONSE_CODE))
                        .queryParam(VNP_TRANSACTION_NO, params.get(VNP_TRANSACTION_NO))
                        .queryParam(VNP_TRANSACTION_STATUS, params.get(VNP_TRANSACTION_STATUS))
                        .queryParam(VNP_SECURE_HASH, params.get(VNP_SECURE_HASH)))
                .andExpect(status().isFound())
                .andExpect(org.springframework.test.web.servlet.result.MockMvcResultMatchers.header()
                        .string("Location", org.hamcrest.Matchers.containsString("status=success")));
    }

    @Test
    void callback_redirectsToFrontendWithFailedPaymentStatus() throws Exception {
        Map<String, String> params = signedIpnParams("1004", "4500000", "24", "TXN-1004");

        mockMvc.perform(get("/api/v1/payment/vnpay-callback")
                        .queryParam(VNP_TXN_REF, params.get(VNP_TXN_REF))
                        .queryParam(VNP_AMOUNT, params.get(VNP_AMOUNT))
                        .queryParam(VNP_RESPONSE_CODE, params.get(VNP_RESPONSE_CODE))
                        .queryParam(VNP_TRANSACTION_NO, params.get(VNP_TRANSACTION_NO))
                        .queryParam(VNP_TRANSACTION_STATUS, params.get(VNP_TRANSACTION_STATUS))
                        .queryParam(VNP_SECURE_HASH, params.get(VNP_SECURE_HASH)))
                .andExpect(status().isFound())
                .andExpect(org.springframework.test.web.servlet.result.MockMvcResultMatchers.header()
                        .string("Location", org.hamcrest.Matchers.containsString("status=failed")));
    }

    private Map<String, String> signedIpnParams(String txnRef, String amount, String responseCode, String transactionNo) {
        Map<String, String> params = new LinkedHashMap<>();
        params.put(VNP_TXN_REF, txnRef);
        params.put(VNP_AMOUNT, amount);
        params.put(VNP_RESPONSE_CODE, responseCode);
        params.put(VNP_TRANSACTION_NO, transactionNo);
        params.put(VNP_TRANSACTION_STATUS, responseCode);

        String hashData = new TreeMap<>(params).entrySet().stream()
                .map(entry -> entry.getKey() + "=" + URLEncoder.encode(entry.getValue(), StandardCharsets.US_ASCII))
                .reduce((left, right) -> left + "&" + right)
                .orElse("");

        params.put(VNP_SECURE_HASH, vnPayProvider.hmacSHA512(HASH_SECRET, hashData));
        return params;
    }
}
