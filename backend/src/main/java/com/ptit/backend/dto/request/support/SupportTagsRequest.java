package com.ptit.backend.dto.request.support;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.ArrayList;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SupportTagsRequest {

    @JsonProperty("tags")
    @Builder.Default
    private List<String> tags = new ArrayList<>();
}
