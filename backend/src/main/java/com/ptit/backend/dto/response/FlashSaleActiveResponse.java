package com.ptit.backend.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.math.BigDecimal;
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
public class FlashSaleActiveResponse {

    @JsonProperty("campaign_id")
    private Long campaignId;

    @JsonProperty("name")
    private String name;

    @JsonProperty("starts_at")
    private LocalDateTime startsAt;

    @JsonProperty("ends_at")
    private LocalDateTime endsAt;

    @JsonProperty("items")
    private List<FlashSaleActiveItemResponse> items;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class FlashSaleActiveItemResponse {

        @JsonProperty("flash_sale_item_id")
        private Long flashSaleItemId;

        @JsonProperty("book_id")
        private Long bookId;

        @JsonProperty("book_title")
        private String bookTitle;

        @JsonProperty("cover_image")
        private String coverImage;

        @JsonProperty("original_price")
        private BigDecimal originalPrice;

        @JsonProperty("flash_sale_price")
        private BigDecimal flashSalePrice;

        @JsonProperty("total_quantity")
        private Integer totalQuantity;

        @JsonProperty("remaining_stock")
        private Integer remainingStock;

        @JsonProperty("sold_quantity")
        private Integer soldQuantity;

        @JsonProperty("max_per_user")
        private Integer maxPerUser;

        @JsonProperty("sold_out")
        private boolean soldOut;

        @JsonProperty("is_purchased")
        private boolean isPurchased;
    }
}
