package com.ptit.backend.service;

import java.util.Map;

public interface FlashSaleStockRedisService {

    /**
     * Nạp tồn kho flash sale vào Redis khi chiến dịch bắt đầu.
     */
    void initializeStock(Long flashSaleItemId, int availableStock);

    /**
     * Giữ tồn kho nguyên tử bằng Lua script.
     * @return 1=thành công, -1=trùng yêu cầu, -2=vượt giới hạn, -3=hết hàng
     */
    long reserveStock(Long flashSaleItemId, Long userId, int quantity, int maxPerUser);

    /**
     * Hoàn tồn kho nguyên tử bằng Lua script.
     * @return 1=thành công, 0=lượt giữ hàng đã mất
     */
    long releaseStock(Long flashSaleItemId, Long userId, String reservationId, int quantity);

    /**
     * Lấy tồn kho còn lại từ Redis.
     */
    int getStock(Long flashSaleItemId);

    /**
     * Lưu reservation kèm TTL.
     */
    void setReservation(String reservationId, Map<String, String> data, long ttlSeconds);

    /**
     * Lấy dữ liệu reservation.
     */
    Map<String, String> getReservation(String reservationId);

    /**
     * Xóa reservation khi xác nhận đơn.
     */
    void deleteReservation(String reservationId);

    /**
     * Lấy TTL còn lại của reservation.
     */
    long getReservationTtl(String reservationId);

    /**
     * Dọn toàn bộ key Redis của một chiến dịch.
     */
    void cleanupCampaignKeys(Long campaignId, java.util.List<Long> flashSaleItemIds);

    /**
     * Giữ nguyên số lượng mua của người dùng sau khi xác nhận đơn.
     */
    void commitUserPurchase(Long flashSaleItemId, Long userId);

    /**
     * Lấy số lượng người dùng đã giữ/mua của một item.
     */
    int getUserPurchaseCount(Long flashSaleItemId, Long userId);
}
