package com.ptit.backend.controller;

import com.ptit.backend.dto.response.ApiResponse;
import com.ptit.backend.dto.response.FirebaseCustomTokenResponse;
import com.ptit.backend.service.firebase.FirebaseChatService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/firebase")
@RequiredArgsConstructor
public class FirebaseController {

    private static final int SUCCESS_CODE = 1000;
    private static final String SUCCESS_MESSAGE = "Thanh cong";

    private final FirebaseChatService firebaseChatService;

    @GetMapping("/custom-token")
    public ResponseEntity<ApiResponse<FirebaseCustomTokenResponse>> createCustomToken(Authentication authentication) {
        FirebaseCustomTokenResponse result = firebaseChatService.createCustomToken(authentication);
        return ResponseEntity.ok(ApiResponse.<FirebaseCustomTokenResponse>builder()
                .code(SUCCESS_CODE)
                .message(SUCCESS_MESSAGE)
                .result(result)
                .build());
    }
}
