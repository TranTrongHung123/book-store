package com.ptit.backend.controller;

import com.ptit.backend.dto.request.FlashSaleCampaignRequest;
import com.ptit.backend.dto.request.FlashSaleItemRequest;
import com.ptit.backend.dto.response.ApiResponse;
import com.ptit.backend.dto.response.FlashSaleCampaignResponse;
import com.ptit.backend.dto.response.FlashSaleItemResponse;
import com.ptit.backend.dto.response.PagedResponse;
import com.ptit.backend.service.FlashSaleAdminService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/flash-sale/campaigns")
@RequiredArgsConstructor
public class FlashSaleController {

    private static final int SUCCESS_CODE = 1000;
    private static final String SUCCESS_MESSAGE = "Thanh cong";

    private final FlashSaleAdminService flashSaleAdminService;

    @GetMapping
    public ResponseEntity<ApiResponse<PagedResponse<FlashSaleCampaignResponse>>> getCampaigns(
            @RequestParam(name = "_page", defaultValue = "0") int page,
            @RequestParam(name = "_limit", defaultValue = "10") int limit,
            @RequestParam(name = "_sort", defaultValue = "campaignId") String sort,
            @RequestParam(name = "_order", defaultValue = "desc") String order
    ) {
        Pageable pageable = buildPageable(page, limit, sort, order);
        Page<FlashSaleCampaignResponse> result = flashSaleAdminService.getCampaigns(pageable);
        return ResponseEntity.ok(success(toPagedResponse(result)));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<FlashSaleCampaignResponse>> createCampaign(
            @Valid @RequestBody FlashSaleCampaignRequest request
    ) {
        FlashSaleCampaignResponse result = flashSaleAdminService.createCampaign(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(success(result));
    }

    @GetMapping("/{campaignId}/items")
    public ResponseEntity<ApiResponse<PagedResponse<FlashSaleItemResponse>>> getCampaignItems(
            @PathVariable Long campaignId,
            @RequestParam(name = "_page", defaultValue = "0") int page,
            @RequestParam(name = "_limit", defaultValue = "10") int limit,
            @RequestParam(name = "_sort", defaultValue = "flashSaleItemId") String sort,
            @RequestParam(name = "_order", defaultValue = "desc") String order
    ) {
        Pageable pageable = buildPageable(page, limit, sort, order);
        Page<FlashSaleItemResponse> result = flashSaleAdminService.getCampaignItems(campaignId, pageable);
        return ResponseEntity.ok(success(toPagedResponse(result)));
    }

    @PostMapping("/{campaignId}/items")
    public ResponseEntity<ApiResponse<FlashSaleItemResponse>> addCampaignItem(
            @PathVariable Long campaignId,
            @Valid @RequestBody FlashSaleItemRequest request
    ) {
        FlashSaleItemResponse result = flashSaleAdminService.addCampaignItem(campaignId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(success(result));
    }

    @DeleteMapping("/{campaignId}")
    public ResponseEntity<Void> deleteCampaign(@PathVariable Long campaignId) {
        flashSaleAdminService.deleteCampaign(campaignId);
        return ResponseEntity.noContent().build(); // Trả về 204 Xóa thành công
    }

    private Pageable buildPageable(int page, int limit, String sort, String order) {
        Sort.Direction direction = "desc".equalsIgnoreCase(order) ? Sort.Direction.DESC : Sort.Direction.ASC;
        return PageRequest.of(Math.max(page, 0), Math.max(limit, 1), Sort.by(direction, sort));
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
                .page(page.getNumber())
                .limit(page.getSize())
                .totalElements(page.getTotalElements())
                .totalPages(page.getTotalPages())
                .build();
    }
}
