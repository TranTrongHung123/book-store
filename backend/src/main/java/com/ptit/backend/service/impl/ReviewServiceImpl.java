package com.ptit.backend.service.impl;

import com.ptit.backend.dto.request.ReviewRequest;
import com.ptit.backend.dto.response.ReviewResponse;
import com.ptit.backend.entity.Book;
import com.ptit.backend.entity.Review;
import com.ptit.backend.entity.User;
import com.ptit.backend.exception.AppException;
import com.ptit.backend.exception.ErrorCode;
import com.ptit.backend.mapper.ReviewMapper;
import com.ptit.backend.repository.BookRepository;
import com.ptit.backend.repository.ReviewRepository;
import com.ptit.backend.repository.UserRepository;
import com.ptit.backend.service.ReviewService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

@Service
@RequiredArgsConstructor
public class ReviewServiceImpl implements ReviewService {

    private static final int REVIEW_PENDING = 0;
    private static final int REVIEW_APPROVED = 1;
    private static final int REVIEW_REJECTED = -1;

    private final ReviewRepository reviewRepository;
    private final BookRepository bookRepository;
    private final UserRepository userRepository;
    private final ReviewMapper reviewMapper;

    @Override
    @Transactional(readOnly = true)
    public List<ReviewResponse> getApprovedReviewsByBook(Long bookId) {
        ensureBookExists(bookId);
        return reviewMapper.toResponseList(
                reviewRepository.findByBookBookIdAndIsApprovedOrderByCreatedAtDesc(bookId, REVIEW_APPROVED)
        );
    }

    @Override
    @Transactional
    public ReviewResponse createReview(Long bookId, ReviewRequest request, Authentication authentication) {
        User user = resolveAuthenticatedUser(authentication);
        Book book = ensureBookExists(bookId);
        validateReviewContent(request);

        if (reviewRepository.existsByUserUserIdAndBookBookId(user.getUserId(), bookId)) {
            throw new AppException(ErrorCode.INVALID_REQUEST, "Ban da danh gia sach nay.");
        }

        Review review = Review.builder()
                .user(user)
                .book(book)
                .rating(request.getRating())
                .comment(normalizeComment(request.getComment()))
                .isApproved(REVIEW_PENDING)
                .build();

        return reviewMapper.toResponse(reviewRepository.save(review));
    }

    @Override
    @Transactional(readOnly = true)
    public List<ReviewResponse> getReviewsForModeration(String status) {
        Integer resolvedStatus = resolveModerationStatus(status);
        List<Review> reviews = resolvedStatus == null
                ? reviewRepository.findAllByOrderByCreatedAtDesc()
                : reviewRepository.findByIsApprovedOrderByCreatedAtDesc(resolvedStatus);
        return reviewMapper.toResponseList(reviews);
    }

    @Override
    @Transactional
    public ReviewResponse moderateReview(Long reviewId, ReviewRequest request) {
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new AppException(ErrorCode.INVALID_REQUEST, "Khong tim thay danh gia"));

        Integer nextStatus = request.getIsApproved();
        if (nextStatus == null || (nextStatus != REVIEW_APPROVED && nextStatus != REVIEW_REJECTED)) {
            throw new AppException(ErrorCode.INVALID_REQUEST, "Trang thai duyet phai la 1 hoac -1");
        }

        review.setIsApproved(nextStatus);
        return reviewMapper.toResponse(reviewRepository.save(review));
    }

    private Book ensureBookExists(Long bookId) {
        if (bookId == null) {
            throw new AppException(ErrorCode.INVALID_REQUEST, "Ma sach la bat buoc");
        }
        return bookRepository.findById(bookId)
                .orElseThrow(() -> new AppException(ErrorCode.BOOK_NOT_FOUND));
    }

    private void validateReviewContent(ReviewRequest request) {
        if (request.getRating() == null || request.getRating() < 1 || request.getRating() > 5) {
            throw new AppException(ErrorCode.INVALID_REQUEST, "So sao danh gia phai tu 1 den 5");
        }
        String comment = normalizeComment(request.getComment());
        if (!StringUtils.hasText(comment)) {
            throw new AppException(ErrorCode.INVALID_REQUEST, "Noi dung binh luan khong duoc de trong");
        }
        if (comment.length() > 2000) {
            throw new AppException(ErrorCode.INVALID_REQUEST, "Noi dung binh luan khong duoc vuot qua 2000 ky tu");
        }
    }

    private String normalizeComment(String comment) {
        return String.valueOf(comment == null ? "" : comment).trim();
    }

    private Integer resolveModerationStatus(String status) {
        if (!StringUtils.hasText(status) || "all".equalsIgnoreCase(status.trim())) {
            return null;
        }
        String normalized = status.trim().toLowerCase();
        return switch (normalized) {
            case "pending" -> REVIEW_PENDING;
            case "approved" -> REVIEW_APPROVED;
            case "rejected" -> REVIEW_REJECTED;
            default -> throw new AppException(ErrorCode.INVALID_REQUEST, "Trang thai review khong hop le");
        };
    }

    private User resolveAuthenticatedUser(Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new AppException(ErrorCode.UNAUTHENTICATED);
        }

        Object principal = authentication.getPrincipal();
        String username = null;
        if (principal instanceof UserDetails userDetails) {
            username = userDetails.getUsername();
        } else if (principal instanceof String principalString) {
            username = principalString;
        }

        if (!StringUtils.hasText(username)) {
            throw new AppException(ErrorCode.UNAUTHENTICATED);
        }

        return userRepository.findByUsername(username.trim())
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));
    }
}
