package com.ptit.backend.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Nested DTO trong ChatbotResponse — đại diện cho một cuốn sách được AI gợi ý.
 * BeanOutputConverter sẽ dùng class này để generate JSON schema cho Gemini.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BookSuggestion {

    @JsonProperty("book_id")
    private Long bookId;

    private String title;

    private String authors;

    @JsonProperty("selling_price")
    private BigDecimal sellingPrice;

    @JsonProperty("flash_sale_price")
    private BigDecimal flashSalePrice;

    @JsonProperty("flash_sale_name")
    private String flashSaleName;

    @JsonProperty("promotion_code")
    private String promotionCode;

    @JsonProperty("discount_percent")
    private BigDecimal discountPercent;

    @JsonProperty("total_stock")
    private Integer totalStock;
}
