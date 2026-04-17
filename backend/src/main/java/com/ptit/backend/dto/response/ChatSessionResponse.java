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
public class ChatSessionResponse {

    @JsonProperty("session_id")
    private Long sessionId;

    private String status;

    @JsonProperty("started_at")
    private LocalDateTime startedAt;

    @JsonProperty("user_id")
    private Long userId;
}
