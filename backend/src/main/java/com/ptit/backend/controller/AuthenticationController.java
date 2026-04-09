package com.ptit.backend.controller;

import com.ptit.backend.dto.request.LoginRequest;
import com.ptit.backend.dto.response.ApiResponse;
import com.ptit.backend.dto.response.LoginResponse;
import com.ptit.backend.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthenticationController {

    private static final int SUCCESS_CODE = 1000;
    private static final String SUCCESS_MESSAGE = "Thanh cong";

    private final AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<LoginResponse>> login(@Valid @RequestBody LoginRequest request) {
        LoginResponse result = authService.login(request);

        ApiResponse<LoginResponse> response = ApiResponse.<LoginResponse>builder()
                .code(SUCCESS_CODE)
                .message(SUCCESS_MESSAGE)
                .result(result)
                .build();

        return ResponseEntity.ok(response);
    }
}

