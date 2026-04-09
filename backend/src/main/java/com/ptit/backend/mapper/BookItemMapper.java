package com.ptit.backend.mapper;

import com.ptit.backend.dto.request.BookItemRequest;
import com.ptit.backend.dto.response.BookItemResponse;
import com.ptit.backend.entity.Book;
import com.ptit.backend.entity.BookItem;
import java.util.List;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface BookItemMapper {

    @Mapping(target = "bookItemId", ignore = true)
    @Mapping(target = "book", source = "bookId")
    @Mapping(target = "version", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    BookItem toEntity(BookItemRequest request);

    @Mapping(source = "bookItemId", target = "id")
    @Mapping(source = "book.bookId", target = "bookId")
    BookItemResponse toResponse(BookItem entity);

    List<BookItemResponse> toResponseList(List<BookItem> entities);

    default Book mapBook(Long bookId) {
        if (bookId == null) {
            return null;
        }
        return Book.builder().bookId(bookId).build();
    }
}

