package com.ptit.backend.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RentalStatusUpdateRequest {

    @JsonProperty("status")
    private String status;

    @JsonProperty("payment_status")
    private String paymentStatus;

    @JsonProperty("return_date")
    private LocalDateTime returnDate;

    @JsonProperty("penalty_fee")
    private BigDecimal penaltyFee;
}

