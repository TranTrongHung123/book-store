package com.ptit.backend.controller;

import com.ptit.backend.dto.request.support.StaffChatStatusRequest;
import com.ptit.backend.dto.request.support.SupportMessageRequest;
import com.ptit.backend.dto.request.support.SupportTagsRequest;
import com.ptit.backend.dto.response.ApiResponse;
import com.ptit.backend.dto.response.support.SupportConversationResponse;
import com.ptit.backend.service.firebase.FirebaseChatService;
import jakarta.validation.Valid;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/staff/chat")
@RequiredArgsConstructor
public class StaffChatController {

    private static final int SUCCESS_CODE = 1000;
    private static final String SUCCESS_MESSAGE = "Thanh cong";

    private final FirebaseChatService firebaseChatService;

    @GetMapping("/conversations")
    public ResponseEntity<ApiResponse<List<Map<String, Object>>>> getConversations(Authentication authentication) {
        return ResponseEntity.ok(success(firebaseChatService.getStaffConversations(authentication)));
    }

    @GetMapping("/conversations/{conversationId}/messages")
    public ResponseEntity<ApiResponse<List<Map<String, Object>>>> getMessages(
            Authentication authentication,
            @PathVariable String conversationId
    ) {
        return ResponseEntity.ok(success(firebaseChatService.getStaffConversationMessages(authentication, conversationId)));
    }

    @PostMapping("/conversations/{conversationId}/messages")
    public ResponseEntity<ApiResponse<SupportConversationResponse>> sendMessage(
            Authentication authentication,
            @PathVariable String conversationId,
            @Valid @RequestBody SupportMessageRequest request
    ) {
        return ResponseEntity.ok(success(firebaseChatService.sendStaffMessage(authentication, conversationId, request)));
    }

    @PatchMapping("/conversations/{conversationId}/tags")
    public ResponseEntity<ApiResponse<SupportConversationResponse>> updateTags(
            Authentication authentication,
            @PathVariable String conversationId,
            @RequestBody SupportTagsRequest request
    ) {
        return ResponseEntity.ok(success(firebaseChatService.updateStaffConversationTags(authentication, conversationId, request)));
    }

    @PatchMapping("/conversations/{conversationId}/read")
    public ResponseEntity<ApiResponse<SupportConversationResponse>> markRead(
            Authentication authentication,
            @PathVariable String conversationId
    ) {
        return ResponseEntity.ok(success(firebaseChatService.markStaffConversationRead(authentication, conversationId)));
    }

    @PostMapping("/status")
    public ResponseEntity<ApiResponse<SupportConversationResponse>> upsertStatus(
            Authentication authentication,
            @RequestBody StaffChatStatusRequest request
    ) {
        return ResponseEntity.ok(success(firebaseChatService.upsertStaffChatStatus(authentication, request)));
    }

    private <T> ApiResponse<T> success(T result) {
        return ApiResponse.<T>builder()
                .code(SUCCESS_CODE)
                .message(SUCCESS_MESSAGE)
                .result(result)
                .build();
    }
}
