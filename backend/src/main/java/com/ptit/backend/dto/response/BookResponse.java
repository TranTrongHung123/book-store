package com.ptit.backend.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.math.BigDecimal;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BookResponse {

    @JsonProperty("id")
    private Long id;

    @JsonProperty("title")
    private String title;

    @JsonProperty("author_name")
    private String authorName;

    @JsonProperty("category_name")
    private String categoryName;

    @JsonProperty("original_price")
    private BigDecimal originalPrice;

    @JsonProperty("selling_price")
    private BigDecimal sellingPrice;

    @JsonProperty("cover_image")
    private String coverImage;

    @JsonProperty("total_stock")
    private Integer totalStock;

    @JsonProperty("sold_count")
    private Integer soldCount;

    @JsonProperty("description")
    private String description;

    @JsonProperty("gallery_images")
    private List<String> galleryImages;

    @JsonProperty("rental_count")
    private Integer rentalCount;

    @JsonProperty("long_description")
    private String longDescription;
}

