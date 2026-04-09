package com.ptit.backend.mapper;

import com.ptit.backend.dto.request.RentalRequest;
import com.ptit.backend.dto.response.RentalResponse;
import com.ptit.backend.entity.BookItem;
import com.ptit.backend.entity.Rental;
import com.ptit.backend.entity.User;
import java.util.List;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface RentalMapper {

    @Mapping(target = "rentalId", ignore = true)
    @Mapping(target = "user", source = "userId")
    @Mapping(target = "bookItem", source = "bookItemId")
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    Rental toEntity(RentalRequest request);

    @Mapping(source = "rentalId", target = "id")
    @Mapping(source = "user.userId", target = "userId")
    @Mapping(source = "bookItem.bookItemId", target = "bookItemId")
    @Mapping(source = "bookItem.book.title", target = "bookTitle")
    RentalResponse toResponse(Rental entity);

    List<RentalResponse> toResponseList(List<Rental> entities);

    default User mapUser(Long userId) {
        if (userId == null) {
            return null;
        }
        return User.builder().userId(userId).build();
    }

    default BookItem mapBookItem(Long bookItemId) {
        if (bookItemId == null) {
            return null;
        }
        return BookItem.builder().bookItemId(bookItemId).build();
    }
}

