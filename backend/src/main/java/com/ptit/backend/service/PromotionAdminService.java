package com.ptit.backend.service;

import com.ptit.backend.dto.request.PromotionRequest;
import com.ptit.backend.dto.response.PromotionResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface PromotionAdminService {

    Page<PromotionResponse> getPromotions(Pageable pageable);

    PromotionResponse createPromotion(PromotionRequest request);

    PromotionResponse updatePromotionPartial(Long id, PromotionRequest request);
}
