package com.ptit.backend.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;
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
public class BookRequest {

    @JsonProperty("title")
    @Size(min = 1, max = 255, message = "Ten sach khong duoc de trong")
    private String title;

    @JsonProperty("author_name")
    private String authorName;

    @JsonProperty("category_name")
    private String categoryName;

    @JsonProperty("publisher_id")
    private Long publisherId;

    @JsonProperty("publication_year")
    private Integer publicationYear;

    @JsonProperty("language")
    private String language;

    @JsonProperty("original_price")
    @DecimalMin(value = "0.0", inclusive = true, message = "Gia goc phai lon hon hoac bang 0")
    private BigDecimal originalPrice;

    @JsonProperty("selling_price")
    @DecimalMin(value = "0.0", inclusive = true, message = "Gia ban phai lon hon hoac bang 0")
    private BigDecimal sellingPrice;

    @JsonProperty("cover_image")
    private String coverImage;

    @JsonProperty("total_stock")
    @Min(value = 0, message = "So luong ton kho phai lon hon hoac bang 0")
    private Integer totalStock;

    @JsonProperty("description")
    private String description;

    @JsonProperty("long_description")
    private String longDescription;

    @JsonProperty("gallery_images")
    private List<String> galleryImages;

    @JsonProperty("status")
    private Integer status;
}

