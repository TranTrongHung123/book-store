package com.ptit.backend.repository;

import com.ptit.backend.entity.BookAuthor;
import com.ptit.backend.entity.BookAuthorId;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BookAuthorRepository extends JpaRepository<BookAuthor, BookAuthorId> {

	List<BookAuthor> findByBookBookId(Long bookId);
}

