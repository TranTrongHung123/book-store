package com.ptit.backend.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderStatusUpdateRequest {

    @JsonProperty("order_status")
    @Size(max = 50, message = "Trang thai don hang toi da 50 ky tu")
    private String orderStatus;

    @JsonProperty("payment_status")
    @Size(max = 50, message = "Trang thai thanh toan toi da 50 ky tu")
    private String paymentStatus;
}

