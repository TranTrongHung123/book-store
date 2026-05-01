package com.ptit.backend.service.impl;

import com.ptit.backend.dto.request.FlashSaleCampaignRequest;
import com.ptit.backend.dto.request.FlashSaleItemRequest;
import com.ptit.backend.dto.response.FlashSaleCampaignResponse;
import com.ptit.backend.dto.response.FlashSaleItemResponse;
import com.ptit.backend.entity.Book;
import com.ptit.backend.entity.FlashSaleCampaign;
import com.ptit.backend.entity.FlashSaleItem;
import com.ptit.backend.exception.AppException;
import com.ptit.backend.exception.ErrorCode;
import com.ptit.backend.repository.BookRepository;
import com.ptit.backend.repository.FlashSaleCampaignRepository;
import com.ptit.backend.repository.FlashSaleItemRepository;
import com.ptit.backend.service.FlashSaleAdminService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

@Service
@RequiredArgsConstructor
public class FlashSaleAdminServiceImpl implements FlashSaleAdminService {

    private final FlashSaleCampaignRepository campaignRepository;
    private final FlashSaleItemRepository itemRepository;
    private final BookRepository bookRepository;

    @Override
    public Page<FlashSaleCampaignResponse> getCampaigns(Pageable pageable) {
        return campaignRepository.findAll(pageable).map(this::toCampaignResponse);
    }

    @Override
    @Transactional
    public FlashSaleCampaignResponse createCampaign(FlashSaleCampaignRequest request) {
        if (request.getEndsAt().isBefore(request.getStartsAt())) {
            throw new AppException(ErrorCode.INVALID_REQUEST, "ends_at phai lon hon hoac bang starts_at");
        }

        FlashSaleCampaign campaign = FlashSaleCampaign.builder()
                .name(request.getName().trim())
                .startTime(request.getStartsAt())
                .endTime(request.getEndsAt())
                .status(resolveStatus(request.getStatus()))
                .build();
        return toCampaignResponse(campaignRepository.save(campaign));
    }

    @Override
    public Page<FlashSaleItemResponse> getCampaignItems(Long campaignId, Pageable pageable) {
        ensureCampaignExists(campaignId);
        return itemRepository.findByCampaignCampaignId(campaignId, pageable).map(this::toItemResponse);
    }

    @Override
    @Transactional
    public FlashSaleItemResponse addCampaignItem(Long campaignId, FlashSaleItemRequest request) {
        FlashSaleCampaign campaign = campaignRepository.findById(campaignId)
                .orElseThrow(() -> new AppException(ErrorCode.INVALID_REQUEST, "Khong tim thay campaign"));

        Book book = bookRepository.findById(request.getBookId())
                .orElseThrow(() -> new AppException(ErrorCode.BOOK_NOT_FOUND));

        FlashSaleItem item = FlashSaleItem.builder()
                .campaign(campaign)
                .book(book)
                .flashSalePrice(request.getFlashPrice())
                .quantity(request.getFlashStock())
                .soldQuantity(0)
                .maxPerUser(request.getPurchaseLimit())
                .build();
        return toItemResponse(itemRepository.save(item));
    }

    @Override
    public void deleteCampaignItem(Long campaignId, Long itemId) {

    }

    @Override
    public void deleteCampaign(Long campaignId) {
        FlashSaleCampaign campaign = campaignRepository.findById(campaignId)
                .orElseThrow(() -> new AppException(ErrorCode.INVALID_REQUEST, "Không tìm thấy chiến dịch Flash Sale với ID: " + campaignId));

        // 2. Xóa chiến dịch
        // LƯU Ý MẠNH: Nếu trong Entity FlashSaleCampaign bạn có map OneToMany với `items`
        // và cài đặt `cascade = CascadeType.ALL` hoặc `orphanRemoval = true`,
        // thì khi xóa Campaign, toàn bộ các Item bên trong nó sẽ TỰ ĐỘNG BỊ XÓA THEO.
        campaignRepository.delete(campaign);
    }

    private void ensureCampaignExists(Long campaignId) {
        if (!campaignRepository.existsById(campaignId)) {
            throw new AppException(ErrorCode.INVALID_REQUEST, "Khong tim thay campaign");
        }
    }

    private String resolveStatus(String inputStatus) {
        if (!StringUtils.hasText(inputStatus)) {
            return "UPCOMING";
        }
        String value = inputStatus.trim().toUpperCase();
        if (!value.equals("UPCOMING") && !value.equals("ACTIVE") && !value.equals("ENDED")) {
            throw new AppException(ErrorCode.INVALID_REQUEST, "status phai la UPCOMING, ACTIVE hoac ENDED");
        }
        return value;
    }

    private FlashSaleCampaignResponse toCampaignResponse(FlashSaleCampaign campaign) {
        return FlashSaleCampaignResponse.builder()
                .id(campaign.getCampaignId())
                .name(campaign.getName())
                .startsAt(campaign.getStartTime())
                .endsAt(campaign.getEndTime())
                .build();
    }

    private FlashSaleItemResponse toItemResponse(FlashSaleItem item) {
        return FlashSaleItemResponse.builder()
                .id(item.getFlashSaleItemId())
                .campaignId(item.getCampaign().getCampaignId())
                .bookId(item.getBook().getBookId())
                .flashPrice(item.getFlashSalePrice())
                .flashStock(item.getQuantity())
                .purchaseLimit(item.getMaxPerUser())
                .build();
    }
}
