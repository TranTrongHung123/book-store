package com.ptit.backend.mapper;

import com.ptit.backend.dto.request.OrderItemRequest;
import com.ptit.backend.dto.response.OrderItemResponse;
import com.ptit.backend.entity.Book;
import com.ptit.backend.entity.OrderDetail;
import java.util.List;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface OrderItemMapper {

    @Mapping(target = "orderDetailId", ignore = true)
    @Mapping(target = "order", ignore = true)
    @Mapping(target = "book", source = "bookId")
    OrderDetail toEntity(OrderItemRequest request);

    @Mapping(source = "book.bookId", target = "bookItemId")
    @Mapping(source = "book.title", target = "title")
    OrderItemResponse toResponse(OrderDetail entity);

    List<OrderItemResponse> toResponseList(List<OrderDetail> entities);

    default Book mapBook(Long bookId) {
        if (bookId == null) {
            return null;
        }
        return Book.builder().bookId(bookId).build();
    }
}

