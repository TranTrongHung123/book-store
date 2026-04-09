package com.ptit.backend.repository;

import com.ptit.backend.entity.BookCategory;
import com.ptit.backend.entity.BookCategoryId;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BookCategoryRepository extends JpaRepository<BookCategory, BookCategoryId> {

    List<BookCategory> findByCategoryNameIgnoreCase(String categoryName);

    List<BookCategory> findByBookBookId(Long bookId);

    Optional<BookCategory> findFirstByBookBookIdAndIsPrimary(Long bookId, Integer isPrimary);
}

