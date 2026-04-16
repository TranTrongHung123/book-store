package com.ptit.backend.dto.response.support;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SupportConversationResponse {

    @JsonProperty("conversation_id")
    private String conversationId;

    @JsonProperty("status")
    private String status;

    @JsonProperty("staff_uid")
    private String staffUid;

    @JsonProperty("staff_id")
    private Long staffId;

    @JsonProperty("staff_name")
    private String staffName;

    @JsonProperty("message")
    private String message;
}
