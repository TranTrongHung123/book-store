package com.ptit.backend.service;

import com.ptit.backend.dto.request.FlashSaleCampaignRequest;
import com.ptit.backend.dto.request.FlashSaleItemRequest;
import com.ptit.backend.dto.response.FlashSaleCampaignResponse;
import com.ptit.backend.dto.response.FlashSaleItemResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface FlashSaleAdminService {

    Page<FlashSaleCampaignResponse> getCampaigns(Pageable pageable);

    FlashSaleCampaignResponse createCampaign(FlashSaleCampaignRequest request);

    Page<FlashSaleItemResponse> getCampaignItems(Long campaignId, Pageable pageable);

    FlashSaleItemResponse addCampaignItem(Long campaignId, FlashSaleItemRequest request);
}
