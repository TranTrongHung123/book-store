package com.ptit.backend.controller;

import com.ptit.backend.dto.request.OrderRequest;
import com.ptit.backend.dto.request.OrderStatusUpdateRequest;
import com.ptit.backend.dto.response.ApiResponse;
import com.ptit.backend.dto.response.OrderResponse;
import com.ptit.backend.dto.response.PagedResponse;
import com.ptit.backend.service.OrderService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/orders")
@RequiredArgsConstructor
public class OrderController {

    private static final int SUCCESS_CODE = 1000;
    private static final String SUCCESS_MESSAGE = "Thanh cong";

    private final OrderService orderService;

    @GetMapping
    public ResponseEntity<ApiResponse<PagedResponse<OrderResponse>>> getOrders(
            @RequestParam(name = "_page", defaultValue = "0") int page,
            @RequestParam(name = "_limit", defaultValue = "10") int limit,
            @RequestParam(name = "_sort", defaultValue = "orderId") String sort,
            @RequestParam(name = "_order", defaultValue = "desc") String order,
            @RequestParam(name = "user_id", required = false) Long userId,
            @RequestParam(name = "q", required = false) String q
    ) {
        Pageable pageable = buildPageable(page, limit, sort, order);
        Page<OrderResponse> result = orderService.getOrders(userId, pageable);

        ApiResponse<PagedResponse<OrderResponse>> response = ApiResponse.<PagedResponse<OrderResponse>>builder()
                .code(SUCCESS_CODE)
                .message(SUCCESS_MESSAGE)
                .result(toPagedResponse(result))
                .build();

        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<OrderResponse>> getOrderById(@PathVariable Long id) {
        OrderResponse result = orderService.getOrderById(id);
        return ResponseEntity.ok(success(result));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<OrderResponse>> createOrder(@Valid @RequestBody OrderRequest request, HttpServletRequest httpServletRequest) {
        OrderResponse result = orderService.createOrder(request,httpServletRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(success(result));
    }

    @PatchMapping("/{id}")
    public ResponseEntity<ApiResponse<OrderResponse>> updateOrderStatus(
            @PathVariable Long id,
            @Valid @RequestBody OrderStatusUpdateRequest request
    ) {
        OrderResponse result = orderService.updateOrderStatus(id, request);
        return ResponseEntity.ok(success(result));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Object>> deleteOrder(@PathVariable Long id) {
        orderService.deleteOrder(id);
        return ResponseEntity.ok(success(null));
    }

    private Pageable buildPageable(int page, int limit, String sort, String order) {
        Sort.Direction direction = "desc".equalsIgnoreCase(order) ? Sort.Direction.DESC : Sort.Direction.ASC;
        int safePage = Math.max(page, 0);
        int safeLimit = Math.max(limit, 1);
        return PageRequest.of(safePage, safeLimit, Sort.by(direction, sort));
    }

    private <T> ApiResponse<T> success(T result) {
        return ApiResponse.<T>builder()
                .code(SUCCESS_CODE)
                .message(SUCCESS_MESSAGE)
                .result(result)
                .build();
    }

    private <T> PagedResponse<T> toPagedResponse(Page<T> page) {
        return PagedResponse.<T>builder()
                .content(page.getContent())
                .page(page.getNumber())
                .limit(page.getSize())
                .totalElements(page.getTotalElements())
                .totalPages(page.getTotalPages())
                .build();
    }
}

