package com.ptit.backend.config;

import com.ptit.backend.entity.FlashSaleCampaign;
import com.ptit.backend.entity.FlashSaleItem;
import com.ptit.backend.repository.FlashSaleCampaignRepository;
import com.ptit.backend.repository.FlashSaleItemRepository;
import com.ptit.backend.service.FlashSaleStockRedisService;
import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * Automatically activates/deactivates flash sale campaigns based on start_time/end_time.
 * When a campaign activates, its stock is loaded into Redis.
 * When a campaign ends, Redis keys are cleaned up and sold_quantity is synced back.
 */
@Slf4j
@Component
@RequiredArgsConstructor
@EnableScheduling
public class FlashSaleCampaignScheduler {

    private final FlashSaleCampaignRepository campaignRepository;
    private final FlashSaleItemRepository flashSaleItemRepository;
    private final FlashSaleStockRedisService redisService;

    /**
     * Runs every 30 seconds to check campaign status transitions.
     */
    @Scheduled(fixedDelay = 30000)
    @Transactional
    public void checkCampaignTransitions() {
        LocalDateTime now = LocalDateTime.now();
        List<FlashSaleCampaign> allCampaigns = campaignRepository.findAll();

        for (FlashSaleCampaign campaign : allCampaigns) {
            String currentStatus = campaign.getStatus();

            if ("UPCOMING".equals(currentStatus) && !now.isBefore(campaign.getStartTime())) {
                // Transition: UPCOMING -> ACTIVE
                activateCampaign(campaign);
            } else if ("ACTIVE".equals(currentStatus) && now.isAfter(campaign.getEndTime())) {
                // Transition: ACTIVE -> ENDED
                endCampaign(campaign);
            }
        }
    }

    private void activateCampaign(FlashSaleCampaign campaign) {
        campaign.setStatus("ACTIVE");
        campaignRepository.save(campaign);

        // Load stock into Redis for each item
        List<FlashSaleItem> items = flashSaleItemRepository.findByCampaignCampaignId(campaign.getCampaignId());
        for (FlashSaleItem item : items) {
            int soldQty = item.getSoldQuantity() != null ? item.getSoldQuantity() : 0;
            int availableStock = item.getQuantity() - soldQty;
            redisService.initializeStock(item.getFlashSaleItemId(), Math.max(0, availableStock));
        }

        log.info("[CampaignScheduler] Activated campaign={} '{}', loaded {} items into Redis",
                campaign.getCampaignId(), campaign.getName(), items.size());
    }

    private void endCampaign(FlashSaleCampaign campaign) {
        campaign.setStatus("ENDED");
        campaignRepository.save(campaign);

        // Sync final stock back to MySQL and cleanup Redis
        List<FlashSaleItem> items = flashSaleItemRepository.findByCampaignCampaignId(campaign.getCampaignId());
        List<Long> itemIds = new java.util.ArrayList<>();

        for (FlashSaleItem item : items) {
            int remainingStock = redisService.getStock(item.getFlashSaleItemId());
            int totalQty = item.getQuantity();
            int finalSoldQty = totalQty - remainingStock;
            item.setSoldQuantity(Math.max(0, finalSoldQty));
            flashSaleItemRepository.save(item);
            itemIds.add(item.getFlashSaleItemId());
        }

        redisService.cleanupCampaignKeys(campaign.getCampaignId(), itemIds);

        log.info("[CampaignScheduler] Ended campaign={} '{}', synced {} items to MySQL",
                campaign.getCampaignId(), campaign.getName(), items.size());
    }
}
