package com.ptit.backend.controller;

import com.ptit.backend.dto.request.FlashSaleReserveRequest;
import com.ptit.backend.dto.response.ApiResponse;
import com.ptit.backend.dto.response.FlashSaleActiveResponse;
import com.ptit.backend.dto.response.FlashSaleReservationStatusResponse;
import com.ptit.backend.dto.response.FlashSaleReserveResponse;
import com.ptit.backend.entity.User;
import com.ptit.backend.exception.AppException;
import com.ptit.backend.exception.ErrorCode;
import com.ptit.backend.repository.UserRepository;
import com.ptit.backend.service.FlashSaleCustomerService;
import com.ptit.backend.service.FlashSaleSseService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@RestController
@RequestMapping("/api/v1/flash-sale")
@RequiredArgsConstructor
public class FlashSaleCustomerController {

    private static final int SUCCESS_CODE = 1000;
    private static final String SUCCESS_MSG = "Thanh cong";

    private final FlashSaleCustomerService flashSaleCustomerService;
    private final FlashSaleSseService sseService;
    private final UserRepository userRepository;

    /**
     * GET /api/v1/flash-sale/active
     * Public — returns active campaigns with realtime stock from Redis.
     */
    @GetMapping("/active")
    public ResponseEntity<ApiResponse<List<FlashSaleActiveResponse>>> getActiveCampaigns() {
        Long userId = null;
        try {
            userId = getCurrentUserId();
        } catch (Exception e) {
            // Public access or unauthenticated
        }
        List<FlashSaleActiveResponse> result = flashSaleCustomerService.getActiveCampaigns(userId);
        return ResponseEntity.ok(success(result));
    }

    /**
     * POST /api/v1/flash-sale/reserve
     * Authenticated — reserve stock → create order → return VNPay URL.
     */
    @PostMapping("/reserve")
    public ResponseEntity<ApiResponse<FlashSaleReserveResponse>> reserveStock(
            @Valid @RequestBody FlashSaleReserveRequest request,
            HttpServletRequest httpRequest) {
        Long userId = getCurrentUserId();
        FlashSaleReserveResponse result = flashSaleCustomerService.reserveStock(userId, request, httpRequest);
        return ResponseEntity.ok(success(result));
    }

    /**
     * GET /api/v1/flash-sale/reserve/{reservationId}/status
     * Authenticated — check reservation countdown and status.
     */
    @GetMapping("/reserve/{reservationId}/status")
    public ResponseEntity<ApiResponse<FlashSaleReservationStatusResponse>> getReservationStatus(
            @PathVariable String reservationId) {
        Long userId = getCurrentUserId();
        FlashSaleReservationStatusResponse result = flashSaleCustomerService.getReservationStatus(reservationId, userId);
        return ResponseEntity.ok(success(result));
    }

    /**
     * GET /api/v1/flash-sale/sse/stock
     * Public SSE endpoint — broadcasts realtime stock updates.
     */
    @GetMapping(value = "/sse/stock", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter stockSse() {
        return sseService.createEmitter();
    }

    private <T> ApiResponse<T> success(T result) {
        return ApiResponse.<T>builder()
                .code(SUCCESS_CODE)
                .message(SUCCESS_MSG)
                .result(result)
                .build();
    }

    private Long getCurrentUserId() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || auth.getPrincipal() == null) {
            throw new AppException(ErrorCode.UNAUTHENTICATED);
        }
        Object principal = auth.getPrincipal();
        if (principal instanceof UserDetails userDetails) {
            String username = userDetails.getUsername();
            User user = userRepository.findByUsername(username)
                    .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));
            return user.getUserId();
        }
        throw new AppException(ErrorCode.UNAUTHENTICATED);
    }
}
