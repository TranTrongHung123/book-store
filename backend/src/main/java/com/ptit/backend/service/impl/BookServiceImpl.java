package com.ptit.backend.service.impl;

import com.ptit.backend.dto.request.BookRequest;
import com.ptit.backend.dto.response.BookResponse;
import com.ptit.backend.entity.Author;
import com.ptit.backend.entity.Book;
import com.ptit.backend.entity.BookAuthor;
import com.ptit.backend.entity.BookAuthorId;
import com.ptit.backend.entity.BookCategory;
import com.ptit.backend.entity.BookCategoryId;
import com.ptit.backend.entity.Category;
import com.ptit.backend.entity.Publisher;
import com.ptit.backend.exception.AppException;
import com.ptit.backend.exception.ErrorCode;
import com.ptit.backend.mapper.BookMapper;
import com.ptit.backend.repository.AuthorRepository;
import com.ptit.backend.repository.BookAuthorRepository;
import com.ptit.backend.repository.BookCategoryRepository;
import com.ptit.backend.repository.BookRepository;
import com.ptit.backend.repository.CategoryRepository;
import com.ptit.backend.repository.OrderDetailRepository;
import com.ptit.backend.repository.PublisherRepository;
import com.ptit.backend.repository.RentalRepository;
import com.ptit.backend.service.BookService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

@Service
@RequiredArgsConstructor
public class BookServiceImpl implements BookService {

    private final BookRepository bookRepository;
    private final PublisherRepository publisherRepository;
    private final AuthorRepository authorRepository;
    private final CategoryRepository categoryRepository;
    private final BookAuthorRepository bookAuthorRepository;
    private final BookCategoryRepository bookCategoryRepository;
    private final OrderDetailRepository orderDetailRepository;
    private final RentalRepository rentalRepository;
    private final BookMapper bookMapper;

    @Override
    public Page<BookResponse> getBooks(String q, String categoryName, Pageable pageable) {
        String keyword = StringUtils.hasText(q) ? q.trim() : null;
        String categoryFilter = StringUtils.hasText(categoryName) ? categoryName.trim() : null;

        Page<Book> books = bookRepository.searchBooks(keyword, categoryFilter, pageable);
        return books.map(this::buildBookResponse);
    }

    @Override
    public BookResponse getBookById(Long id) {
        Book book = bookRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.BOOK_NOT_FOUND));
        return buildBookResponse(book);
    }

    @Override
    @Transactional
    public BookResponse createBook(BookRequest request) {
        Book book = bookMapper.toEntity(request);

        if (request.getPublisherId() != null) {
            Publisher publisher = publisherRepository.findById(request.getPublisherId())
                    .orElseThrow(() -> new AppException(ErrorCode.PUBLISHER_NOT_FOUND));
            book.setPublisher(publisher);
        }

        if (book.getTotalStock() == null) {
            book.setTotalStock(0);
        }

        Book savedBook = bookRepository.save(book);
        upsertBookRelations(savedBook, request);

        return buildBookResponse(savedBook);
    }

    @Override
    @Transactional
    public BookResponse updateBook(Long id, BookRequest request) {
        Book existingBook = bookRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.BOOK_NOT_FOUND));

        if (request.getPublisherId() != null) {
            Publisher publisher = publisherRepository.findById(request.getPublisherId())
                    .orElseThrow(() -> new AppException(ErrorCode.PUBLISHER_NOT_FOUND));
            existingBook.setPublisher(publisher);
        }

        if (request.getTitle() != null) {
            existingBook.setTitle(request.getTitle());
        }
        if (request.getPublicationYear() != null) {
            existingBook.setPublicationYear(request.getPublicationYear());
        }
        if (request.getLanguage() != null) {
            existingBook.setLanguage(request.getLanguage());
        }
        if (request.getOriginalPrice() != null) {
            existingBook.setOriginalPrice(request.getOriginalPrice());
        }
        if (request.getSellingPrice() != null) {
            existingBook.setSellingPrice(request.getSellingPrice());
        }
        if (request.getTotalStock() != null) {
            existingBook.setTotalStock(request.getTotalStock());
        }
        if (request.getDescription() != null) {
            existingBook.setDescription(request.getDescription());
        }
        if (request.getCoverImage() != null) {
            existingBook.setCoverImage(request.getCoverImage());
        }
        if (request.getStatus() != null) {
            existingBook.setStatus(request.getStatus());
        }

        Book updatedBook = bookRepository.save(existingBook);
        upsertBookRelations(updatedBook, request);

        return buildBookResponse(updatedBook);
    }

    @Override
    @Transactional
    public void deleteBook(Long id) {
        Book book = bookRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.BOOK_NOT_FOUND));

        bookAuthorRepository.deleteAll(bookAuthorRepository.findByBookBookId(id));
        bookCategoryRepository.deleteAll(bookCategoryRepository.findByBookBookId(id));
        bookRepository.delete(book);
    }

    private void upsertBookRelations(Book book, BookRequest request) {
        if (StringUtils.hasText(request.getAuthorName())) {
            Author author = authorRepository.findByNameIgnoreCase(request.getAuthorName().trim())
                    .orElseGet(() -> authorRepository.save(Author.builder().name(request.getAuthorName().trim()).build()));

            List<BookAuthor> existingBookAuthors = bookAuthorRepository.findByBookBookId(book.getBookId());
            bookAuthorRepository.deleteAll(existingBookAuthors);

            BookAuthor bookAuthor = BookAuthor.builder()
                    .id(BookAuthorId.builder().bookId(book.getBookId()).authorId(author.getAuthorId()).build())
                    .book(book)
                    .author(author)
                    .authorRole("Tac gia")
                    .build();
            bookAuthorRepository.save(bookAuthor);
        }

        if (StringUtils.hasText(request.getCategoryName())) {
            Category category = categoryRepository.findByNameIgnoreCase(request.getCategoryName().trim())
                    .orElseGet(() -> categoryRepository.save(Category.builder().name(request.getCategoryName().trim()).build()));

            List<BookCategory> existingBookCategories = bookCategoryRepository.findByBookBookId(book.getBookId());
            bookCategoryRepository.deleteAll(existingBookCategories);

            BookCategory bookCategory = BookCategory.builder()
                    .id(BookCategoryId.builder().bookId(book.getBookId()).categoryId(category.getCategoryId()).build())
                    .book(book)
                    .category(category)
                    .isPrimary(1)
                    .build();
            bookCategoryRepository.save(bookCategory);
        }
    }

    private BookResponse buildBookResponse(Book book) {
        String authorName = bookAuthorRepository.findByBookBookId(book.getBookId()).stream()
                .findFirst()
                .map(bookAuthor -> bookAuthor.getAuthor().getName())
                .orElse(null);

        String categoryName = bookCategoryRepository.findFirstByBookBookIdAndIsPrimary(book.getBookId(), 1)
                .map(bookCategory -> bookCategory.getCategory().getName())
                .orElseGet(() -> bookCategoryRepository.findByBookBookId(book.getBookId()).stream()
                        .findFirst()
                        .map(bookCategory -> bookCategory.getCategory().getName())
                        .orElse(null));

        Integer soldCount = orderDetailRepository.sumSoldQuantityByBookId(book.getBookId()).intValue();
        Integer rentalCount = (int) rentalRepository.countByBookItemBookBookId(book.getBookId());

        return bookMapper.toResponse(book, authorName, categoryName, soldCount, rentalCount, List.of());
    }
}

