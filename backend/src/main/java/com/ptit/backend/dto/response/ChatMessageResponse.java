package com.ptit.backend.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChatMessageResponse {

    @JsonProperty("message_id")
    private Long messageId;

    @JsonProperty("sender_type")
    private String senderType; // USER | BOT

    private String content;

    @JsonProperty("created_at")
    private LocalDateTime createdAt;
}
