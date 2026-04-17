package com.ptit.backend.chatbot.rag;

import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Internal DTO chứa kết quả truy vấn RAG từ MySQL FULLTEXT search.
 * Được dùng để build context payload cho System Prompt của AI.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BookRagResult {

    private Long bookId;
    private String title;
    private String description;
    private BigDecimal sellingPrice;
    private Integer totalStock;

    private String authors;
    private String publisherName;
    private String categories;

    private BigDecimal flashSalePrice;
    private String campaignName;

    private String promotionCode;
    private BigDecimal discountPercent;
}
