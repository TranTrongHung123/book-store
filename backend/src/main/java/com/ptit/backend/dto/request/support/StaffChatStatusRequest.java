package com.ptit.backend.dto.request.support;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StaffChatStatusRequest {

    @JsonProperty("accepting_chats")
    private boolean acceptingChats = true;

    @JsonProperty("current_load")
    private Long currentLoad;

    @JsonProperty("max_load")
    private Long maxLoad;
}
