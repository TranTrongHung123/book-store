package com.ptit.backend.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserRequest {

    @JsonProperty("username")
    @Size(min = 3, max = 50, message = "Ten dang nhap phai tu 3 den 50 ky tu")
    private String username;

    @JsonProperty("password")
    @Size(min = 6, max = 255, message = "Mat khau phai tu 6 den 255 ky tu")
    private String password;

    @JsonProperty("role_id")
    private Long roleId;

    @JsonProperty("full_name")
    private String fullName;

    @JsonProperty("email")
    @Email(message = "Email khong dung dinh dang")
    private String email;

    @JsonProperty("phone")
    private String phone;

    @JsonProperty("address")
    private String address;

    @JsonProperty("total_points")
    @Min(value = 0, message = "Tong diem tich luy phai lon hon hoac bang 0")
    private Integer totalPoints;

    @JsonProperty("status")
    private Integer status;
}

