package com.ptit.backend.repository;

import com.ptit.backend.entity.FlashSaleItem;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface FlashSaleItemRepository extends JpaRepository<FlashSaleItem, Long> {

    /**
     * Tìm Flash Sale item đang active cho một cuốn sách cụ thể.
     */
    @Query("""
            SELECT fsi FROM FlashSaleItem fsi
            JOIN fsi.campaign c
            WHERE fsi.book.bookId = :bookId
              AND c.status = 'ACTIVE'
              AND c.startTime <= CURRENT_TIMESTAMP
              AND c.endTime >= CURRENT_TIMESTAMP
              AND fsi.soldQuantity < fsi.quantity
            """)
    Optional<FlashSaleItem> findActiveFlashSaleByBookId(@Param("bookId") Long bookId);

    List<FlashSaleItem> findByCampaignCampaignId(Long campaignId);
}
