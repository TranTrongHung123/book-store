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
public class OrderItemResponse {

    @JsonProperty("book_item_id")
    private Long bookItemId;

    @JsonProperty("title")
    private String title;

    @JsonProperty("unit_price")
    private BigDecimal unitPrice;

    @JsonProperty("quantity")
    private Integer quantity;
}

