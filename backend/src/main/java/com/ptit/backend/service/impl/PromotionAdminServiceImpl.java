package com.ptit.backend.service.impl;

import com.ptit.backend.dto.request.PromotionRequest;
import com.ptit.backend.dto.response.PromotionResponse;
import com.ptit.backend.entity.Promotion;
import com.ptit.backend.exception.AppException;
import com.ptit.backend.exception.ErrorCode;
import com.ptit.backend.mapper.PromotionMapper;
import com.ptit.backend.repository.PromotionRepository;
import com.ptit.backend.service.PromotionAdminService;
import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

@Service
@RequiredArgsConstructor
public class PromotionAdminServiceImpl implements PromotionAdminService {

    private final PromotionRepository promotionRepository;
    private final PromotionMapper promotionMapper;

    @Override
    public Page<PromotionResponse> getPromotions(Pageable pageable) {
        return promotionRepository.findAll(pageable).map(this::toResponse);
    }

    @Override
    @Transactional
    public PromotionResponse createPromotion(PromotionRequest request) {
        validateTimeRange(request.getStartDate(), request.getEndDate());

        if (StringUtils.hasText(request.getCode()) && promotionRepository.findByCode(request.getCode().trim()).isPresent()) {
            throw new AppException(ErrorCode.INVALID_REQUEST, "Ma khuyen mai da ton tai");
        }

        Promotion promotion = new Promotion();
        applyFields(promotion, request, false);
        if (promotion.getUsedCount() == null) {
            promotion.setUsedCount(0);
        }
        if (promotion.getStatus() == null) {
            promotion.setStatus(1);
        }

        return toResponse(promotionRepository.save(promotion));
    }

    @Override
    @Transactional
    public PromotionResponse updatePromotionPartial(Long id, PromotionRequest request) {
        Promotion promotion = promotionRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.PROMOTION_NOT_FOUND));

        validateTimeRange(request.getStartDate(), request.getEndDate());

        if (StringUtils.hasText(request.getCode())) {
            String newCode = request.getCode().trim();
            promotionRepository.findByCode(newCode).ifPresent(existing -> {
                if (!existing.getPromotionId().equals(id)) {
                    throw new AppException(ErrorCode.INVALID_REQUEST, "Ma khuyen mai da ton tai");
                }
            });
        }

        applyFields(promotion, request, true);
        return toResponse(promotionRepository.save(promotion));
    }

    private void applyFields(Promotion promotion, PromotionRequest request, boolean partialUpdate) {
        if (!partialUpdate || StringUtils.hasText(request.getCode())) {
            promotion.setCode(StringUtils.hasText(request.getCode()) ? request.getCode().trim() : promotion.getCode());
        }
        if (!partialUpdate || request.getDiscountPercent() != null) {
            promotion.setDiscountPercent(request.getDiscountPercent());
        }
        if (!partialUpdate || request.getMaxDiscountAmount() != null) {
            promotion.setMaxDiscountAmount(request.getMaxDiscountAmount());
        }
        if (!partialUpdate || request.getMinOrderValue() != null) {
            promotion.setMinOrderValue(request.getMinOrderValue());
        }
        if (!partialUpdate || request.getUsageLimit() != null) {
            promotion.setUsageLimit(request.getUsageLimit());
        }
        if (!partialUpdate || request.getStartDate() != null) {
            promotion.setStartDate(request.getStartDate());
        }
        if (!partialUpdate || request.getEndDate() != null) {
            promotion.setEndDate(request.getEndDate());
        }
        if (!partialUpdate || request.getStatus() != null) {
            promotion.setStatus(request.getStatus());
        }
        if (!partialUpdate || StringUtils.hasText(request.getTitle())) {
            promotion.setTitle(request.getTitle());
        }
        if (!partialUpdate || StringUtils.hasText(request.getDescription())) {
            promotion.setDescription(request.getDescription());
        }
        if (!partialUpdate || (request.getApplicableCategories() != null && !request.getApplicableCategories().isEmpty())) {
            String categories = request.getApplicableCategories() == null || request.getApplicableCategories().isEmpty()
                    ? null
                    : String.join(",", request.getApplicableCategories());
            promotion.setApplicableCategories(categories);
        }
    }

    private void validateTimeRange(LocalDateTime from, LocalDateTime to) {
        if (from != null && to != null && to.isBefore(from)) {
            throw new AppException(ErrorCode.INVALID_REQUEST, "valid_to phai lon hon hoac bang valid_from");
        }
    }

    private PromotionResponse toResponse(Promotion entity) {
        return promotionMapper.toResponse(entity);
    }
}
