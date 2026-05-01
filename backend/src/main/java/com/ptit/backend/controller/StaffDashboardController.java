package com.ptit.backend.controller;

import com.ptit.backend.dto.response.ApiResponse;
import com.ptit.backend.dto.response.support.StaffDashboardSummaryResponse;
import com.ptit.backend.service.firebase.FirebaseChatService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/staff/dashboard")
public class StaffDashboardController {

    private static final int SUCCESS_CODE = 1000;
    private static final String SUCCESS_MESSAGE = "Thanh cong";

    private final FirebaseChatService firebaseChatService;

    public StaffDashboardController(FirebaseChatService firebaseChatService) {
        this.firebaseChatService = firebaseChatService;
    }

    @GetMapping("/summary")
    public ResponseEntity<ApiResponse<StaffDashboardSummaryResponse>> getSummary() {
        StaffDashboardSummaryResponse result = firebaseChatService.getStaffDashboardSummary();
        return ResponseEntity.ok(ApiResponse.<StaffDashboardSummaryResponse>builder()
                .code(SUCCESS_CODE)
                .message(SUCCESS_MESSAGE)
                .result(result)
                .build());
    }
}
