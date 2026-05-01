package com.ptit.backend.dto.request;

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
public class PromotionRequest {

    @JsonProperty("id")
    private Long promotionId;

    @JsonProperty("code")
    private String code;

    @JsonProperty("discount_percent")
    private BigDecimal discountPercent;

    @JsonProperty("max_discount_amount")
    private BigDecimal maxDiscountAmount;

    @JsonProperty("min_order_value")
    private BigDecimal minOrderValue;

    @JsonProperty("usage_limit")
    private Integer usageLimit;

    @JsonProperty("used_count")
    private Integer usedCount;

    @JsonProperty("start_date")
    private LocalDateTime startDate;

    @JsonProperty("end_date")
    private LocalDateTime endDate;

    @JsonProperty("status")
    private Integer status;

    @JsonProperty("title")
    private String title;

    @JsonProperty("description")
    private String description;

    // Backend sẽ biến chuỗi "1,3" thành mảng ["1", "3"] trước khi trả về
    @JsonProperty("applicable_categories")
    private List<String> applicableCategories;

    @JsonProperty("created_at")
    private LocalDateTime createdAt;

    @JsonProperty("updated_at")
    private LocalDateTime updatedAt;
}

