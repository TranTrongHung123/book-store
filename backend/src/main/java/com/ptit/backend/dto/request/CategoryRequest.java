package com.ptit.backend.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CategoryRequest {

    @JsonProperty("name")
    @NotBlank(message = "Ten the loai khong duoc de trong")
    @Size(max = 100, message = "Ten the loai toi da 100 ky tu")
    private String name;

    @JsonProperty("parent_id")
    private Long parentId;
}

