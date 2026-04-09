package com.ptit.backend.controller;

import com.ptit.backend.dto.request.RentalRequest;
import com.ptit.backend.dto.request.RentalStatusUpdateRequest;
import com.ptit.backend.dto.response.ApiResponse;
import com.ptit.backend.dto.response.PagedResponse;
import com.ptit.backend.dto.response.RentalResponse;
import com.ptit.backend.service.RentalService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/rentals")
@RequiredArgsConstructor
public class RentalController {

    private static final int SUCCESS_CODE = 0;
    private static final String SUCCESS_MESSAGE = "Thanh cong";

    private final RentalService rentalService;

    @GetMapping
    public ResponseEntity<ApiResponse<PagedResponse<RentalResponse>>> getRentals(
            @RequestParam(name = "_page", defaultValue = "1") int page,
            @RequestParam(name = "_limit", defaultValue = "10") int limit,
            @RequestParam(name = "_sort", defaultValue = "rentalId") String sort,
            @RequestParam(name = "_order", defaultValue = "desc") String order,
            @RequestParam(name = "user_id", required = false) Long userId,
            @RequestParam(name = "status", required = false) String status
    ) {
        Pageable pageable = buildPageable(page, limit, sort, order);
        Page<RentalResponse> result = rentalService.getRentals(userId, status, pageable);

        ApiResponse<PagedResponse<RentalResponse>> response = ApiResponse.<PagedResponse<RentalResponse>>builder()
                .code(SUCCESS_CODE)
                .message(SUCCESS_MESSAGE)
                .result(toPagedResponse(result))
                .build();

        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<RentalResponse>> getRentalById(@PathVariable Long id) {
        RentalResponse result = rentalService.getRentalById(id);
        return ResponseEntity.ok(success(result));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<RentalResponse>> createRental(@Valid @RequestBody RentalRequest request) {
        RentalResponse result = rentalService.createRental(request);
        return ResponseEntity.ok(success(result));
    }

    @PatchMapping("/{id}")
    public ResponseEntity<ApiResponse<RentalResponse>> updateRentalStatus(
            @PathVariable Long id,
            @RequestBody RentalStatusUpdateRequest request
    ) {
        RentalResponse result = rentalService.updateRentalStatus(id, request);
        return ResponseEntity.ok(success(result));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Object>> deleteRental(@PathVariable Long id) {
        rentalService.deleteRental(id);
        return ResponseEntity.ok(success(null));
    }

    private Pageable buildPageable(int page, int limit, String sort, String order) {
        Sort.Direction direction = "desc".equalsIgnoreCase(order) ? Sort.Direction.DESC : Sort.Direction.ASC;
        int safePage = Math.max(page - 1, 0);
        int safeLimit = Math.max(limit, 1);
        return PageRequest.of(safePage, safeLimit, Sort.by(direction, sort));
    }

    private <T> ApiResponse<T> success(T result) {
        return ApiResponse.<T>builder()
                .code(SUCCESS_CODE)
                .message(SUCCESS_MESSAGE)
                .result(result)
                .build();
    }

    private <T> PagedResponse<T> toPagedResponse(Page<T> page) {
        return PagedResponse.<T>builder()
                .content(page.getContent())
                .page(page.getNumber() + 1)
                .limit(page.getSize())
                .totalElements(page.getTotalElements())
                .totalPages(page.getTotalPages())
                .build();
    }
}

