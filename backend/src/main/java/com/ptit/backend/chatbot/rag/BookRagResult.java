package com.ptit.backend.chatbot.rag;

import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * DTO nội bộ chứa kết quả RAG từ MySQL FULLTEXT.
 * Dùng để tạo context cho prompt của AI.
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
    private Integer publicationYear;

    private String authors;
    private String publisherName;
    private String categories;

    private BigDecimal flashSalePrice;
    private String campaignName;

    private String promotionCode;
    private BigDecimal discountPercent;
}
