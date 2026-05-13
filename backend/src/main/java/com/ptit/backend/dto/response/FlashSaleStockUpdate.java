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
public class FlashSaleStockUpdate {

    @JsonProperty("flash_sale_item_id")
    private Long flashSaleItemId;

    @JsonProperty("remaining_stock")
    private int remainingStock;

    @JsonProperty("sold_out")
    private boolean soldOut;
}
