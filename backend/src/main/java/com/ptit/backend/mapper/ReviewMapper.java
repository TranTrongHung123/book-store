package com.ptit.backend.mapper;

import com.ptit.backend.dto.request.ReviewRequest;
import com.ptit.backend.dto.response.ReviewResponse;
import com.ptit.backend.entity.Book;
import com.ptit.backend.entity.Review;
import com.ptit.backend.entity.User;
import java.util.List;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ReviewMapper {

    @Mapping(target = "reviewId", ignore = true)
    @Mapping(target = "book", source = "bookId")
    @Mapping(target = "user", source = "userId")
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    Review toEntity(ReviewRequest request);

    @Mapping(source = "reviewId", target = "id")
    @Mapping(source = "book.bookId", target = "bookId")
    @Mapping(source = "user.userId", target = "userId")
    @Mapping(source = "user.fullName", target = "userName")
    ReviewResponse toResponse(Review entity);

    List<ReviewResponse> toResponseList(List<Review> entities);

    default Book mapBook(Long bookId) {
        if (bookId == null) {
            return null;
        }
        return Book.builder().bookId(bookId).build();
    }

    default User mapUser(Long userId) {
        if (userId == null) {
            return null;
        }
        return User.builder().userId(userId).build();
    }
}

