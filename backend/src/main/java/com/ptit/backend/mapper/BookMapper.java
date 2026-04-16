package com.ptit.backend.mapper;

import com.ptit.backend.dto.request.BookRequest;
import com.ptit.backend.dto.response.BookResponse;
import com.ptit.backend.entity.Book;
import com.ptit.backend.entity.BookCategory;
import com.ptit.backend.entity.Publisher;
import java.util.List;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface BookMapper {

    @Mapping(target = "bookId", ignore = true)
    @Mapping(target = "publisher", source = "publisherId")
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    Book toEntity(BookRequest request);

    @Mapping(source = "bookId", target = "id")
    @Mapping(target = "authorName", ignore = true)
    @Mapping(target = "categoryName", ignore = true)
    @Mapping(target = "soldCount", ignore = true)
    @Mapping(target = "rentalCount", ignore = true)
    @Mapping(target = "longDescription", ignore = true)
    @Mapping(target = "galleryImages", ignore = true)
    BookResponse toResponse(Book entity);

    @Mapping(source = "book.bookId", target = "id")
    @Mapping(source = "book.title", target = "title")
    @Mapping(source = "book.originalPrice", target = "originalPrice")
    @Mapping(source = "book.sellingPrice", target = "sellingPrice")
    @Mapping(source = "book.coverImage", target = "coverImage")
    @Mapping(source = "book.totalStock", target = "totalStock")
    @Mapping(source = "book.description", target = "description")
    @Mapping(source = "book.description", target = "longDescription")
    @Mapping(source = "authorName", target = "authorName")
    @Mapping(source = "categoryName", target = "categoryName")
    @Mapping(source = "soldCount", target = "soldCount")
    @Mapping(source = "rentalCount", target = "rentalCount")
    @Mapping(source = "galleryImages", target = "galleryImages")
    BookResponse toResponse(
            Book book,
            String authorName,
            String categoryName,
            Integer soldCount,
            Integer rentalCount,
            List<String> galleryImages
    );

    @Mapping(source = "book.bookId", target = "id")
    @Mapping(source = "book.title", target = "title")
    @Mapping(source = "book.originalPrice", target = "originalPrice")
    @Mapping(source = "book.sellingPrice", target = "sellingPrice")
    @Mapping(source = "book.coverImage", target = "coverImage")
    @Mapping(source = "book.totalStock", target = "totalStock")
    @Mapping(source = "book.description", target = "description")
    @Mapping(source = "book.description", target = "longDescription")
    @Mapping(source = "category.category.name", target = "categoryName")
    @Mapping(target = "authorName", ignore = true)
    @Mapping(target = "soldCount", ignore = true)
    @Mapping(target = "rentalCount", ignore = true)
    @Mapping(target = "galleryImages", ignore = true)
    BookResponse toResponseFromCategory(Book book, BookCategory category);

    List<BookResponse> toResponseList(List<Book> entities);

    default Publisher mapPublisher(Long publisherId) {
        if (publisherId == null) {
            return null;
        }
        return Publisher.builder().publisherId(publisherId).build();
    }
}


