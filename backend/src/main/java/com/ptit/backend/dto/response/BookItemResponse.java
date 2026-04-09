package com.ptit.backend.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BookItemResponse {

    @JsonProperty("id")
    private Long id;

    @JsonProperty("book_id")
    private Long bookId;

    @JsonProperty("barcode")
    private String barcode;

    @JsonProperty("condition_type")
    private String conditionType;

    @JsonProperty("condition_note")
    private String conditionNote;

    @JsonProperty("is_for_rent")
    private Integer isForRent;

    @JsonProperty("deposit_amount")
    private BigDecimal depositAmount;

    @JsonProperty("current_rental_price")
    private BigDecimal currentRentalPrice;

    @JsonProperty("status")
    private String status;
}

