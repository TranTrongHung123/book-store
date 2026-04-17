package com.ptit.backend.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderResponse {

    @JsonProperty("id")
    private Long id;

    @JsonProperty("user_id")
    private Long userId;

    @JsonProperty("promotion_id")
    private Long promotionId;

    @JsonProperty("order_date")
    private LocalDateTime orderDate;

    @JsonProperty("total_amount")
    private BigDecimal totalAmount;

    @JsonProperty("shipping_address")
    private String shippingAddress;

    @JsonProperty("payment_method")
    private String paymentMethod;

    @JsonProperty("order_status")
    private String orderStatus;

    @JsonProperty("payment_url")
    private String paymentUrl; // Thêm trường này

    @JsonProperty("items")
    private List<OrderItemResponse> items;
}

