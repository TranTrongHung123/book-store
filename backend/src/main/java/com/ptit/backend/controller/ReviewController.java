package com.ptit.backend.controller;

import com.ptit.backend.dto.request.ReviewRequest;
import com.ptit.backend.dto.response.ApiResponse;
import com.ptit.backend.dto.response.ReviewResponse;
import com.ptit.backend.service.ReviewService;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class ReviewController {

    private static final int SUCCESS_CODE = 1000;
    private static final String SUCCESS_MESSAGE = "Thanh cong";

    private final ReviewService reviewService;

    @GetMapping("/api/v1/books/{bookId}/reviews")
    public ResponseEntity<ApiResponse<List<ReviewResponse>>> getBookReviews(@PathVariable Long bookId) {
        return ResponseEntity.ok(success(reviewService.getApprovedReviewsByBook(bookId)));
    }

    @PostMapping("/api/v1/books/{bookId}/reviews")
    public ResponseEntity<ApiResponse<ReviewResponse>> createReview(
            @PathVariable Long bookId,
            @Valid @RequestBody ReviewRequest request,
            Authentication authentication
    ) {
        ReviewResponse result = reviewService.createReview(bookId, request, authentication);
        return ResponseEntity.status(HttpStatus.CREATED).body(success(result));
    }

    @GetMapping("/api/v1/admin/reviews")
    public ResponseEntity<ApiResponse<List<ReviewResponse>>> getReviewsForModeration(
            @RequestParam(name = "status", defaultValue = "pending") String status
    ) {
        return ResponseEntity.ok(success(reviewService.getReviewsForModeration(status)));
    }

    @PatchMapping("/api/v1/admin/reviews/{reviewId}")
    public ResponseEntity<ApiResponse<ReviewResponse>> moderateReview(
            @PathVariable Long reviewId,
            @RequestBody ReviewRequest request
    ) {
        return ResponseEntity.ok(success(reviewService.moderateReview(reviewId, request)));
    }

    private <T> ApiResponse<T> success(T result) {
        return ApiResponse.<T>builder()
                .code(SUCCESS_CODE)
                .message(SUCCESS_MESSAGE)
                .result(result)
                .build();
    }
}
