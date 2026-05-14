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
public class FlashSaleReservationStatusResponse {

    @JsonProperty("reservation_id")
    private String reservationId;

    @JsonProperty("order_id")
    private Long orderId;

    /** Trạng thái: PENDING, SUCCESS, CANCELLED, EXPIRED */
    @JsonProperty("status")
    private String status;

    @JsonProperty("remaining_seconds")
    private long remainingSeconds;

    @JsonProperty("payment_url")
    private String paymentUrl;
}
