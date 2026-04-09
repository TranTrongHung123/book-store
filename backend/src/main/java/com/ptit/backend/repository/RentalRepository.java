package com.ptit.backend.repository;

import com.ptit.backend.entity.Rental;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RentalRepository extends JpaRepository<Rental, Long> {

    Page<Rental> findByUserUserId(Long userId, Pageable pageable);

    Page<Rental> findByUserUserIdAndStatus(Long userId, String status, Pageable pageable);

    Page<Rental> findByStatus(String status, Pageable pageable);

    long countByBookItemBookBookId(Long bookId);
}

