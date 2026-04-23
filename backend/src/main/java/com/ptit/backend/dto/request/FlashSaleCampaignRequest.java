package com.ptit.backend.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FlashSaleCampaignRequest {

    @JsonProperty("name")
    @NotBlank(message = "Ten campaign khong duoc de trong")
    private String name;

    @JsonProperty("starts_at")
    @NotNull(message = "Thoi gian bat dau la bat buoc")
    private LocalDateTime startsAt;

    @JsonProperty("ends_at")
    @NotNull(message = "Thoi gian ket thuc la bat buoc")
    private LocalDateTime endsAt;

    @JsonProperty("status")
    private String status;
}
