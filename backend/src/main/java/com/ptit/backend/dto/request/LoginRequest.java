package com.ptit.backend.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LoginRequest {

    @JsonProperty("username")
    @NotBlank(message = "Ten dang nhap khong duoc de trong")
    private String username;

    @JsonProperty("password")
    @NotBlank(message = "Mat khau khong duoc de trong")
    private String password;
}

