package com.ptit.backend.service;

import com.ptit.backend.dto.request.RentalRequest;
import com.ptit.backend.dto.request.RentalStatusUpdateRequest;
import com.ptit.backend.dto.response.RentalResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface RentalService {

    Page<RentalResponse> getRentals(Long userId, String status, Pageable pageable);

    RentalResponse getRentalById(Long id);

    RentalResponse createRental(RentalRequest request);

    RentalResponse updateRentalStatus(Long id, RentalStatusUpdateRequest request);

    void deleteRental(Long id);
}

