package com.ptit.backend.service;

import com.ptit.backend.dto.request.BookRequest;
import com.ptit.backend.dto.response.BookResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface BookService {

    Page<BookResponse> getBooks(String q, String categoryName, Pageable pageable);

    BookResponse getBookById(Long id);

    BookResponse createBook(BookRequest request);

    BookResponse updateBook(Long id, BookRequest request);

    void deleteBook(Long id);
}

