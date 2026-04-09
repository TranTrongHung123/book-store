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
public class PromotionResponse {

    @JsonProperty("id")
    private Long id;

    @JsonProperty("code")
    private String code;

    @JsonProperty("title")
    private String title;

    @JsonProperty("subtitle")
    private String subtitle;

    @JsonProperty("discount_percent")
    private BigDecimal discountPercent;

    @JsonProperty("voucher_type")
    private String voucherType;

    @JsonProperty("applies_to_categories")
    private List<String> appliesToCategories;

    @JsonProperty("condition_text")
    private String conditionText;

    @JsonProperty("valid_from")
    private LocalDateTime validFrom;

    @JsonProperty("valid_to")
    private LocalDateTime validTo;

    @JsonProperty("terms")
    private String terms;
}

