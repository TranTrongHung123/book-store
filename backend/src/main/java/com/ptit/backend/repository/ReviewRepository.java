package com.ptit.backend.repository;

import com.ptit.backend.entity.Review;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ReviewRepository extends JpaRepository<Review, Long> {

    Page<Review> findByBookBookId(Long bookId, Pageable pageable);

    Page<Review> findByBookBookIdAndIsApproved(Long bookId, Integer isApproved, Pageable pageable);

    List<Review> findByBookBookIdAndIsApprovedOrderByCreatedAtDesc(Long bookId, Integer isApproved);

    List<Review> findByIsApprovedOrderByCreatedAtDesc(Integer isApproved);

    List<Review> findAllByOrderByCreatedAtDesc();

    boolean existsByUserUserIdAndBookBookId(Long userId, Long bookId);
}

