package com.ptit.backend.repository;

import com.ptit.backend.entity.BookItem;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BookItemRepository extends JpaRepository<BookItem, Long> {

    Page<BookItem> findByBookBookId(Long bookId, Pageable pageable);

    Page<BookItem> findByBookBookIdAndStatus(Long bookId, String status, Pageable pageable);

    Page<BookItem> findByStatus(String status, Pageable pageable);
}

