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
public class FlashSaleItemResponse {

    @JsonProperty("id")
    private Long id;

    @JsonProperty("campaign_id")
    private Long campaignId;

    @JsonProperty("book_id")
    private Long bookId;

    @JsonProperty("flash_price")
    private BigDecimal flashPrice;

    @JsonProperty("flash_stock")
    private Integer flashStock;

    @JsonProperty("purchase_limit")
    private Integer purchaseLimit;
}
