package com.ptit.backend.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FlashSaleItemRequest {

    @JsonProperty("book_id")
    @NotNull(message = "book_id la bat buoc")
    private Long bookId;

    @JsonProperty("flash_price")
    @NotNull(message = "flash_price la bat buoc")
    @DecimalMin(value = "0.0", inclusive = false, message = "flash_price phai lon hon 0")
    private BigDecimal flashPrice;

    @JsonProperty("flash_stock")
    @NotNull(message = "flash_stock la bat buoc")
    @Min(value = 1, message = "flash_stock toi thieu la 1")
    private Integer flashStock;

    @JsonProperty("purchase_limit")
    @NotNull(message = "purchase_limit la bat buoc")
    @Min(value = 1, message = "purchase_limit toi thieu la 1")
    private Integer purchaseLimit;
}
