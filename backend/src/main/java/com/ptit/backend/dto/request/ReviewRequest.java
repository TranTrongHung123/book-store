package com.ptit.backend.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReviewRequest {

    @JsonProperty("book_id")
    private Long bookId;

    @JsonProperty("user_id")
    private Long userId;

    @JsonProperty("rating")
    @NotNull(message = "So sao danh gia la bat buoc")
    @Min(value = 1, message = "So sao danh gia phai tu 1 den 5")
    @Max(value = 5, message = "So sao danh gia phai tu 1 den 5")
    private Integer rating;

    @JsonProperty("comment")
    @Size(max = 2000, message = "Noi dung binh luan khong duoc vuot qua 2000 ky tu")
    private String comment;

    @JsonProperty("is_approved")
    private Integer isApproved;
}

