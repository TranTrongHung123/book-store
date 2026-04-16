package com.ptit.backend.dto.request.support;

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
public class SupportOpenRequest {

    @JsonProperty("message")
    @NotBlank(message = "Noi dung ho tro khong duoc de trong")
    @Size(max = 2000, message = "Noi dung ho tro toi da 2000 ky tu")
    private String message;
}
