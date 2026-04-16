package com.ptit.backend.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FirebaseCustomTokenResponse {

    @JsonProperty("token")
    private String token;

    @JsonProperty("uid")
    private String uid;

    @JsonProperty("primary_role")
    private String primaryRole;

    @JsonProperty("user_id")
    private Long userId;

    @JsonProperty("full_name")
    private String fullName;
}
