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
public class ChangePasswordRequest {

    @JsonProperty("current_password")
    @NotBlank(message = "Mat khau hien tai khong duoc de trong")
    private String currentPassword;

    @JsonProperty("new_password")
    @NotBlank(message = "Mat khau moi khong duoc de trong")
    @Size(min = 6, max = 255, message = "Mat khau moi phai tu 6 den 255 ky tu")
    private String newPassword;
}
