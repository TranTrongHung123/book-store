package com.ptit.backend.service;

import com.ptit.backend.dto.request.OrderRequest;
import com.ptit.backend.dto.request.OrderStatusUpdateRequest;
import com.ptit.backend.dto.response.OrderResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface OrderService {

    Page<OrderResponse> getOrders(Long userId, Pageable pageable);

    OrderResponse getOrderById(Long id);

    OrderResponse createOrder(OrderRequest request);

    OrderResponse updateOrderStatus(Long id, OrderStatusUpdateRequest request);

    void deleteOrder(Long id);
}

