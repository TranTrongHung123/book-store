package com.ptit.backend.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderItemRequest {

    @JsonProperty("book_item_id")
    private Long bookItemId;

    @JsonProperty("book_id")
    private Long bookId;

    @JsonProperty("title")
    private String title;

    @JsonProperty("quantity")
    @Min(value = 1, message = "So luong mua phai lon hon 0")
    private Integer quantity;

    @JsonProperty("unit_price")
    @DecimalMin(value = "0.0", inclusive = true, message = "Don gia phai lon hon hoac bang 0")
    private BigDecimal unitPrice;
}

