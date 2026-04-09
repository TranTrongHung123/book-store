package com.ptit.backend.dto.response;

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
public class RentalResponse {

    @JsonProperty("id")
    private Long id;

    @JsonProperty("user_id")
    private Long userId;

    @JsonProperty("book_item_id")
    private Long bookItemId;

    @JsonProperty("book_title")
    private String bookTitle;

    @JsonProperty("rent_date")
    private LocalDateTime rentDate;

    @JsonProperty("due_date")
    private LocalDateTime dueDate;

    @JsonProperty("return_date")
    private LocalDateTime returnDate;

    @JsonProperty("actual_deposit")
    private BigDecimal actualDeposit;

    @JsonProperty("rental_fee")
    private BigDecimal rentalFee;

    @JsonProperty("penalty_fee")
    private BigDecimal penaltyFee;

    @JsonProperty("payment_status")
    private String paymentStatus;

    @JsonProperty("status")
    private String status;
}

