package com.ptit.backend.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FlashSaleReserveResponse {

    @JsonProperty("reservation_id")
    private String reservationId;

    @JsonProperty("order_id")
    private Long orderId;

    @JsonProperty("payment_url")
    private String paymentUrl;

    @JsonProperty("expires_at")
    private long expiresAt;

    @JsonProperty("countdown_seconds")
    private int countdownSeconds;
}
