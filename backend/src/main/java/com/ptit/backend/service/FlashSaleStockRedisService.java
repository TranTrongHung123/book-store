package com.ptit.backend.service;

import java.util.Map;

public interface FlashSaleStockRedisService {

    /**
     * Initialize flash sale stock in Redis when campaign becomes active.
     */
    void initializeStock(Long flashSaleItemId, int availableStock);

    /**
     * Atomic stock reservation via Lua script.
     * @return 1=success, -1=duplicate, -2=max_per_user exceeded, -3=out of stock
     */
    long reserveStock(Long flashSaleItemId, Long userId, int quantity, int maxPerUser);

    /**
     * Atomic stock release via Lua script.
     * @return 1=success, 0=reservation already gone
     */
    long releaseStock(Long flashSaleItemId, Long userId, String reservationId, int quantity);

    /**
     * Get current remaining stock from Redis.
     */
    int getStock(Long flashSaleItemId);

    /**
     * Store reservation data with TTL.
     */
    void setReservation(String reservationId, Map<String, String> data, long ttlSeconds);

    /**
     * Get reservation data.
     */
    Map<String, String> getReservation(String reservationId);

    /**
     * Delete reservation (used on commit).
     */
    void deleteReservation(String reservationId);

    /**
     * Get TTL remaining on reservation.
     */
    long getReservationTtl(String reservationId);

    /**
     * Cleanup all Redis keys for a campaign.
     */
    void cleanupCampaignKeys(Long campaignId, java.util.List<Long> flashSaleItemIds);

    /**
     * Decrement user purchase count (used on commit to make count permanent).
     */
    void commitUserPurchase(Long flashSaleItemId, Long userId);

    /**
     * Get user purchase count for a specific flash sale item.
     */
    int getUserPurchaseCount(Long flashSaleItemId, Long userId);
}
