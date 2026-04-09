package com.ptit.backend.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
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
public class OrderRequest {

    @JsonProperty("user_id")
    @NotNull(message = "Truong user_id la bat buoc")
    private Long userId;

    @JsonProperty("promotion_id")
    private Long promotionId;

    @JsonProperty("order_date")
    private LocalDateTime orderDate;

    @JsonProperty("shipping_address")
    private String shippingAddress;

    @JsonProperty("payment_method")
    private String paymentMethod;

    @JsonProperty("payment_status")
    private String paymentStatus;

    @JsonProperty("order_status")
    private String orderStatus;

    @JsonProperty("items")
    @NotEmpty(message = "Don hang phai co it nhat mot san pham")
    @Valid
    private List<OrderItemRequest> items;
}

