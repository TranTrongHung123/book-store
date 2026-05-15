package com.ptit.backend.service;

import com.ptit.backend.dto.request.ReviewRequest;
import com.ptit.backend.dto.response.ReviewResponse;
import java.util.List;
import org.springframework.security.core.Authentication;

public interface ReviewService {

    List<ReviewResponse> getApprovedReviewsByBook(Long bookId);

    ReviewResponse createReview(Long bookId, ReviewRequest request, Authentication authentication);

    List<ReviewResponse> getReviewsForModeration(String status);

    ReviewResponse moderateReview(Long reviewId, ReviewRequest request);
}
