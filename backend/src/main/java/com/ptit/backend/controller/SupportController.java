package com.ptit.backend.controller;

import com.ptit.backend.dto.request.support.SupportOpenRequest;
import com.ptit.backend.dto.response.ApiResponse;
import com.ptit.backend.dto.response.support.SupportConversationResponse;
import com.ptit.backend.service.firebase.FirebaseChatService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/support")
@RequiredArgsConstructor
public class SupportController {

    private static final int SUCCESS_CODE = 1000;
    private static final String SUCCESS_MESSAGE = "Thanh cong";

    private final FirebaseChatService firebaseChatService;

    @PostMapping("/open")
    public ResponseEntity<ApiResponse<SupportConversationResponse>> openSupport(
            Authentication authentication,
            @Valid @RequestBody SupportOpenRequest request
    ) {
        SupportConversationResponse result = firebaseChatService.openConversation(authentication, request);
        return ResponseEntity.ok(success(result));
    }

    @PostMapping("/claim-waiting")
    public ResponseEntity<ApiResponse<SupportConversationResponse>> claimWaitingConversation(
            Authentication authentication
    ) {
        SupportConversationResponse result = firebaseChatService.claimWaitingConversation(authentication);
        return ResponseEntity.ok(success(result));
    }

    @PatchMapping("/{conversationId}/close")
    public ResponseEntity<ApiResponse<SupportConversationResponse>> closeSupport(
            Authentication authentication,
            @PathVariable String conversationId
    ) {
        SupportConversationResponse result = firebaseChatService.closeConversation(authentication, conversationId);
        return ResponseEntity.ok(success(result));
    }

    private <T> ApiResponse<T> success(T result) {
        return ApiResponse.<T>builder()
                .code(SUCCESS_CODE)
                .message(SUCCESS_MESSAGE)
                .result(result)
                .build();
    }
}
