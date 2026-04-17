package com.ptit.backend.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;


public record ChatRequest(

        @NotNull(message = "Session ID không được để trống")
        Long sessionId,

        @NotBlank(message = "Nội dung tin nhắn không được để trống")
        @Size(max = 2000, message = "Tin nhắn không được vượt quá 2000 ký tự")
        String message
) {
}
