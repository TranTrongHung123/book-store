package com.ptit.backend.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChatbotResponse {

    private String answer;

    private String intent;

    private List<BookSuggestion> books;

    @JsonProperty("promotion_info")
    private String promotionInfo;

    @JsonProperty("flash_sale_info")
    private String flashSaleInfo;

    @JsonProperty("has_results")
    private boolean hasResults;
}
