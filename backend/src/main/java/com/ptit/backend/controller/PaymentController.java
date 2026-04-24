package com.ptit.backend.controller;

import com.ptit.backend.config.VNPayProvider;
import com.ptit.backend.config.VNPayProperties;
import com.ptit.backend.dto.response.IpnResponse;
import com.ptit.backend.service.impl.VNPayIpnHandler;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.*;

@Slf4j
@RestController
@RequestMapping("/api/v1/payment")
@RequiredArgsConstructor
public class PaymentController {
    private final VNPayIpnHandler ipnHandler;
    private final VNPayProvider vnpProvider;
    private final VNPayProperties vnPayProperties;

    @GetMapping("/vnpay-callback")
    public ResponseEntity<Void> paymentCallback(@RequestParam Map<String, String> params) {
        log.info("[VNPay CALLBACK] Params received: {}", params);

        boolean validSignature = vnpProvider.verifyIpn(params);
        String responseCode = params.getOrDefault("vnp_ResponseCode", "");
        String txnRef = params.getOrDefault("vnp_TxnRef", "");

        String status;
        String message;
        if (!validSignature) {
            status = "invalid_signature";
            message = "Chu ky callback khong hop le";
        } else if ("00".equals(responseCode)) {
            status = "success";
            message = "Thanh toan thanh cong";
        } else {
            status = "failed";
            message = "Thanh toan that bai hoac bi huy";
        }

        // Fallback: process payment on return callback as well.
        // This keeps order status in sync even when VNPay IPN is delayed/unreachable.
        if (validSignature) {
            try {
                IpnResponse callbackProcessResult = ipnHandler.process(params);
                log.info("[VNPay CALLBACK] Fallback process result for txnRef {}: {}",
                        txnRef, callbackProcessResult);
            } catch (Exception exception) {
                log.error("[VNPay CALLBACK] Fallback process failed for txnRef {}", txnRef, exception);
            }
        }

        URI redirectUri = UriComponentsBuilder
                .fromUriString(vnPayProperties.getFrontendReturnUrl())
                .queryParam("order_id", txnRef)
                .queryParam("response_code", responseCode)
                .queryParam("valid_signature", validSignature)
                .queryParam("status", status)
                .queryParam("message", message)
                .build()
                .encode()
                .toUri();

        return ResponseEntity.status(HttpStatus.FOUND)
                .location(redirectUri)
                .build();
    }

    @GetMapping("/vnpay-ipn")
    public IpnResponse processIpn(@RequestParam Map<String, String> params) {
        log.info("[VNPay IPN] Params received: {}", params);
        return ipnHandler.process(params);
    }
}
