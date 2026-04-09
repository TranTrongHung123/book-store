package com.ptit.backend.repository;

import com.ptit.backend.entity.Review;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ReviewRepository extends JpaRepository<Review, Long> {

    Page<Review> findByBookBookId(Long bookId, Pageable pageable);

    Page<Review> findByBookBookIdAndIsApproved(Long bookId, Integer isApproved, Pageable pageable);
}

