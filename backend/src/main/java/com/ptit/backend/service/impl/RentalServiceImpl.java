package com.ptit.backend.service.impl;

import com.ptit.backend.dto.request.RentalRequest;
import com.ptit.backend.dto.request.RentalStatusUpdateRequest;
import com.ptit.backend.dto.response.RentalResponse;
import com.ptit.backend.entity.BookItem;
import com.ptit.backend.entity.Rental;
import com.ptit.backend.entity.User;
import com.ptit.backend.exception.AppException;
import com.ptit.backend.exception.ErrorCode;
import com.ptit.backend.mapper.RentalMapper;
import com.ptit.backend.repository.BookItemRepository;
import com.ptit.backend.repository.RentalRepository;
import com.ptit.backend.repository.UserRepository;
import com.ptit.backend.service.RentalService;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

@Service
@RequiredArgsConstructor
public class RentalServiceImpl implements RentalService {

    private static final int DEFAULT_RENTAL_DAYS = 14;

    private final RentalRepository rentalRepository;
    private final UserRepository userRepository;
    private final BookItemRepository bookItemRepository;
    private final RentalMapper rentalMapper;

    @Override
    public Page<RentalResponse> getRentals(Long userId, String status, Pageable pageable) {
        Page<Rental> rentals;
        if (userId != null && StringUtils.hasText(status)) {
            rentals = rentalRepository.findByUserUserIdAndStatus(userId, status.trim(), pageable);
        } else if (userId != null) {
            rentals = rentalRepository.findByUserUserId(userId, pageable);
        } else if (StringUtils.hasText(status)) {
            rentals = rentalRepository.findByStatus(status.trim(), pageable);
        } else {
            rentals = rentalRepository.findAll(pageable);
        }

        return rentals.map(rentalMapper::toResponse);
    }

    @Override
    public RentalResponse getRentalById(Long id) {
        Rental rental = rentalRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.RENTAL_NOT_FOUND));
        return rentalMapper.toResponse(rental);
    }

    @Override
    @Transactional
    public RentalResponse createRental(RentalRequest request) {
        if (request.getUserId() == null) {
            throw new AppException(ErrorCode.INVALID_REQUEST, "Truong user_id la bat buoc");
        }
        if (request.getBookItemId() == null) {
            throw new AppException(ErrorCode.INVALID_REQUEST, "Truong book_item_id la bat buoc");
        }

        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));

        BookItem bookItem = bookItemRepository.findById(request.getBookItemId())
                .orElseThrow(() -> new AppException(ErrorCode.BOOK_ITEM_NOT_FOUND));

        if (bookItem.getIsForRent() == null || bookItem.getIsForRent() != 1) {
            throw new AppException(ErrorCode.BOOK_ITEM_NOT_FOR_RENT);
        }
        if (StringUtils.hasText(bookItem.getStatus()) && !"San sang".equalsIgnoreCase(bookItem.getStatus())) {
            throw new AppException(ErrorCode.BOOK_ITEM_NOT_AVAILABLE);
        }

        Rental rental = rentalMapper.toEntity(request);
        rental.setUser(user);
        rental.setBookItem(bookItem);

        LocalDateTime rentDate = request.getRentDate() != null ? request.getRentDate() : LocalDateTime.now();
        LocalDateTime dueDate = request.getDueDate() != null ? request.getDueDate() : rentDate.plusDays(DEFAULT_RENTAL_DAYS);
        rental.setRentDate(rentDate);
        rental.setDueDate(dueDate);

        if (rental.getActualDeposit() == null) {
            rental.setActualDeposit(bookItem.getDepositAmount() != null ? bookItem.getDepositAmount() : BigDecimal.ZERO);
        }
        if (rental.getRentalFee() == null) {
            rental.setRentalFee(bookItem.getCurrentRentalPrice() != null ? bookItem.getCurrentRentalPrice() : BigDecimal.ZERO);
        }
        if (!StringUtils.hasText(rental.getPaymentStatus())) {
            rental.setPaymentStatus("Da thu coc");
        }
        if (!StringUtils.hasText(rental.getStatus())) {
            rental.setStatus("Dang thue");
        }

        bookItem.setStatus("Dang thue");
        bookItemRepository.save(bookItem);

        Rental savedRental = rentalRepository.save(rental);
        return rentalMapper.toResponse(savedRental);
    }

    @Override
    @Transactional
    public RentalResponse updateRentalStatus(Long id, RentalStatusUpdateRequest request) {
        Rental rental = rentalRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.RENTAL_NOT_FOUND));

        if (StringUtils.hasText(request.getStatus())) {
            rental.setStatus(request.getStatus());
        }
        if (StringUtils.hasText(request.getPaymentStatus())) {
            rental.setPaymentStatus(request.getPaymentStatus());
        }
        if (request.getReturnDate() != null) {
            rental.setReturnDate(request.getReturnDate());
        }
        if (request.getPenaltyFee() != null) {
            rental.setPenaltyFee(request.getPenaltyFee());
        }

        if ("Da tra".equalsIgnoreCase(rental.getStatus())) {
            BookItem bookItem = rental.getBookItem();
            bookItem.setStatus("San sang");
            bookItemRepository.save(bookItem);
        }

        Rental updatedRental = rentalRepository.save(rental);
        return rentalMapper.toResponse(updatedRental);
    }

    @Override
    @Transactional
    public void deleteRental(Long id) {
        Rental rental = rentalRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.RENTAL_NOT_FOUND));
        rentalRepository.delete(rental);
    }
}

